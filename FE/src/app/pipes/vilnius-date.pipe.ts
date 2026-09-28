import { Pipe, PipeTransform } from '@angular/core';

const VILNIUS_FORMATTER = new Intl.DateTimeFormat('en-GB', {
  timeZone: 'Europe/Vilnius',
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
  hour12: false
});

@Pipe({
  name: 'vilniusDate'
})
export class VilniusDatePipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    if (!value) {
      return '';
    }
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return '';
    }
    return VILNIUS_FORMATTER.format(date).replace(',', '');
  }
}
