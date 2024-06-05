package inventorymanagement;

import junit.framework.TestCase;
import org.junit.jupiter.api.*;
import java.sql.*;

import java.sql.Connection;
import java.sql.SQLException;

public class InventoryManagementTest extends TestCase {
    public static String url = "jdbc:mysql://localhost:3306/inventory_management";
    public static String username = "root";
    public static String password = "rootroot";
    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    @BeforeAll
    public static void setUpk() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Create test data
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, username VARCHAR(50), password VARCHAR(50), role VARCHAR(10))");
            statement.execute("CREATE TABLE IF NOT EXISTS products (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50), quantity INT, price INT)");
            statement.execute("INSERT INTO users (id, username, password, role) VALUES (1, 'admin', 'admin', 'admin')");
            statement.execute("INSERT INTO users (id, username, password, role) VALUES (2, 'user', 'user', 'user')");
            statement.execute("INSERT INTO products (name, quantity, price) VALUES ('Product1', 100, 50)");
        }
    }

    public void testAddProduct() {
        InventoryManagement.addProduct(connection, "Product2", 200, 100);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM products WHERE name = 'Product2'")) {
            assertTrue(resultSet.next());
            assertEquals(200, resultSet.getInt("quantity"));
            assertEquals(100, resultSet.getInt("price"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void testUpdateProduct() throws SQLException {
        InventoryManagement.updateProduct(connection, 1, "UpdatedProduct", 150, 75);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM products WHERE id = 1")) {
            assertTrue(resultSet.next());
            assertEquals("UpdatedProduct", resultSet.getString("name"));
            assertEquals(150, resultSet.getInt("quantity"));
            assertEquals(75, resultSet.getInt("price"));
        }
    }

    public void testSearchProduct() {
        InventoryManagement.addProduct(connection, "SearchProduct", 300, 150);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT id FROM products WHERE name = 'SearchProduct'")) {
            assertTrue(resultSet.next());
            int productId = resultSet.getInt("id");

            InventoryManagement.searchProduct(connection, productId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}