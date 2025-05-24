package com.example;
import static org.junit.jupiter.api.Assertions.*;

import com.example.service.InventoryServiceImpl;
import com.example.util.DBConnectionUtil;
import com.example.entity.Inventory;
import com.example.service.InventoryService;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.*;


public class AppTest {

    InventoryService inS = new InventoryServiceImpl();


    // Test Case 1: File Existence Check
    @Test
    public void test_Util_File_Exist() {
        File file = new File("src/main/java/com/example/util/DBConnectionUtil.java");
        assertTrue(file.exists(), "DBConnectionUtil.java file should exist");
    }

    // Test Case 2: Folder Structure Validation
    @Test
    public void test_Util_Folder_Exist() {
        File folder = new File("src/main/java/com/example/util");
        assertTrue(folder.exists() && folder.isDirectory(), "util folder should exist in the specified path");
    }

    // Test Case 3: Method Existence Check in Service Implementation
    @Test
    public void test_Check_Method_Exist() {
        try {
            InventoryServiceImpl.class.getDeclaredMethod("addItem", Inventory.class);
            InventoryServiceImpl.class.getDeclaredMethod("deleteItem", int.class);
            InventoryServiceImpl.class.getDeclaredMethod("getAllItems");
            assertTrue(true, "All required methods exist.");
        } catch (NoSuchMethodException e) {
            fail("One or more required methods are missing: " + e.getMessage());
        }
    
    
    }

    // Test Case 4: Create Item Query Execution - pseudo test
    @Test
    public void test_Create_Query_Exist() throws Exception {
        Inventory in = new Inventory(1, "Idli", "Breakfast", 2, 40.6);
        String result = inS.addItem(in);

        assertEquals("Item added successfully.", result, "Item should be added successfully.");

        // Verify in database
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM inventory WHERE itemName = ?")) {
            stmt.setString(1, "Idli");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), "Item should exist in the database.");
        }
    }

    // Test Case 5: Delete Item Query Execution - pseudo test
    @Test
    public void test_Delete_Query_Exist() throws Exception {
        Inventory in = new Inventory(10, "Test Delete", "Meals", 4, 99.9);
        inS.addItem(in);

        // Get the order ID
        int itemId;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT itemId FROM inventory WHERE itemName = ?")) {
            stmt.setString(1, "Test Delete");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                itemId = rs.getInt("itemId");
            } else {
                fail("Item not found in DB after insertion.");
                return;
            }

        }

        //Delete the order
        String result = inS.deleteItem(itemId);
        assertEquals("Item deleted successfully.", result, "Item should be deleted successfully.");

        // Verify deletion
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM inventory WHERE itemId = ?")) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            assertFalse(rs.next(), "Item should not exist in the database after deletion.");
        }
    }
}
