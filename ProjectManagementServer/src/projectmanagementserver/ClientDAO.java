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
import org.json.JSONArray;
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
                            String userId = resultSet.getString("id");
                            

                            // Check if the provided password matches the stored password
                            if (storedPassword.equals(password)) {
                                System.out.println("log88");
                                isAuthenticated = true;
                                // Passwords match, authentication successful
                                // Send authentication result to client
                                jsonObject.put("userId", userId);
                                jsonObject.put("processDone", "true");

                                String[] projectArray = GetProjectByUser(jsonObject, serverClient);
                                JSONArray jsonArray = new JSONArray();
                                for (String str : projectArray) {
                                    jsonArray.put(str);
                                }
                                jsonObject.put("projectArray", jsonArray);
                                serverClient.SendMessage(jsonObject);
                            } else {
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

    public static String[] GetProjectByUser(JSONObject jsonObject, ServerClient serverClient) throws IOException {
        String[] projects = null;
        String userId = jsonObject.getString("userId");

        try (Connection connection = DriverManager.getConnection(connectionUrl, DBuser, DBPassword)) {
            // SQL query
            String sql = "SELECT networkdb.projects.project_id, "
                    + "networkdb.projects.title, "
                    + "networkdb.projects.project_key, "
                    + "networkdb.project_members.is_manager "
                    + "FROM networkdb.projects "
                    + "JOIN networkdb.project_members "
                    + "ON networkdb.projects.project_id = networkdb.project_members.project_id "
                    + "WHERE networkdb.project_members.member_id = ?";

            // Create a prepared statement        
            PreparedStatement preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, Integer.parseInt(userId));

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Count the number of rows in the result set
            int rowCount = 0;
            if (resultSet.last()) {
                rowCount = resultSet.getRow();
                resultSet.beforeFirst(); // Move the cursor back to before the first row
            }

            // Initialize the result array
            projects = new String[rowCount];

            // Process the result set
            int i = 0;
            while (resultSet.next()) {
                // Build the string representation of each row
                String projectKey = resultSet.getBoolean("is_manager") ? resultSet.getString("project_key") : "-";
                String row = resultSet.getInt("project_id") + ", "
                        + resultSet.getString("title") + ", "
                        + projectKey + ", "
                        + resultSet.getBoolean("is_manager");
                projects[i] = row;
                i++;
            }

            for (String row : projects) {
                System.out.println(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projects;
    }
}
