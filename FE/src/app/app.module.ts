import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { OrdersDashboardComponent } from './orders-dashboard/orders-dashboard.component';
import { VilniusDatePipe } from './pipes/vilnius-date.pipe';

@NgModule({
  declarations: [
    AppComponent,
    OrdersDashboardComponent,
    VilniusDatePipe
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
