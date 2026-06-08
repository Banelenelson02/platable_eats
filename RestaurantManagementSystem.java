import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ============================================================
// ENUMS
// ============================================================

enum OrderStatus { PENDING, IN_KITCHEN, READY, SERVED, CANCELLED }
enum TableStatus { AVAILABLE, OCCUPIED, RESERVED }
enum ReservationStatus { CONFIRMED, CANCELLED, COMPLETED }

// ============================================================
// MENU ITEM
// ============================================================

class MenuItem {
    private String id;
    private String name;
    private double price;
    private String category;
    private boolean available;

    public MenuItem(String id, String name, double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.available = true;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return String.format("[%s] %s - $%.2f (%s)%s",
            id, name, price, category, available ? "" : " [UNAVAILABLE]");
    }
}

// ============================================================
// ORDER ITEM
// ============================================================

class OrderItem {
    private MenuItem menuItem;
    private int quantity;
    private String specialInstructions;

    public OrderItem(MenuItem menuItem, int quantity, String specialInstructions) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.specialInstructions = specialInstructions;
    }

    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getSpecialInstructions() { return specialInstructions; }
    public double getSubtotal() { return menuItem.getPrice() * quantity; }

    @Override
    public String toString() {
        String instr = specialInstructions.isEmpty() ? "" : " (" + specialInstructions + ")";
        return String.format("  x%d %s%s - $%.2f", quantity, menuItem.getName(), instr, getSubtotal());
    }
}

// ============================================================
// ORDER
// ============================================================

class Order {
    private String orderId;
    private String tableId;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private String waiterId;

    public Order(String orderId, String tableId, String waiterId) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.waiterId = waiterId;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public String getOrderId() { return orderId; }
    public String getTableId() { return tableId; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public List<OrderItem> getItems() { return items; }
    public String getWaiterId() { return waiterId; }

    public void addItem(MenuItem item, int qty, String instructions) {
        // Check if item already exists, if so update quantity
        for (OrderItem oi : items) {
            if (oi.getMenuItem().getId().equals(item.getId())) {
                oi.setQuantity(oi.getQuantity() + qty);
                return;
            }
        }
        items.add(new OrderItem(item, qty, instructions));
    }

    public boolean removeItem(String menuItemId) {
        return items.removeIf(oi -> oi.getMenuItem().getId().equals(menuItemId));
    }

    public double getTotal() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    public void printReceipt() {
        System.out.println("\n========== ORDER RECEIPT ==========");
        System.out.println("Order ID : " + orderId);
        System.out.println("Table    : " + tableId);
        System.out.println("Waiter   : " + waiterId);
        System.out.println("Time     : " + createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println("Status   : " + status);
        System.out.println("-----------------------------------");
        items.forEach(System.out::println);
        System.out.println("-----------------------------------");
        System.out.printf("TOTAL    : $%.2f%n", getTotal());
        System.out.println("===================================\n");
    }
}

// ============================================================
// TABLE
// ============================================================

class Table {
    private String tableId;
    private int capacity;
    private TableStatus status;
    private String currentOrderId;

    public Table(String tableId, int capacity) {
        this.tableId = tableId;
        this.capacity = capacity;
        this.status = TableStatus.AVAILABLE;
    }

    public String getTableId() { return tableId; }
    public int getCapacity() { return capacity; }
    public TableStatus getStatus() { return status; }
    public void setStatus(TableStatus status) { this.status = status; }
    public String getCurrentOrderId() { return currentOrderId; }
    public void setCurrentOrderId(String orderId) { this.currentOrderId = orderId; }

    @Override
    public String toString() {
        return String.format("Table %s (cap: %d) - %s", tableId, capacity, status);
    }
}

// ============================================================
// RESERVATION
// ============================================================

class Reservation {
    private String reservationId;
    private String customerName;
    private String tableId;
    private LocalDateTime reservationTime;
    private int partySize;
    private ReservationStatus status;

    public Reservation(String reservationId, String customerName, String tableId,
                       LocalDateTime time, int partySize) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.tableId = tableId;
        this.reservationTime = time;
        this.partySize = partySize;
        this.status = ReservationStatus.CONFIRMED;
    }

    public String getReservationId() { return reservationId; }
    public String getCustomerName() { return customerName; }
    public String getTableId() { return tableId; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Reservation [%s] %s - Table %s, Party of %d at %s [%s]",
            reservationId, customerName, tableId, partySize,
            reservationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), status);
    }
}

// ============================================================
// ABSTRACT EMPLOYEE
// ============================================================

abstract class Employee {
    protected String employeeId;
    protected String name;
    protected String role;

