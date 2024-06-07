package inventorymanagement;

import java.sql.*;
import java.util.Scanner;

public class InventoryManagement {
    // Database connection details
    private static final String url = "jdbc:mysql://localhost:3306/inventory_management";
    private static final String username = "root";
    private static final String password = "rootroot";
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws ClassNotFoundException {
        // Load Driver Class
        Class.forName("com.mysql.cj.jdbc.Driver");

        boolean im_running = true;

        try(Connection connection = DriverManager.getConnection(url, username, password)) {
            // Authentication
            System.out.println("Enter username: ");
            String usern = scanner.nextLine();
            System.out.println("Enter password: ");
            String passw = scanner.nextLine();
            // An object with the user information is also created
            User user = authenticateUser(connection, usern, passw);

            if (user != null) {
                System.out.println("Success. Welcome " + user.getUsername());
                if ("admin".equalsIgnoreCase(user.getRole())) {
                    // Gives access to features based on if the user exists and the role of the user
                    while (im_running) {
                        // Admin Menu
                        System.out.println("1. Add Product");
                        System.out.println("2. Update Product");
                        System.out.println("3. Delete Product");
                        System.out.println("4. View Products");
                        System.out.println("5. Sell Product");
                        System.out.println("6. Generate Sales Report");
                        System.out.println("7. Exit");
                        int choice = scanner.nextInt();
                        scanner.nextLine();
                        if (choice == 1) {
                            // Add products
                            System.out.println("What is the product name?");
                            String name = scanner.nextLine();
                            System.out.println("Quantity?");
                            int quantity = scanner.nextInt();
                            System.out.println("Price?");
                            int price = scanner.nextInt();
                            addProduct(connection, name, quantity, price);
                        } else if (choice == 2) {
                            // Update products
                            System.out.println("Product Id?");
                            int product_id = scanner.nextInt();
                            scanner.nextLine();
                            System.out.println("Product name?");
                            String name = scanner.nextLine();
                            System.out.println("Quantity?");
                            int quantity = scanner.nextInt();
                            System.out.println("Price?");
                            int price = scanner.nextInt();
                            updateProduct(connection, product_id, name, quantity, price);
                        } else if (choice == 3) {
                            // Delete Products
                            System.out.println("Product Id?");
                            int product_id = scanner.nextInt();
                            deleteProduct(connection, product_id);
                        } else if (choice == 4) {
                            // View All Products
                            viewProducts(connection);
                        } else if (choice == 5) {
                            // Sell a Product
                            System.out.println("Product Id?");
                            int product_id = scanner.nextInt();
                            scanner.nextLine();
                            System.out.println("Quantity?");
                            int quantity = scanner.nextInt();
                            System.out.println("Price?");
                            int price = scanner.nextInt();
                            sellProduct(connection, product_id, quantity, price);
                        } else if (choice == 6) {
                            // Generate Sales report
                            generateSalesReport(connection);
                        } else if (choice == 7) {
                            im_running = false;
                        } else {
                            System.out.println("Pick a number from the options.");
                        }
                    }

                } else if ("none".equalsIgnoreCase(user.getRole()))
                    while(im_running) {
                        System.out.println("1. View Product");
                        System.out.println("2. Search Product");
                        System.out.println("3. Sell Product");
                        System.out.println("4. Exit");
                        int choice = scanner.nextInt();
                        scanner.nextLine();
                        if (choice == 1) {
                            viewProducts(connection);
                        } else if (choice == 2) {
                            System.out.println("Product Id?");
                            int product_id = scanner.nextInt();
                            searchProduct(connection, product_id);
                        } else if (choice == 3) {
                            System.out.println("Product Id?");
                            int product_id = scanner.nextInt();
                            scanner.nextLine();
                            System.out.println("Quantity?");
                            int quantity = scanner.nextInt();
                            System.out.println("Price?");
                            int price = scanner.nextInt();
                            sellProduct(connection, product_id, quantity, price);
                        } else if (choice == 4) {
                            im_running = false;
                        } else {
                            System.out.println("Pick a number from the options.");
                        }
                    }

            }  else {
                System.out.println("Authentication failed. Try Again.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static User authenticateUser(Connection connection, String usern, String passw) {
        // Verify if user exists with correct username and password
        String readQuery = "Select * FROM users WHERE username = (?) AND password = (?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(readQuery)) {
            preparedStatement.setString(1, usern);
            preparedStatement.setString(2, passw);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // Created user object with information of logged in user
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setRole(resultSet.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void viewProducts(Connection connection) {
        // Returns all products
        String readQuery = "SELECT * FROM products";
        try (Statement readStatement = connection.createStatement();
        ResultSet resultSet = readStatement.executeQuery(readQuery)) {
            while (resultSet.next()) {
                System.out.print("Product ");
                System.out.print("{Id : " +resultSet.getString("id") + " | ");
                System.out.print("Name : " +resultSet.getString("name") + " | ");
                System.out.print("Quantity : " +resultSet.getString("quantity") + " | ");
                System.out.print("Price : " +resultSet.getString("price") + "}");
                System.out.println(" ");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addProduct(Connection connection, String name, int quantity, int price) {
        // Insert data to products
        String insertQuery = "INSERT INTO products(name, quantity, price) VALUES(?, ?, ?)";
        try(PreparedStatement createStatement = connection.prepareStatement(insertQuery)){
            createStatement.setString(1, name);
            createStatement.setInt(2, quantity);
            createStatement.setInt(3, price);
            createStatement.executeUpdate();
            System.out.println(" ");
            System.out.println("Success. Product added!");
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateProduct(Connection connection, int id, String name, int quantity, int price) throws SQLException {
        // Update product
        String updateQuery = "UPDATE products SET name = ?, quantity = ?, price = ? WHERE id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, name);
            updateStatement.setInt(2, quantity);
            updateStatement.setInt(3, price);
            updateStatement.setInt(4, id);
            updateStatement.executeUpdate();
            System.out.println(" ");
            System.out.println("Success. Product updated!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteProduct(Connection connection, int id) {
        // Delete product based on id
        String deleteQuery = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setInt(1, id);
            deleteStatement.executeUpdate();
            System.out.println(" ");
            System.out.println("Success. Product deleted.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void searchProduct(Connection connection, int id) {
        // Search product with ID
        String searchQuery = "SELECT * FROM products WHERE id = ?";
        try (PreparedStatement createStatement = connection.prepareStatement(searchQuery)) {
            createStatement.setInt(1, id);

            try (ResultSet resultSet = createStatement.executeQuery()) {
                if (resultSet.next() ) {
                    System.out.print("Product ");
                    System.out.print("{Id : " +resultSet.getString("id") + " | ");
                    System.out.print("Name : " +resultSet.getString("name") + " | ");
                    System.out.print("Quantity : " +resultSet.getString("quantity") + " | ");
                    System.out.print("Price : " +resultSet.getString("price") + " | ");
                    System.out.print("name : " +resultSet.getString("name") + "}");
                    System.out.println(" ");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sellProduct(Connection connection, int product_id, int quantity, int price) throws SQLException {
        // Add data to sales table for products sold
        String insertSaleQuery = "INSERT INTO sales(product_id, quantity_sold, price_sold, sale_date) VALUES(?, ?, ?, NOW())";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertSaleQuery)) {
            insertStatement.setInt(1, product_id);
            insertStatement.setInt(2, quantity);
            insertStatement.setInt(3, price);
            insertStatement.executeUpdate();
        }

        // Update products sold information
        String updateQuery = "UPDATE products SET quantity = quantity - ?, price = price - ? WHERE id = ?";
        try (PreparedStatement createStatement = connection.prepareStatement(updateQuery)) {
            createStatement.setInt(1, quantity);
            createStatement.setInt(2, price);
            createStatement.setInt(3, product_id);
            createStatement.executeUpdate();
            System.out.println("Success. " + quantity + " items sold!");
        }
    }

    public static void generateSalesReport(Connection connection) {
        // Return information about products sold
        String query = "SELECT products.name, sales.quantity_sold, sales.price_sold, sales.sale_date FROM sales JOIN products ON sales.product_id = products.id";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            System.out.println("Sales Report:");
            while (resultSet.next()) {
                System.out.print("Name : " +resultSet.getString("name") + " | ");
                System.out.print("Quantity Sold : " +resultSet.getString("quantity_sold") + " | ");
                System.out.print("Price Sold : " +resultSet.getString("price_sold") + " | ");
                System.out.print("Sales Date : " +resultSet.getString("sale_date") + "}");
                System.out.println(" ");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generating sales report", e);
        }
    }

}
