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
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/sql_quiz";
        String username = "root";
        String password = "rootroot";

        // Load Driver Class
        Class.forName("com.mysql.cj.jdbc.Driver");

        System.out.println("Enter username: ");
        String usern = scanner.nextLine();
        System.out.println("Enter password: ");
        String passw = scanner.nextLine();
        User user = authenticateUser(usern, passw);

        if (user != null) {
            System.out.println("Success. Welcome " + user.getUsername());
            if ("admin".equalsIgnoreCase(user.getRole())) {
                return;
            } else if ("none".equalsIgnoreCase(user.getRole()))
                System.out.println("1. View Product");
                System.out.println("2. Search Product");
                System.out.println("3. Sell Product");
                System.out.println("4. Exit");
                int choice = scanner.nextInt();
                if (choice == 1) {
                    viewProducts();
                }

        }  else {
            System.out.println("Authentication failed. Try Again.");
        }


    }

    public static User authenticateUser(String usern, String passw) {
        String readQuery = "Select * FROM users WHERE username = (?) AND password = (?)";
        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(readQuery)) {
            preparedStatement.setString(1, usern);
            preparedStatement.setString(2, passw);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
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

    public static void viewProducts() {
        String readQuery = "SELECT * FROM products";

    }

}