    public Employee(String employeeId, String name, String role) {
        this.employeeId = employeeId;
        this.name = name;
        this.role = role;
    }

    public String getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getRole() { return role; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", employeeId, name, role);
    }
}

// ============================================================
// RECEPTIONIST
// ============================================================

class Receptionist extends Employee {
    private RestaurantSystem system;

    public Receptionist(String id, String name, RestaurantSystem system) {
        super(id, name, "Receptionist");
        this.system = system;
    }

    public Table addTable(String tableId, int capacity) {
        return system.addTable(tableId, capacity);
    }

    public void modifyTableLayout(String tableId, int newCapacity) {
        system.modifyTable(tableId, newCapacity);
        System.out.println(name + " updated layout for table " + tableId);
    }

    public Reservation createReservation(String customerName, String tableId,
                                          LocalDateTime time, int partySize) {
        return system.createReservation(customerName, tableId, time, partySize);
    }

    public boolean cancelReservation(String reservationId) {
        return system.cancelReservation(reservationId);
    }

    public List<Table> searchAvailableTables(int minCapacity) {
        return system.getAvailableTables(minCapacity);
    }
}

// ============================================================
// WAITER
// ============================================================

class Waiter extends Employee {
    private RestaurantSystem system;

    public Waiter(String id, String name, RestaurantSystem system) {
        super(id, name, "Waiter");
        this.system = system;
    }

    public Order takeOrder(String tableId) {
        return system.createOrder(tableId, employeeId);
    }

    public void addItemToOrder(String orderId, String menuItemId, int qty, String instructions) {
        system.addItemToOrder(orderId, menuItemId, qty, instructions);
        System.out.println(name + " added item to order " + orderId);
    }

    public void modifyOrder(String orderId, String menuItemId, int newQty) {
        system.modifyOrderItem(orderId, menuItemId, newQty);
        System.out.println(name + " modified order " + orderId);
    }

    public void submitToKitchen(String orderId) {
        system.updateOrderStatus(orderId, OrderStatus.IN_KITCHEN);
        System.out.println(name + " submitted order " + orderId + " to kitchen.");
    }

    public void serveMealToTable(String orderId) {
        system.updateOrderStatus(orderId, OrderStatus.SERVED);
        System.out.println(name + " served order " + orderId + " to table.");
    }
}

// ============================================================
// CHEF
// ============================================================

class Chef extends Employee {
    private RestaurantSystem system;

    public Chef(String id, String name, RestaurantSystem system) {
        super(id, name, "Chef");
        this.system = system;
    }

    public List<Order> viewIncomingOrders() {
        List<Order> orders = system.getOrdersByStatus(OrderStatus.IN_KITCHEN);
        System.out.println(name + " viewing " + orders.size() + " incoming order(s).");
        return orders;
    }

    public void prepareMeal(String orderId) {
        System.out.println(name + " is preparing order " + orderId + "...");
    }

    public void markOrderReady(String orderId) {
        system.updateOrderStatus(orderId, OrderStatus.READY);
        System.out.println(name + " marked order " + orderId + " as READY.");
    }

    public void manageKitchen() {
        System.out.println(name + " managing kitchen operations.");
    }
}

// ============================================================
// MANAGER
// ============================================================

class Manager extends Employee {
    private RestaurantSystem system;

    public Manager(String id, String name, RestaurantSystem system) {
        super(id, name, "Manager");
        this.system = system;
    }

    public Employee addWorker(String id, String workerName, String role) {
        return system.addEmployee(id, workerName, role);
    }

    public void modifyMenuItem(String itemId, Double newPrice, Boolean available) {
        system.modifyMenuItem(itemId, newPrice, available);
        System.out.println(name + " modified menu item " + itemId);
    }

    public void addMenuItem(String id, String itemName, double price, String category) {
        system.addMenuItem(id, itemName, price, category);
        System.out.println(name + " added menu item: " + itemName);
    }

    public void overseeOperations() {
        System.out.println("\n--- OPERATIONS OVERVIEW ---");
        system.printStatus();
    }

    public void manageBranch(String branchName) {
        System.out.println(name + " managing branch: " + branchName);
    }
}

// ============================================================
// RESTAURANT SYSTEM (Core)
// ============================================================

class RestaurantSystem {
    private String restaurantName;
    private Map<String, Table> tables;
    private Map<String, MenuItem> menu;
    private Map<String, Order> orders;
    private Map<String, Reservation> reservations;
    private Map<String, Employee> employees;
    private int orderCounter = 1;
    private int reservationCounter = 1;

