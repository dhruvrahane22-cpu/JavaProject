import java.sql.*;
import java.util.*;

public class CafeManagement {
    static final String DBURL = "jdbc:mysql://localhost:3306/cafe_db";
    static final String USER = "root";
    static final String PASS = "Dhruv@275";

    // Admin Credentials
    static final String ADMIN_USER = "admin";
    static final String ADMIN_PASS = "admin123";

    // Sample Combo Offers
    static Map<String, Double> comboOffers = new HashMap<>();
    static {
        comboOffers.put("1+2", 150.0);
        comboOffers.put("3+4", 220.0);
    }

    // ====================== DISPLAY MENU ======================
    static void displayMenu() {
        try (Connection conn = DriverManager.getConnection(DBURL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM FOOD_ITEMS ORDER BY itemId")) {

            System.out.println("\n=========== MENU ===========");
            while (rs.next()) {
                System.out.printf("%d | %s | %s | Rs. %.2f%n",
                        rs.getInt("itemId"),
                        rs.getString("itemName"),
                        rs.getString("category"),
                        rs.getDouble("price"));
            }
            System.out.println("============================\n");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ====================== USER LOGIN ======================
    static Integer userLogin(Scanner sc) {
        System.out.print("Enter User ID: ");
        String inputId = sc.nextLine().trim();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine().trim();

        try (Connection conn = DriverManager.getConnection(DBURL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT userId FROM USERS WHERE userId = ? AND password = ?")) {

            pstmt.setString(1, inputId);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("userId");
                System.out.println("✅ User Login Successful! Welcome, User #" + userId);
                return userId;
            } else {
                System.out.println("❌ Invalid User ID or Password!");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Login Error: " + e.getMessage());
            return null;
        }
    }

    // ====================== ADMIN LOGIN ======================
    static boolean adminLogin(Scanner sc) {
        System.out.print("Enter Admin Username: ");
        String user = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();
        if (user.equals(ADMIN_USER) && pass.equals(ADMIN_PASS)) {
            System.out.println("✅ Admin Login Successful!");
            return true;
        } else {
            System.out.println("❌ Invalid Credentials!");
            return false;
        }
    }

    // ====================== ADD FOOD ITEM ======================
    static void addFoodItem(Scanner sc) {
        try (Connection conn = DriverManager.getConnection(DBURL, USER, PASS)) {
            System.out.print("Enter Item ID: ");
            int id = Integer.parseInt(sc.nextLine());
            System.out.print("Enter Item Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Category: ");
            String cat = sc.nextLine();
            System.out.print("Enter Price: ");
            double price = Double.parseDouble(sc.nextLine());

            String query = "INSERT INTO FOOD_ITEMS (itemId, itemName, category, price) VALUES(?,?,?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, cat);
                pstmt.setDouble(4, price);
                pstmt.executeUpdate();
                System.out.println("✅ Food Item Added Successfully!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ====================== UPDATE FOOD ITEM ======================
    static void updateFoodItem(Scanner sc) {
        try (Connection conn = DriverManager.getConnection(DBURL, USER, PASS)) {
            System.out.print("Enter Item ID to Update: ");
            int id = Integer.parseInt(sc.nextLine());
            System.out.print("New Name: ");
            String name = sc.nextLine();
            System.out.print("New Category: ");
            String cat = sc.nextLine();
            System.out.print("New Price: ");
            double price = Double.parseDouble(sc.nextLine());

            String query = "UPDATE FOOD_ITEMS SET itemName=?, category=?, price=? WHERE itemId=?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, cat);
                pstmt.setDouble(3, price);
                pstmt.setInt(4, id);
                int rows = pstmt.executeUpdate();
                if (rows > 0) System.out.println("✅ Item Updated Successfully!");
                else System.out.println("Item not found!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ====================== DELETE FOOD ITEM ======================
    static void deleteFoodItem(Scanner sc) {
        try (Connection conn = DriverManager.getConnection(DBURL, USER, PASS)) {
            System.out.print("Enter Item ID to Delete: ");
            int id = Integer.parseInt(sc.nextLine());
            String query = "DELETE FROM FOOD_ITEMS WHERE itemId=?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, id);
                int rows = pstmt.executeUpdate();
                if (rows > 0) System.out.println("✅ Item Deleted Successfully!");
                else System.out.println("Item not found!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ====================== SALES REPORT ======================
    static void salesReport() {
        try (Connection conn = DriverManager.getConnection(DBURL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ORDERS ORDER BY order_date DESC")) {

            System.out.println("\n=========== SALES REPORT ===========");
            double grandTotal = 0;
            boolean hasOrders = false;
            while (rs.next()) {
                hasOrders = true;
                System.out.printf("Order #%d | User ID: %d | %s | Final: Rs. %.2f%n",
                        rs.getInt("order_id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("order_date"),
                        rs.getDouble("final_amount"));
                grandTotal += rs.getDouble("final_amount");
            }
            if (!hasOrders) {
                System.out.println("No orders placed yet.");
            }
            System.out.println("====================================");
            System.out.printf("Total Revenue: Rs. %.2f%n", grandTotal);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ====================== VIEW ALL USERS ======================
    static void viewAllUsers() {
        try (Connection conn = DriverManager.getConnection(DBURL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT userId, username, created_at FROM USERS ORDER BY userId")) {

            System.out.println("\n=========== ALL REGISTERED USERS ===========");
            boolean hasUsers = false;
            while (rs.next()) {
                hasUsers = true;
                System.out.printf("User ID: %d | Username: %s | Joined: %s%n",
                        rs.getInt("userId"),
                        rs.getString("username"),
                        rs.getTimestamp("created_at"));
            }
            if (!hasUsers) {
                System.out.println("No users found in the database.");
            }
            System.out.println("============================================");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ====================== VIEW USER ORDER HISTORY ======================
    static void viewUserOrderHistory(int userId) {
        try (Connection conn = DriverManager.getConnection(DBURL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM ORDERS WHERE user_id = ? ORDER BY order_date DESC")) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n=========== ORDER HISTORY FOR USER #" + userId + " ===========");
            boolean hasOrders = false;
            while (rs.next()) {
                hasOrders = true;
                System.out.printf("Order #%d | Date: %s | Items: %s | Final Amount: Rs. %.2f%n",
                        rs.getInt("order_id"),
                        rs.getTimestamp("order_date"),
                        rs.getString("items"),
                        rs.getDouble("final_amount"));
            }
            if (!hasOrders) {
                System.out.println("No orders found for User ID " + userId);
            }
            System.out.println("====================================================");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ====================== PLACE ORDER ======================
    static void placeOrder(Scanner sc, Integer userId) {
        List<String> orderItems = new ArrayList<>();
        double subtotal = 0.0;
        System.out.println("\n=== Place Order ===");
        displayMenu();

        char more;
        do {
            System.out.print("Enter Item ID (or 0 for combo): ");
            int id = Integer.parseInt(sc.nextLine());

            if (id == 0) {
                System.out.print("Enter Combo Code (e.g., 1+2): ");
                String combo = sc.nextLine();
                if (comboOffers.containsKey(combo)) {
                    double comboPrice = comboOffers.get(combo);
                    subtotal += comboPrice;
                    orderItems.add("COMBO " + combo + " - ₹" + comboPrice);
                    System.out.println("✅ Combo Applied!");
                } else {
                    System.out.println("Invalid Combo!");
                }
            } else {
                System.out.print("Enter Quantity: ");
                int qty = Integer.parseInt(sc.nextLine());

                try (Connection conn = DriverManager.getConnection(DBURL, USER, PASS);
                     PreparedStatement pstmt = conn.prepareStatement(
                             "SELECT itemName, price FROM FOOD_ITEMS WHERE itemId=?")) {

                    pstmt.setInt(1, id);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        String name = rs.getString("itemName");
                        double price = rs.getDouble("price");
                        double itemTotal = price * qty;
                        subtotal += itemTotal;
                        orderItems.add(name + " x " + qty + " = ₹" + itemTotal);
                        System.out.println("✅ " + name + " added!");
                    } else {
                        System.out.println("Item not found!");
                    }
                } catch (Exception e) {
                    System.out.println("Error fetching item.");
                }
            }

            System.out.print("Add more items? (y/n): ");
            more = sc.nextLine().toLowerCase().charAt(0);
        } while (more == 'y');

        double gst = subtotal * 0.05;
        double discount = subtotal > 1000 ? subtotal * 0.10 : 0;
        double finalAmount = subtotal + gst - discount;

        saveOrderToDB(userId, orderItems, subtotal, gst, discount, finalAmount);

        // Print Bill
        System.out.println("\n=========== CUSTOMER BILL ===========");
        System.out.println("Date: " + new java.util.Date());
        if (userId != null) System.out.println("User ID: " + userId);
        System.out.println("-------------------------------------");
        for (String item : orderItems) System.out.println(item);
        System.out.println("-------------------------------------");
        System.out.printf("Subtotal     : Rs. %.2f%n", subtotal);
        System.out.printf("GST (5%%)     : Rs. %.2f%n", gst);
        System.out.printf("Discount     : Rs. %.2f%n", discount);
        System.out.printf("Final Amount : Rs. %.2f%n", finalAmount);
        System.out.println("=====================================");
        System.out.println("Thank you for visiting! Come again :)");
    }

    static void saveOrderToDB(Integer userId, List<String> items, double sub, double gst, double disc, double finalAmt) {
        String itemsStr = String.join(", ", items);
        try (Connection conn = DriverManager.getConnection(DBURL, USER, PASS)) {
            String sql = "INSERT INTO ORDERS (user_id, items, subtotal, gst, discount, final_amount) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId != null ? userId : 0);
                pstmt.setString(2, itemsStr);
                pstmt.setDouble(3, sub);
                pstmt.setDouble(4, gst);
                pstmt.setDouble(5, disc);
                pstmt.setDouble(6, finalAmt);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Failed to save order: " + e.getMessage());
        }
    }

    // ====================== MAIN METHOD ======================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        char continueChoice;

        do {
            System.out.println("\n==============================");
            System.out.println("     SMART CAFE MANAGEMENT");
            System.out.println("==============================");
            System.out.println("1. User Panel");
            System.out.println("2. Admin Panel");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int mainChoice = Integer.parseInt(sc.nextLine());

            switch (mainChoice) {
                case 1: // User Panel
                    System.out.println("\n--- User Panel ---");
                    Integer loggedInUserId = userLogin(sc);
                    if (loggedInUserId == null) break;

                    char cChoice;
                    do {
                        System.out.println("\n1. View Menu");
                        System.out.println("2. Place Order");
                        System.out.print("Enter option: ");
                        int opt = Integer.parseInt(sc.nextLine());

                        if (opt == 1) displayMenu();
                        else if (opt == 2) placeOrder(sc, loggedInUserId);

                        System.out.print("\nContinue in User Panel? (y/n): ");
                        cChoice = sc.nextLine().toLowerCase().charAt(0);
                    } while (cChoice == 'y');
                    break;

                case 2: // Admin Panel
                    if (adminLogin(sc)) {
                        char aChoice;
                        do {
                            System.out.println("\n--- Admin Panel ---");
                            System.out.println("1. Add Food Item");
                            System.out.println("2. Update Food Item");
                            System.out.println("3. Delete Food Item");
                            System.out.println("4. Sales Report");
                            System.out.println("5. View All Users");
                            System.out.println("6. View User Order History");
                            System.out.print("Enter option: ");
                            int opt = Integer.parseInt(sc.nextLine());

                            switch (opt) {
                                case 1: addFoodItem(sc); break;
                                case 2: updateFoodItem(sc); break;
                                case 3: deleteFoodItem(sc); break;
                                case 4: salesReport(); break;
                                case 5: viewAllUsers(); break;
                                case 6:
                                    System.out.print("Enter User ID: ");
                                    int uid = Integer.parseInt(sc.nextLine());
                                    viewUserOrderHistory(uid);
                                    break;
                                default: System.out.println("Invalid option!");
                            }

                            System.out.print("\nContinue in Admin Panel? (y/n): ");
                            aChoice = sc.nextLine().toLowerCase().charAt(0);
                        } while (aChoice == 'y');
                    }
                    break;

                case 3:
                    System.out.println("Thank You! Goodbye.");
                    sc.close();
                    System.exit(0);
            }

            System.out.print("\nReturn to Main Menu? (y/n): ");
            continueChoice = sc.nextLine().toLowerCase().charAt(0);
        } while (continueChoice == 'y');

        sc.close();
    }
}