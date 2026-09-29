# Orders "Needs Attention" Feature — Implementation Plan

## Top-Level Overview

The orders team needs to identify orders that have been waiting too long
(more than 24 hours, status NEW or PROCESSING) and sort by oldest first.

The implementation adds a `needsAttention` boolean to the API response
(computed at the backend, returned per order), then builds on the existing
in-memory filter pipeline in the Angular dashboard to add an attention
counter, a row highlight, a toggle filter, and sortable column headers.

**No new dependencies, no new endpoints, no BE filtering parameter.**
The existing `GET /api/orders` endpoint is extended; all filtering and
sorting remain in-memory in the Angular component, consistent with how
search and status filtering already work.

**Scope:** 6 files change total — 2 BE, 4 FE.

---

## Sub-Task 1 — Extend the API response with `needsAttention`

**Status:** [x] done

### Intent
Compute whether each order needs attention entirely on the backend so the
frontend receives a plain boolean and does not have to re-implement the
rule. The rule is: `status IN (NEW, PROCESSING)` AND
`Instant.now().isAfter(createdAt.plus(24, HOURS))`.  
The `>` (strictly greater than) comparison using `isAfter` satisfies the
"exactly 24 hours does not qualify" requirement.

The backend is the right place for this calculation because:
- `Instant` arithmetic is reliable and timezone-independent.
- The frontend's `Date` arithmetic would be correct too, but separating
  the rule from the display logic keeps the component simple and makes
  the rule easier to change or test later.
- It avoids duplicating the business rule across both tiers.

### Expected Outcomes
- `GET /api/orders` returns every order object with a `needsAttention`
  boolean field.
- Orders with status SHIPPED, DELIVERED, or CANCELLED always return
  `needsAttention: false`.
- An order with status NEW/PROCESSING created exactly 24 hours ago
  returns `false`; one created 24 hours and 1 second ago returns `true`.

