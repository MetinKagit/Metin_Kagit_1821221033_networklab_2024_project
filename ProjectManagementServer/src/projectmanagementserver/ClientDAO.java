/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectmanagementserver;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import org.json.JSONObject;

/**
 *
 * @author metinkagit
 */
public class ClientDAO {

    static String connectionUrl = "jdbc:mysql://network-db.mysql.database.azure.com:3306";
    static String DBuser = "networkRoot";
    static String DBPassword = "13579_Metin";

    public static boolean insertUserIntoDatabase(JSONObject jsonObject, ServerClient serverClient) throws IOException {
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");

        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(connectionUrl, DBuser, DBPassword);
            // Prepare a statement to insert user into the database
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO networkdb.users (name, password) VALUES (?, ?)");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Execute the statement
            int rowsAffected = preparedStatement.executeUpdate();

            // Close the connection and statement
            preparedStatement.close();
            connection.close();
            System.out.println("Log 4");
            if (rowsAffected > 0) {
                System.out.println("Log 44");
                jsonObject.put("processDone", "true");
                serverClient.SendMessage(jsonObject);
                //serverClient.writer.flush();
            } else {
                System.out.println("Log 45");
                serverClient.SendMessage(jsonObject);
                //serverClient.writer.flush();
            }
            // Check if the insertion was successful
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean authenticateUser(JSONObject jsonObject, ServerClient serverClient) throws IOException {
        boolean isAuthenticated = false;
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");

        try {
            // Establish database connection
            try (Connection connection = DriverManager.getConnection(connectionUrl, DBuser, DBPassword)) {
                // Create a prepared statement to query the database for the user
                String query = "SELECT * FROM networkdb.users WHERE name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            // Get the stored password from the database
                            String storedPassword = resultSet.getString("password");
                            String storedClientName = resultSet.getString("name");
                            System.out.println("--" + storedClientName);
                            System.out.println("---" + storedPassword);
                            // Check if the provided password matches the stored password
                            if (storedPassword.equals(password)) {
                                System.out.println("log88");
                                isAuthenticated = true;
                                // Passwords match, authentication successful
                                // Send authentication result to client
                                jsonObject.put("processDone", "true");
                                serverClient.SendMessage(jsonObject);
                            }else{
                                System.out.println("log89");
                                serverClient.SendMessage(jsonObject);
                                return isAuthenticated;
                            }
                        }
                    }
                }
            }

            return isAuthenticated;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
