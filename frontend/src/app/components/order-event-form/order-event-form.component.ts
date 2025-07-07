import { Component } from '@angular/core';
import { OrderService } from '../../services/order.service';
import { OrderEvent } from '../../models/order.model';

@Component({
  selector: 'app-order-event-form',
  templateUrl: './order-event-form.component.html',
  styleUrls: ['./order-event-form.component.css']
})
export class OrderEventFormComponent {
  orderEvent: OrderEvent = {
    orderId: '',
    userId: '',
    itemId: '',
    quantity: 1,
    orderTime: new Date().toISOString()
  };

  loading = false;
  success = false;
  error = '';

  constructor(private orderService: OrderService) { }

  submitOrderEvent(): void {
    if (!this.validateForm()) {
      return;
    }

    this.loading = true;
    this.success = false;
    this.error = '';

    this.orderService.placeOrderEvent(this.orderEvent).subscribe({
      next: (response) => {
        this.success = true;
        this.loading = false;
        this.resetForm();
      },
      error: (error) => {
        this.error = 'Error sending order event';
        this.loading = false;
        console.error('Order event error:', error);
      }
    });
  }

  validateForm(): boolean {
    if (!this.orderEvent.orderId.trim()) {
      this.error = 'Order ID is required';
      return false;
    }
    if (!this.orderEvent.userId.trim()) {
      this.error = 'User ID is required';
      return false;
    }
    if (!this.orderEvent.itemId.trim()) {
      this.error = 'Item ID is required';
      return false;
    }
    if (this.orderEvent.quantity <= 0) {
      this.error = 'Quantity must be greater than 0';
      return false;
    }
    this.error = '';
    return true;
  }

  resetForm(): void {
    this.orderEvent = {
      orderId: '',
      userId: '',
      itemId: '',
      quantity: 1,
      orderTime: new Date().toISOString()
    };
  }

  generateSampleData(): void {
    this.orderEvent = {
      orderId: 'order_' + Date.now(),
      userId: 'user_' + Math.floor(Math.random() * 1000),
      itemId: 'item_' + Math.floor(Math.random() * 100),
      quantity: Math.floor(Math.random() * 5) + 1,
      orderTime: new Date().toISOString()
    };
  }
} 