### Todo List
1. Add a `needsAttention(Instant now)` method to `OrderResponse.java`
   (or add the field directly to the record's static factory method)
   that evaluates `status IN (NEW, PROCESSING) && now.isAfter(createdAt.plus(24, HOURS))`.
2. Update `OrderResponse.from(CustomerOrder order)` to pass
   `Instant.now()` and set the new boolean field.
3. Add `needsAttention` as a component of the `OrderResponse` record.

### Relevant Context
- [`OrderResponse.java`](BE/src/main/java/BalticCart/Orders/order/OrderResponse.java)
  — Java record with static factory `from(CustomerOrder)`.
- [`OrderStatus.java`](BE/src/main/java/BalticCart/Orders/order/OrderStatus.java)
  — enum values: `NEW`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`.
- [`CustomerOrder.java`](BE/src/main/java/BalticCart/Orders/order/CustomerOrder.java)
  — entity; `getCreatedAt()` returns `java.time.Instant`.
- The controller calls `OrderResponse::from` as a stream mapper; no
  change needed there.
- Existing test [`OrderControllerTest.java`](BE/src/test/java/BalticCart/Orders/order/OrderControllerTest.java)
  checks response size and sort order; the new field does not break it,
  but the test should be updated to assert `needsAttention` values on at
  least one order older than 24h and one younger.

---

## Sub-Task 2 — Update the Angular Order model

**Status:** [x] done

### Intent
Add `needsAttention: boolean` to the `Order` interface so TypeScript
knows about the new field throughout the app.

### Expected Outcomes
- `Order.needsAttention` is typed as `boolean`.
- No compile errors in the component or template.

### Todo List
1. Add `needsAttention: boolean;` to the `Order` interface in
   [`order.model.ts`](FE/src/app/models/order.model.ts).

### Relevant Context
- [`order.model.ts`](FE/src/app/models/order.model.ts) — single file;
  the existing `Order` interface maps directly to `OrderResponse` fields.

---

## Sub-Task 3 — Add attention state, filter, and sorting to the component

**Status:** [x] done

### Intent
Extend `OrdersDashboardComponent` with:
- A computed `attentionCount` (count of orders in `this.orders` where
  `needsAttention === true` — always based on the full unfiltered list
  so the badge reflects reality regardless of active filters).
- An `attentionOnly` boolean toggle; when true, `applyFilters()` adds a
  third AND condition: `order.needsAttention === true`.
- Column sorting state: a `sortColumn` (one of `'orderNumber'`,
  `'customerName'`, `'createdAt'`, `'status'`) and a `sortDirection`
  (`'asc'` | `'desc'`).
- A `sortOrders(column)` method that toggles direction if the same
  column is clicked again, then sorts `filteredOrders` in-place using
  the chosen key. For `createdAt`, compare using `Date.parse(a.createdAt)` (milliseconds) — never lexicographic string comparison.
- `applyFilters()` now also calls sort at the end, so search, status,
  and attention filters always preserve the current sort.

### Expected Outcomes
- `attentionCount` is correct after load and after refresh.
- Toggling `attentionOnly` re-runs all three filters together.
- Clicking Order #, Customer, Created, or Status header sorts
  `filteredOrders` in the chosen direction.
- Clicking the same header twice reverses the direction.
- Sorting by Created compares timestamps numerically so oldest-first is
  correct even when date strings would sort differently.

### Todo List
1. Add `attentionOnly = false` and `sortColumn` / `sortDirection` state
   fields.
2. Add a getter `get attentionCount(): number` that counts attention
   orders from `this.orders`.
3. Add `onAttentionOnlyChange(value: boolean)` handler that updates
   `attentionOnly` and calls `applyFilters()`.
4. Add `sortOrders(column: SortColumn)` that sets/toggles state then
   sorts `filteredOrders`.
5. Update `applyFilters()` to (a) apply the three filters in order and
   (b) call `sortOrders` logic at the end to preserve sort after every
   filter update.

### Relevant Context
- [`orders-dashboard.component.ts`](FE/src/app/orders-dashboard/orders-dashboard.component.ts)
  — existing `applyFilters()` pattern to follow exactly.
- Sort key union type: `type SortColumn = 'orderNumber' | 'customerName' | 'createdAt' | 'status'`.
- `sortDirection` initial value: `'asc'` (or no sort until user clicks).
  Start with no sort (match current backend order) until a column header
  is clicked; `sortColumn` starts as `null`.

---

## Sub-Task 4 — Update the dashboard template and styles

**Status:** [x] done

### Intent
Wire the new component state into the HTML and SCSS:
1. **Attention counter badge** in the toolbar summary area, showing the
   count in a styled indicator (orange, consistent with the brand accent
   colour `#ffb020`). Show the badge only when `attentionCount > 0`.
2. **Attention-only toggle** — a checkbox or toggle control in
   `.toolbar__controls` alongside search and status filter, disabled
   when loading or error, labelled clearly (e.g. "Needs attention only").
3. **Row highlight** — add a modifier class (e.g.
   `orders-table__row--attention`) to `<tr>` rows where
   `order.needsAttention`. Style with a left-border accent or subtle
   background tint (amber/orange family, distinct from status-badge
   colours).
4. **Sortable column headers** — add a `(click)` handler on each of the
   four `<th>` elements. Display a sort-direction indicator (▲/▼ or
   CSS chevron) on the active column. Style the cursor as `pointer`.
5. **Attention empty state** — the existing "No matching orders" state
   panel text is enough, but update its hint text to mention the
   attention filter if all three filters together produce zero results.
   Keep this minimal — one sentence addition is sufficient.

### Expected Outcomes
- A visible count of attention orders appears in the toolbar when
  `attentionCount > 0`.
- A "Needs attention" control appears in the filter controls row.
- Rows needing attention are visually distinguished from others.
- All four column headers are clickable; the active sort column shows a
  direction indicator.
- The mobile responsive layout is preserved (all new controls follow the
  existing flex-wrap pattern).
- Existing loading, error, and empty states still render correctly.

### Todo List
1. Add attention badge HTML next to the count summary in the toolbar.
2. Add a checkbox field (styled like `.field`) for "Needs attention"
   bound to `attentionOnly` with `(ngModelChange)="onAttentionOnlyChange($event)"`.
3. Add `[ngClass]` on table `<tr>` to apply the attention row class.
4. Add `(click)` and sort indicator markup on the four `<th>` elements.
5. Add SCSS rules for `.attention-badge`, `.orders-table__row--attention`,
   and sortable-header cursor/indicator styles.

### Relevant Context
- [`orders-dashboard.component.html`](FE/src/app/orders-dashboard/orders-dashboard.component.html)
  — existing toolbar and table structure to extend.
- [`orders-dashboard.component.scss`](FE/src/app/orders-dashboard/orders-dashboard.component.scss)
  — existing colour palette; use `#ffb020` (brand amber) for attention
  accents; row highlight should be subtle (e.g. `#fff7e6` background
  with a `3px solid #ffb020` left border).
- The mobile media query at `max-width: 640px` needs a rule ensuring
  the new attention field stretches full-width like the other fields.

---

## Files Changed Summary

| File | Change |
|------|--------|
| `BE/src/main/java/BalticCart/Orders/order/OrderResponse.java` | Add `needsAttention` field + computation in `from()` |
| `BE/src/test/java/BalticCart/Orders/order/OrderControllerTest.java` | Assert `needsAttention` values |
| `FE/src/app/models/order.model.ts` | Add `needsAttention: boolean` to interface |
| `FE/src/app/orders-dashboard/orders-dashboard.component.ts` | Attention count, toggle, sort logic |
| `FE/src/app/orders-dashboard/orders-dashboard.component.html` | Badge, toggle control, row class, sortable headers |
| `FE/src/app/orders-dashboard/orders-dashboard.component.scss` | Attention badge, row highlight, header cursor/indicator |

---

## Where the 24-hour rule lives and why

The rule is computed in `OrderResponse.from()` on the backend. The entity
`createdAt` is an `Instant`; `Instant.now().isAfter(createdAt.plus(24, HOURS))`
is exact, timezone-independent arithmetic. The frontend receives a boolean
and does not need to reproduce the rule. This avoids drift if the threshold
ever changes and keeps the Angular component focused on display logic.

---

## Whether the existing API response needs to change

Yes — one additive, backward-compatible field: `needsAttention: boolean`
is appended to every `OrderResponse` object. The endpoint URL, HTTP method,
query parameters, and all existing fields remain unchanged. Old clients
that ignore unknown JSON fields are unaffected.

---

## How attention count, filters, and sorting work together

```
this.orders (full list, never filtered)
      │
      ▼
  attentionCount = this.orders.filter(o => o.needsAttention).length
      │
      ▼
applyFilters() pipeline (called after any control change):
  1. searchTerm substring match (orderNumber, customerName)
  2. statusFilter exact match (or ALL)
  3. attentionOnly boolean gate (skipped when false)
  → produces filteredOrders[]
  4. sortOrders() applied to filteredOrders (no-op until user clicks a header)
      │
      ▼
  filteredOrders → rendered in table
```

All three filters are ANDed. `attentionCount` always reads the unfiltered
`this.orders` list so the badge count never changes when the user narrows
the table.

---

## Manual Verification Checklist

After the implementation is complete, verify the following manually in the
running application (BE on `localhost:8080`, FE on `localhost:4200`):

### 1. Attention count
- Open the dashboard. The attention badge should show a non-zero count
  (the seeder creates many NEW/PROCESSING orders >24h old).
- Expected attention orders include: BC-2018 (30h, PROCESSING),
  BC-2019 (32h, NEW), BC-2021 (36h, PROCESSING), BC-2022 (38h, NEW),
  BC-2024 (42h, PROCESSING), BC-2025 (44h, NEW), BC-2027 (47h, PROCESSING),
  BC-2028 (48h, NEW), BC-2029 (49h, PROCESSING), BC-2030 (50h, NEW),
  BC-2032 (56h, PROCESSING), BC-2033 (59h, NEW), BC-2035 (65h, PROCESSING),
  BC-2037 (71h, NEW), BC-2039 (77h, PROCESSING), BC-2041 (84h, NEW),
  BC-2043 (92h, PROCESSING), BC-2046 (108h, NEW), BC-2048 (120h, PROCESSING),
  BC-2052 (148h, NEW), BC-2054 (168h, PROCESSING), BC-2058 (240h, NEW),
  BC-2060 (300h, PROCESSING).

### 2. Exactly-24-hours boundary
- BC-2015 (Emilija Paulauskaite) was seeded at exactly 24 hours ago
  (now truncated to hour − 24h + some minutes). It has status NEW.
  It must NOT be highlighted and must NOT appear when "Needs attention"
  is toggled on.

### 3. Just-over-24-hours boundary
- BC-2016 (Nojus Stankevicius) is 26h old but CANCELLED. It must NOT
  be highlighted regardless of age.
- BC-2018 (Oliver Brown) is 30h old and PROCESSING. It MUST be
  highlighted and appear in the attention filter.

### 4. Older SHIPPED order does not qualify
- BC-2031 (Martynas Jasiunas) is 53h old with status SHIPPED. The row
  must NOT be highlighted and must NOT appear in the attention filter.
- BC-2057 (Lina Kavaliauskiene) is 216h old with status SHIPPED. Same.

### 5. Attention toggle interacts with other filters
- Type "Brown" in search. With attention filter off, BC-2018 appears.
  Toggle attention on → BC-2018 still appears (satisfies all three filters).
  Switch status filter to SHIPPED → BC-2018 disappears (status mismatch).

### 6. Sorting
- Click "Created" header → oldest first (BC-2060 at top of table).
  Click again → newest first (BC-2001 at top of table).
- Enable attention filter, then sort by Created ascending → BC-2018
  (30h, oldest attention order) appears near the top.
- Click "Order #" header → alphabetical/lexicographic by order number.
- Click "Status" header → alphabetical by status string.

### 7. Responsive layout
- Narrow browser window below 640px. All new controls (attention badge,
  attention toggle) must stack vertically and not overflow.
