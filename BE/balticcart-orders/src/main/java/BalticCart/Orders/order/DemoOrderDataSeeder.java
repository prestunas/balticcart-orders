package BalticCart.Orders.order;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.time.Instant;
import java.util.List;

@Component
public class DemoOrderDataSeeder implements CommandLineRunner {

    private final OrderRepository orderRepository;

    public DemoOrderDataSeeder(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        if (orderRepository.count() > 0) {
            return;
        }

        Instant now = Instant.now();

        List<CustomerOrder> demoOrders = List.of(
                order("BC-2001", "Elena Kovalenko", now, 1, OrderStatus.NEW),
                order("BC-2002", "Marius Petraitis", now, 2, OrderStatus.NEW),
                order("BC-2003", "Austeja Jankauskaite", now, 3, OrderStatus.PROCESSING),
                order("BC-2004", "Tomas Zvirblis", now, 4, OrderStatus.NEW),
                order("BC-2005", "Laura Novakova", now, 5, OrderStatus.PROCESSING),
                order("BC-2006", "Andrius Butkus", now, 6, OrderStatus.NEW),
                order("BC-2007", "Greta Lindqvist", now, 8, OrderStatus.SHIPPED),
                order("BC-2008", "Viktor Sokolov", now, 10, OrderStatus.PROCESSING),
                order("BC-2009", "Ona Meskauskaite", now, 12, OrderStatus.NEW),
                order("BC-2010", "Dovydas Kazlauskas", now, 14, OrderStatus.SHIPPED),

                order("BC-2011", "Milda Jankauskaite", now, 16, OrderStatus.PROCESSING),
                order("BC-2012", "Rokas Adomaitis", now, 18, OrderStatus.NEW),
                order("BC-2013", "Sofia Martin", now, 20, OrderStatus.DELIVERED),
                order("BC-2014", "Lukas Petrauskas", now, 22, OrderStatus.PROCESSING),
                order("BC-2015", "Emilija Paulauskaite", now, 24, OrderStatus.NEW),
                order("BC-2016", "Nojus Stankevicius", now, 26, OrderStatus.CANCELLED),
                order("BC-2017", "Karolina Vaitkute", now, 28, OrderStatus.SHIPPED),
                order("BC-2018", "Oliver Brown", now, 30, OrderStatus.PROCESSING),
                order("BC-2019", "Ieva Balciunaite", now, 32, OrderStatus.NEW),
                order("BC-2020", "Dominykas Urbonas", now, 34, OrderStatus.DELIVERED),

                order("BC-2021", "Gabija Rimkute", now, 36, OrderStatus.PROCESSING),
                order("BC-2022", "Arnas Mikalauskas", now, 38, OrderStatus.NEW),
                order("BC-2023", "Mia Anderson", now, 40, OrderStatus.SHIPPED),
                order("BC-2024", "Paulius Navickas", now, 42, OrderStatus.PROCESSING),
                order("BC-2025", "Egle Petraityte", now, 44, OrderStatus.NEW),
                order("BC-2026", "Daniel Wilson", now, 46, OrderStatus.CANCELLED),
                order("BC-2027", "Ingrida Vasiliauskiene", now, 47, OrderStatus.PROCESSING),
                order("BC-2028", "Amelija Grigaite", now, 48, OrderStatus.NEW),
                order("BC-2029", "Jokubas Matulaitis", now, 49, OrderStatus.PROCESSING),
                order("BC-2030", "Neringa Kairiene", now, 50, OrderStatus.NEW),

                order("BC-2031", "Martynas Jasiunas", now, 53, OrderStatus.SHIPPED),
                order("BC-2032", "Ava Johnson", now, 56, OrderStatus.PROCESSING),
                order("BC-2033", "Simona Zukauskaite", now, 59, OrderStatus.NEW),
                order("BC-2034", "Edgaras Pocius", now, 62, OrderStatus.DELIVERED),
                order("BC-2035", "Kotryna Vilkaite", now, 65, OrderStatus.PROCESSING),
                order("BC-2036", "Liam Harris", now, 68, OrderStatus.SHIPPED),
                order("BC-2037", "Rasa Sabaliauskiene", now, 71, OrderStatus.NEW),
                order("BC-2038", "Titas Marcinkus", now, 74, OrderStatus.CANCELLED),
                order("BC-2039", "Isabella Clark", now, 77, OrderStatus.PROCESSING),
                order("BC-2040", "Augustas Dambrauskas", now, 80, OrderStatus.SHIPPED),

                order("BC-2041", "Monika Aleknaite", now, 84, OrderStatus.NEW),
                order("BC-2042", "Ethan Lewis", now, 88, OrderStatus.DELIVERED),
                order("BC-2043", "Justina Bartkute", now, 92, OrderStatus.PROCESSING),
                order("BC-2044", "Saulius Valaitis", now, 96, OrderStatus.CANCELLED),
                order("BC-2045", "Amelia Walker", now, 102, OrderStatus.SHIPPED),
                order("BC-2046", "Giedre Mockute", now, 108, OrderStatus.NEW),
                order("BC-2047", "Benjamin Taylor", now, 114, OrderStatus.DELIVERED),
                order("BC-2048", "Vytautas Kesminas", now, 120, OrderStatus.PROCESSING),
                order("BC-2049", "Agne Stankunaite", now, 126, OrderStatus.SHIPPED),
                order("BC-2050", "James Allen", now, 132, OrderStatus.CANCELLED),

                order("BC-2051", "Erika Venslovaite", now, 140, OrderStatus.DELIVERED),
                order("BC-2052", "Povilas Raudonis", now, 148, OrderStatus.NEW),
                order("BC-2053", "Charlotte Evans", now, 156, OrderStatus.SHIPPED),
                order("BC-2054", "Tadas Jurkevicius", now, 168, OrderStatus.PROCESSING),
                order("BC-2055", "Viktorija Mazeikaite", now, 180, OrderStatus.CANCELLED),
                order("BC-2056", "Henry Scott", now, 196, OrderStatus.DELIVERED),
                order("BC-2057", "Lina Kavaliauskiene", now, 216, OrderStatus.SHIPPED),
                order("BC-2058", "Arnas Vasiliauskas", now, 240, OrderStatus.NEW),
                order("BC-2059", "Emma Thompson", now, 264, OrderStatus.DELIVERED),
                order("BC-2060", "Deividas Zilinskas", now, 300, OrderStatus.PROCESSING)
        );

        orderRepository.saveAll(demoOrders);
    }

    private CustomerOrder order(
            String orderNumber, String customerName, Instant now, int hoursAgo, OrderStatus status
    ) {
        int number = Integer.parseInt(orderNumber.substring(3));
        int minute = (number * 37) % 60;

        Instant createdAt = now.truncatedTo(ChronoUnit.HOURS).minus(hoursAgo, ChronoUnit.HOURS).plus(minute, ChronoUnit.MINUTES);

        return new CustomerOrder(orderNumber, customerName, createdAt, status);
    }
}
