import { Component, OnInit } from '@angular/core';

import { Order, ORDER_STATUSES, OrderStatus } from '../models/order.model';
import { OrdersService } from '../services/orders.service';

type StatusFilter = OrderStatus | 'ALL';

@Component({
  selector: 'app-orders-dashboard',
  templateUrl: './orders-dashboard.component.html',
  styleUrls: ['./orders-dashboard.component.scss']
})
export class OrdersDashboardComponent implements OnInit {
  readonly statuses = ORDER_STATUSES;

  orders: Order[] = [];
  filteredOrders: Order[] = [];

  loading = false;
  loadError = false;

  searchTerm = '';
  statusFilter: StatusFilter = 'ALL';

  constructor(private readonly ordersService: OrdersService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.loadError = false;

    this.ordersService.getOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
        this.applyFilters();
        this.loading = false;
      },
      error: () => {
        this.orders = [];
        this.filteredOrders = [];
        this.loadError = true;
        this.loading = false;
      }
    });
  }

  onSearchTermChange(value: string): void {
    this.searchTerm = value;
    this.applyFilters();
  }

  onStatusFilterChange(value: string): void {
    this.statusFilter = value as StatusFilter;
    this.applyFilters();
  }

  private applyFilters(): void {
    const term = this.searchTerm.trim().toLowerCase();

    this.filteredOrders = this.orders.filter((order) => {
      const matchesTerm =
        !term ||
        order.orderNumber.toLowerCase().includes(term) ||
        order.customerName.toLowerCase().includes(term);
      const matchesStatus = this.statusFilter === 'ALL' || order.status === this.statusFilter;

      return matchesTerm && matchesStatus;
    });
  }
}
