import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order, OrderEvent, EnrichedOrder } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private baseUrl = 'http://localhost:8085/api';

  constructor(private http: HttpClient) { }

  // Order Management API
  getOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.baseUrl}/orders`);
  }

  getOrderById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.baseUrl}/orders/${id}`);
  }

  createOrder(order: Order): Observable<Order> {
    console.log('Sending order to backend:', order);
    return this.http.post<Order>(`${this.baseUrl}/orders`, order);
  }

  updateOrder(id: number, order: Order): Observable<Order> {
    return this.http.put<Order>(`${this.baseUrl}/orders/${id}`, order);
  }

  deleteOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/orders/${id}`);
  }

  getOrdersByEmail(email: string): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.baseUrl}/orders/email/${email}`);
  }

  getOrdersByStatus(status: string): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.baseUrl}/orders/status/${status}`);
  }

  updateOrderStatus(id: number, status: string): Observable<Order> {
    return this.http.patch<Order>(`${this.baseUrl}/orders/${id}/status?status=${status}`, {});
  }

  // Kafka Order Events
  placeOrderEvent(orderEvent: OrderEvent): Observable<string> {
    return this.http.post<string>(`${this.baseUrl}/orders/place-order`, orderEvent);
  }

  // Enriched Orders (MongoDB)
  getEnrichedOrdersByUserId(userId: string): Observable<EnrichedOrder[]> {
    return this.http.get<EnrichedOrder[]>(`${this.baseUrl}/enriched-orders/${userId}`);
  }

  // Health check
  healthCheck(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/orders/health`);
  }
} 