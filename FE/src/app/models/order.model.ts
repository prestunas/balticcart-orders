export type OrderStatus = 'NEW' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export const ORDER_STATUSES: OrderStatus[] = ['NEW', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

export interface Order {
  id: number;
  orderNumber: string;
  customerName: string;
  createdAt: string;
  status: OrderStatus;
  needsAttention: boolean;
}