    public RestaurantSystem(String restaurantName) {
        this.restaurantName = restaurantName;
        this.tables = new LinkedHashMap<>();
        this.menu = new LinkedHashMap<>();
        this.orders = new LinkedHashMap<>();
        this.reservations = new LinkedHashMap<>();
        this.employees = new LinkedHashMap<>();
    }

    // --- TABLE MANAGEMENT ---
    public Table addTable(String tableId, int capacity) {
        Table t = new Table(tableId, capacity);
        tables.put(tableId, t);
        System.out.println("Table " + tableId + " added (capacity: " + capacity + ")");
        return t;
    }

    public void modifyTable(String tableId, int newCapacity) {
        Table t = tables.get(tableId);
        if (t != null) {
            // Recreate with new capacity (simple approach)
            tables.put(tableId, new Table(tableId, newCapacity));
        }
    }

    public List<Table> getAvailableTables(int minCapacity) {
        List<Table> result = new ArrayList<>();
        for (Table t : tables.values()) {
            if (t.getStatus() == TableStatus.AVAILABLE && t.getCapacity() >= minCapacity) {
                result.add(t);
            }
        }
        return result;
    }

    // --- MENU MANAGEMENT ---
    public MenuItem addMenuItem(String id, String name, double price, String category) {
        MenuItem item = new MenuItem(id, name, price, category);
        menu.put(id, item);
        return item;
    }

    public void modifyMenuItem(String itemId, Double newPrice, Boolean available) {
        MenuItem item = menu.get(itemId);
        if (item != null) {
            if (newPrice != null) item.setPrice(newPrice);
            if (available != null) item.setAvailable(available);
        }
    }

    public MenuItem getMenuItem(String id) { return menu.get(id); }

    // --- ORDER MANAGEMENT ---
    public Order createOrder(String tableId, String waiterId) {
        String orderId = "ORD" + String.format("%03d", orderCounter++);
        Order order = new Order(orderId, tableId, waiterId);
        orders.put(orderId, order);
        Table t = tables.get(tableId);
        if (t != null) {
            t.setStatus(TableStatus.OCCUPIED);
            t.setCurrentOrderId(orderId);
        }
        System.out.println("Order " + orderId + " created for table " + tableId);
        return order;
    }

    public void addItemToOrder(String orderId, String menuItemId, int qty, String instructions) {
        Order order = orders.get(orderId);
        MenuItem item = menu.get(menuItemId);
        if (order != null && item != null && item.isAvailable()) {
            order.addItem(item, qty, instructions);
        }
    }

    public void modifyOrderItem(String orderId, String menuItemId, int newQty) {
        Order order = orders.get(orderId);
        if (order != null) {
            if (newQty <= 0) {
                order.removeItem(menuItemId);
            } else {
                for (OrderItem oi : order.getItems()) {
                    if (oi.getMenuItem().getId().equals(menuItemId)) {
                        oi.setQuantity(newQty);
                        return;
                    }
                }
            }
        }
    }

    public void updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orders.get(orderId);
        if (order != null) {
            order.setStatus(status);
            if (status == OrderStatus.SERVED) {
                Table t = tables.get(order.getTableId());
                if (t != null) t.setStatus(TableStatus.AVAILABLE);
            }
        }
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        List<Order> result = new ArrayList<>();
        for (Order o : orders.values()) {
            if (o.getStatus() == status) result.add(o);
        }
        return result;
    }

    public Order getOrder(String orderId) { return orders.get(orderId); }

    // --- RESERVATION MANAGEMENT ---
    public Reservation createReservation(String customerName, String tableId,
                                          LocalDateTime time, int partySize) {
        String resId = "RES" + String.format("%03d", reservationCounter++);
        Reservation res = new Reservation(resId, customerName, tableId, time, partySize);
        reservations.put(resId, res);
        Table t = tables.get(tableId);
        if (t != null) t.setStatus(TableStatus.RESERVED);
        System.out.println("Reservation " + resId + " created for " + customerName);
        return res;
    }

    public boolean cancelReservation(String reservationId) {
        Reservation res = reservations.get(reservationId);
        if (res != null && res.getStatus() == ReservationStatus.CONFIRMED) {
            res.setStatus(ReservationStatus.CANCELLED);
            Table t = tables.get(res.getTableId());
            if (t != null) t.setStatus(TableStatus.AVAILABLE);
            System.out.println("Reservation " + reservationId + " cancelled.");
            return true;
        }
        return false;
    }

    // --- EMPLOYEE MANAGEMENT ---
    public Employee addEmployee(String id, String name, String role) {
        Employee emp;
        switch (role.toLowerCase()) {
            case "waiter":      emp = new Waiter(id, name, this); break;
            case "chef":        emp = new Chef(id, name, this); break;
            case "receptionist": emp = new Receptionist(id, name, this); break;
            case "manager":     emp = new Manager(id, name, this); break;
            default:            emp = new Waiter(id, name, this); break;
        }
        employees.put(id, emp);
        System.out.println("Employee added: " + emp);
        return emp;
    }

    // --- STATUS OVERVIEW ---
    public void printStatus() {
        System.out.println("Restaurant: " + restaurantName);
        System.out.println("\nTABLES:");
        tables.values().forEach(t -> System.out.println("  " + t));
        System.out.println("\nMENU:");
        menu.values().forEach(m -> System.out.println("  " + m));
        System.out.println("\nACTIVE ORDERS:");
        orders.values().stream()
            .filter(o -> o.getStatus() != OrderStatus.SERVED)
            .forEach(o -> System.out.printf("  %s | Table %s | %s | $%.2f%n",
                o.getOrderId(), o.getTableId(), o.getStatus(), o.getTotal()));
        System.out.println("\nRESERVATIONS:");
        reservations.values().forEach(r -> System.out.println("  " + r));
    }
}

