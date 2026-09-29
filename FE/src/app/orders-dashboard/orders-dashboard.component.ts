import { Component, OnInit } from '@angular/core';

import { Order, ORDER_STATUSES, OrderStatus } from '../models/order.model';
import { OrdersService } from '../services/orders.service';

type StatusFilter = OrderStatus | 'ALL';
type SortColumn = 'orderNumber' | 'customerName' | 'createdAt' | 'status';

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
  attentionOnly = false;

  sortColumn: SortColumn | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';

  constructor(private readonly ordersService: OrdersService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  get attentionCount(): number {
    return this.orders.filter((o) => o.needsAttention).length;
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

  onAttentionOnlyChange(value: boolean): void {
    this.attentionOnly = value;
    this.applyFilters();
  }

  sortOrders(column: SortColumn): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.applySort();
  }

  private applyFilters(): void {
    const term = this.searchTerm.trim().toLowerCase();

    this.filteredOrders = this.orders.filter((order) => {
      const matchesTerm =
        !term ||
        order.orderNumber.toLowerCase().includes(term) ||
        order.customerName.toLowerCase().includes(term);
      const matchesStatus = this.statusFilter === 'ALL' || order.status === this.statusFilter;
      const matchesAttention = !this.attentionOnly || order.needsAttention;

      return matchesTerm && matchesStatus && matchesAttention;
    });

    this.applySort();
  }

  private applySort(): void {
    if (!this.sortColumn) {
      return;
    }

    const col = this.sortColumn;
    const dir = this.sortDirection === 'asc' ? 1 : -1;

    this.filteredOrders = [...this.filteredOrders].sort((a, b) => {
      if (col === 'createdAt') {
        return (Date.parse(a.createdAt) - Date.parse(b.createdAt)) * dir;
      }
      const aVal = a[col] as string;
      const bVal = b[col] as string;
      return aVal.localeCompare(bVal) * dir;
    });
  }
}
