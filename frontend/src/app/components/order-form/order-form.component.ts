import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { Order, OrderItem, OrderStatus } from '../../models/order.model';

@Component({
  selector: 'app-order-form',
  templateUrl: './order-form.component.html',
  styleUrls: ['./order-form.component.css']
})
export class OrderFormComponent implements OnInit {
  orderForm: FormGroup;
  loading = false;
  error = '';
  isEditMode = false;
  orderId: number = 0;
  
  // Status options
  statusOptions = Object.values(OrderStatus);

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService
  ) {
    this.orderForm = this.createForm();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.orderId = +params['id'];
        this.isEditMode = true;
        this.loadOrder();
      } else {
        // Add initial item for new order
        this.addItem();
      }
    });
  }

  createForm(): FormGroup {
    return this.fb.group({
      customerName: ['', [Validators.required, Validators.minLength(2)]],
      customerEmail: ['', [Validators.required, Validators.email]],
      shippingAddress: ['', [Validators.required, Validators.minLength(10)]],
      status: ['PENDING', Validators.required],
      orderDate: [new Date().toISOString().slice(0, 16), Validators.required],
      items: this.fb.array([])
    });
  }

  get items(): FormArray {
    return this.orderForm.get('items') as FormArray;
  }

  loadOrder(): void {
    this.loading = true;
    this.error = '';
    
    this.orderService.getOrderById(this.orderId).subscribe({
      next: (order) => {
        this.populateForm(order);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error loading order: ' + (err.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  populateForm(order: Order): void {
    this.orderForm.patchValue({
      customerName: order.customerName,
      customerEmail: order.customerEmail,
      shippingAddress: order.shippingAddress,
      status: order.status,
      orderDate: order.orderDate ? new Date(order.orderDate).toISOString().slice(0, 16) : ''
    });

    // Clear existing items
    while (this.items.length !== 0) {
      this.items.removeAt(0);
    }

    // Add order items
    if (order.items && order.items.length > 0) {
      order.items.forEach(item => {
        this.addItem(item);
      });
    } else {
      this.addItem();
    }
  }

  addItem(item?: OrderItem): void {
    const itemForm = this.fb.group({
      productName: [item?.productName || '', [Validators.required, Validators.minLength(2)]],
      productCode: [item?.productCode || '', [Validators.required, Validators.minLength(2)]],
      quantity: [item?.quantity || 1, [Validators.required, Validators.min(1)]],
      unitPrice: [item?.unitPrice || 0, [Validators.required, Validators.min(0)]],
      subtotal: [item?.subtotal || 0]
    });

    // Calculate subtotal when quantity or unit price changes
    itemForm.get('quantity')?.valueChanges.subscribe(() => this.calculateSubtotal(itemForm));
    itemForm.get('unitPrice')?.valueChanges.subscribe(() => this.calculateSubtotal(itemForm));

    this.items.push(itemForm);
  }

  removeItem(index: number): void {
    if (this.items.length > 1) {
      this.items.removeAt(index);
      this.calculateTotal();
    }
  }

  calculateSubtotal(itemForm: FormGroup): void {
    const quantity = itemForm.get('quantity')?.value || 0;
    const unitPrice = itemForm.get('unitPrice')?.value || 0;
    const subtotal = quantity * unitPrice;
    itemForm.patchValue({ subtotal: subtotal });
    this.calculateTotal();
  }

  calculateTotal(): number {
    let total = 0;
    this.items.controls.forEach(control => {
      total += control.get('subtotal')?.value || 0;
    });
    return total;
  }

  onSubmit(): void {
    if (this.orderForm.valid) {
      this.loading = true;
      this.error = '';

      const formValue = this.orderForm.value;
      const order: Order = {
        ...formValue,
        orderDate: new Date(formValue.orderDate).toISOString(),
        totalAmount: this.calculateTotal(),
        items: formValue.items.map((item: any) => ({
          ...item,
          subtotal: item.quantity * item.unitPrice
        }))
      };

      if (this.isEditMode) {
        this.orderService.updateOrder(this.orderId, order).subscribe({
          next: () => {
            this.router.navigate(['/orders', this.orderId]);
          },
          error: (err) => {
            this.error = 'Error updating order: ' + (err.message || 'Unknown error');
            this.loading = false;
          }
        });
      } else {
        this.orderService.createOrder(order).subscribe({
          next: (createdOrder) => {
            this.router.navigate(['/orders', createdOrder.id]);
          },
                  error: (err) => {
          console.error('Error creating order:', err);
          this.error = 'Error creating order: ' + (err.message || 'Unknown error');
          this.loading = false;
        }
        });
      }
    } else {
      this.markFormGroupTouched();
    }
  }

  markFormGroupTouched(): void {
    Object.keys(this.orderForm.controls).forEach(key => {
      const control = this.orderForm.get(key);
      control?.markAsTouched();

      if (control instanceof FormArray) {
        (control as FormArray).controls.forEach(group => {
          Object.keys((group as FormGroup).controls).forEach(subKey => {
            (group as FormGroup).get(subKey)?.markAsTouched();
          });
        });
      }
    });
  }

  cancel(): void {
    if (this.isEditMode) {
      this.router.navigate(['/orders', this.orderId]);
    } else {
      this.router.navigate(['/orders']);
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.orderForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  isItemFieldInvalid(index: number, fieldName: string): boolean {
    const item = this.items.at(index);
    const field = item.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldError(fieldName: string): string {
    const field = this.orderForm.get(fieldName);
    if (field?.errors) {
      if (field.errors['required']) return `${fieldName} is required`;
      if (field.errors['email']) return 'Please enter a valid email address';
      if (field.errors['minlength']) return `${fieldName} must be at least ${field.errors['minlength'].requiredLength} characters`;
      if (field.errors['min']) return `${fieldName} must be at least ${field.errors['min'].min}`;
    }
    return '';
  }

  getItemFieldError(index: number, fieldName: string): string {
    const item = this.items.at(index);
    const field = item.get(fieldName);
    if (field?.errors) {
      if (field.errors['required']) return `${fieldName} is required`;
      if (field.errors['minlength']) return `${fieldName} must be at least ${field.errors['minlength'].requiredLength} characters`;
      if (field.errors['min']) return `${fieldName} must be at least ${field.errors['min'].min}`;
    }
    return '';
  }
} 