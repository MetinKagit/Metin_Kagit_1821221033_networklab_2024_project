/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectmanagementserver;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
/**
 *
 * @author metinkagit
 */
public class DBLayer {
    static String connectionUrl = "jdbc:mysql://localhost:4545/TCPTest";
    static String DBuser = "root";
    static String DBPassword = "121212";
    
    public static boolean insertUserIntoDatabase(String username, String password) {
        boolean isProcessDone = false;
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(connectionUrl, DBuser, DBPassword);
            System.out.println("Log 3");
            // Prepare a statement to insert user into the database
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (Name, password) VALUES (?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Execute the statement
            int rowsAffected = preparedStatement.executeUpdate();

            // Close the connection and statement
            preparedStatement.close();
            connection.close();
            System.out.println("Log 4");
            // Check if the insertion was successful
            return isProcessDone;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public boolean authenticateUser( String username, String password) {
        boolean isAuthenticated = false;

        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection(connectionUrl, DBuser, DBPassword);

            // Create a prepared statement to query the database for the user
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            preparedStatement.setString(1, username);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if a user with the provided username exists
            if (resultSet.next()) {
                // Get the stored password from the database
                String storedPassword = resultSet.getString("password");

                // Check if the provided password matches the stored password
                if (storedPassword.equals(password)) {
                    isAuthenticated = true; // Passwords match, authentication successful
                }
            }

            // Close the database resources
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        
    }
}