// ============================================================
// MAIN DEMO
// ============================================================

public class RestaurantManagementSystem {
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   RESTAURANT MANAGEMENT SYSTEM OOD  ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // Initialize system
        RestaurantSystem system = new RestaurantSystem("La Bella Italia");

        // Create staff
        Manager    manager      = new Manager("M001", "Sofia Romano", system);
        Receptionist receptionist = new Receptionist("R001", "Emma Chen", system);
        Waiter     waiter       = new Waiter("W001", "James Park", system);
        Chef       chef         = new Chef("C001", "Marco Rossi", system);

        System.out.println("\n=== MANAGER: Setting Up Restaurant ===");
        // Add menu items
        manager.addMenuItem("P001", "Margherita Pizza",   12.99, "Pizza");
        manager.addMenuItem("P002", "Pepperoni Pizza",    14.99, "Pizza");
        manager.addMenuItem("PA01", "Spaghetti Carbonara",13.99, "Pasta");
        manager.addMenuItem("PA02", "Fettuccine Alfredo", 12.99, "Pasta");
        manager.addMenuItem("D001", "Tiramisu",            6.99, "Dessert");
        manager.addMenuItem("B001", "Sparkling Water",     2.99, "Beverage");

        // Add another worker
        manager.addWorker("W002", "Lily Zhang", "waiter");

        System.out.println("\n=== RECEPTIONIST: Managing Tables & Reservations ===");
        receptionist.addTable("T1", 2);
        receptionist.addTable("T2", 4);
        receptionist.addTable("T3", 6);
        receptionist.addTable("T4", 4);

        List<Table> available = receptionist.searchAvailableTables(4);
        System.out.println("Available tables for 4+: " + available.size());

        Reservation res = receptionist.createReservation(
            "John Smith", "T3",
            LocalDateTime.now().plusHours(2), 5);

        System.out.println("\n=== WAITER: Taking & Submitting Order ===");
        Order order = waiter.takeOrder("T2");
        waiter.addItemToOrder(order.getOrderId(), "P001", 1, "extra cheese");
        waiter.addItemToOrder(order.getOrderId(), "PA01", 2, "");
        waiter.addItemToOrder(order.getOrderId(), "B001", 3, "no ice");
        waiter.addItemToOrder(order.getOrderId(), "D001", 2, "");
        waiter.submitToKitchen(order.getOrderId());

        System.out.println("\n=== CHEF: Kitchen Operations ===");
        List<Order> incomingOrders = chef.viewIncomingOrders();
        if (!incomingOrders.isEmpty()) {
            String ordId = incomingOrders.get(0).getOrderId();
            chef.prepareMeal(ordId);
            chef.markOrderReady(ordId);
        }

        System.out.println("\n=== WAITER: Serving & Receipt ===");
        waiter.serveMealToTable(order.getOrderId());
        system.getOrder(order.getOrderId()).printReceipt();

        System.out.println("\n=== MANAGER: Operations Overview ===");
        manager.overseeOperations();

        System.out.println("\n=== MANAGER: Modify Menu Item ===");
        manager.modifyMenuItem("P001", 13.99, null); // price update
        System.out.println("Updated Margherita Pizza price to $13.99");

        System.out.println("\n=== RECEPTIONIST: Cancel Reservation ===");
        receptionist.cancelReservation(res.getReservationId());

        System.out.println("\n✅ Demo complete!");
    }
}
