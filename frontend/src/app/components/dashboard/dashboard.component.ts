import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../services/order.service';
import { Order, EnrichedOrder } from '../../models/order.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  orders: Order[] = [];
  enrichedOrders: EnrichedOrder[] = [];
  loading = true;
  error = '';

  constructor(private orderService: OrderService) { }

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.loading = true;
    this.error = '';
    
    console.log('Loading dashboard data from:', 'http://localhost:8085/api/orders');
    
    this.orderService.getOrders().subscribe({
      next: (orders) => {
        console.log('Dashboard data loaded successfully:', orders);
        this.orders = orders;
        this.loading = false;
      },
      error: (error) => {
        console.error('Dashboard error details:', error);
        this.error = `Error loading dashboard data: ${error.status} - ${error.message || 'Unknown error'}`;
        this.loading = false;
      }
    });
  }

  getStatusCount(status: string): number {
    return this.orders.filter(order => order.status === status).length;
  }

  getTotalOrders(): number {
    return this.orders.length;
  }

  getTotalAmount(): number {
    return this.orders.reduce((sum, order) => sum + order.totalAmount, 0);
  }
} 