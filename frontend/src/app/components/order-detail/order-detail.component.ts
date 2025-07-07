import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { Order, OrderStatus } from '../../models/order.model';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.css']
})
export class OrderDetailComponent implements OnInit {
  order: Order | null = null;
  loading = false;
  error = '';
  orderId: number = 0;
  
  // Status options
  statusOptions = Object.values(OrderStatus);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.orderId = +params['id'];
      this.loadOrder();
    });
  }

  loadOrder(): void {
    this.loading = true;
    this.error = '';
    
    this.orderService.getOrderById(this.orderId).subscribe({
      next: (order) => {
        this.order = order;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error loading order: ' + (err.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  updateOrderStatus(event: Event): void {
    if (!this.order) return;
    
    const target = event.target as HTMLSelectElement;
    const newStatus = target.value;
    
    this.orderService.updateOrderStatus(this.orderId, newStatus).subscribe({
      next: (updatedOrder) => {
        this.order = updatedOrder;
      },
      error: (err) => {
        this.error = 'Error updating order status: ' + (err.message || 'Unknown error');
      }
    });
  }

  deleteOrder(): void {
    if (!this.order) return;
    
    if (confirm('Are you sure you want to delete this order? This action cannot be undone.')) {
      this.orderService.deleteOrder(this.orderId).subscribe({
        next: () => {
          this.router.navigate(['/orders']);
        },
        error: (err) => {
          this.error = 'Error deleting order: ' + (err.message || 'Unknown error');
        }
      });
    }
  }

  editOrder(): void {
    this.router.navigate(['/orders', this.orderId, 'edit']);
  }

  goBack(): void {
    this.router.navigate(['/orders']);
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'PENDING': return 'status-pending';
      case 'PROCESSED': return 'status-processed';
      case 'CANCELLED': return 'status-cancelled';
      default: return 'status-default';
    }
  }

  getTotalItems(): number {
    return this.order?.items?.length || 0;
  }

  getSubtotal(): number {
    if (!this.order?.items) return 0;
    return this.order.items.reduce((sum, item) => sum + item.subtotal, 0);
  }
} 