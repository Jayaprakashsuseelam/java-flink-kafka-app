import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../services/order.service';
import { EnrichedOrder } from '../../models/order.model';

@Component({
  selector: 'app-enriched-order-list',
  templateUrl: './enriched-order-list.component.html',
  styleUrls: ['./enriched-order-list.component.css']
})
export class EnrichedOrderListComponent implements OnInit {
  enrichedOrders: EnrichedOrder[] = [];
  loading = false;
  error = '';
  searchUserId = '';

  constructor(private orderService: OrderService) { }

  ngOnInit(): void {
    // Load some sample data or show search form
  }

  searchEnrichedOrders(): void {
    if (!this.searchUserId.trim()) {
      this.error = 'Please enter a user ID to search';
      return;
    }

    this.loading = true;
    this.error = '';
    
    this.orderService.getEnrichedOrdersByUserId(this.searchUserId).subscribe({
      next: (orders) => {
        this.enrichedOrders = orders;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error loading enriched orders';
        this.loading = false;
        console.error('Enriched orders error:', error);
      }
    });
  }

  clearResults(): void {
    this.enrichedOrders = [];
    this.searchUserId = '';
    this.error = '';
  }
} 