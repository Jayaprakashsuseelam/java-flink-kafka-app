export interface Order {
  id?: number;
  customerName: string;
  customerEmail: string;
  shippingAddress: string;
  status: OrderStatus;
  orderDate: string;
  totalAmount: number;
  items: OrderItem[];
}

export interface OrderItem {
  id?: number;
  productName: string;
  productCode: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  PROCESSED = 'PROCESSED',
  CANCELLED = 'CANCELLED'
}

export interface OrderEvent {
  orderId: string;
  userId: string;
  itemId: string;
  quantity: number;
  orderTime: string;
}

export interface EnrichedOrder {
  orderId: string;
  userId: string;
  itemId: string;
  quantity: number;
  orderTime: string;
  customerName: string;
  itemName: string;
  itemPrice: number;
  totalAmount: number;
  orderStatus: string;
  processedTime: string;
} 