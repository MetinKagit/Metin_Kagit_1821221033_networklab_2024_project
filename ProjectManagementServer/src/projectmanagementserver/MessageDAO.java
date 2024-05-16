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
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import static projectmanagementserver.ProjectDAO.DBPassword;
import static projectmanagementserver.ProjectDAO.DBuser;
import static projectmanagementserver.ProjectDAO.connectionUrl;
/**
 *
 * @author metinkagit
 * 1821221033
 */
public class MessageDAO {
    static String connectionUrl = "jdbc:mysql://network-db.mysql.database.azure.com:3306";
    static String DBuser = "networkRoot";
    static String DBPassword = "13579_Metin";
    
    public static void sendMessage(int receiverId, int senderId, String Message) throws SQLException {

        try(Connection connection = DriverManager.getConnection(connectionUrl, DBuser, DBPassword)) {
            // SQL insert statement
            String sql = "INSERT INTO networkdb.messages (sender_id, receiver_id, message) VALUES (?, ?, ?)";

            // Create a prepared statement
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, senderId);
            preparedStatement.setInt(2, receiverId);
            preparedStatement.setString(3, Message);

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Message inserted successfully.");
            } else {
                System.out.println("Failed to insert message.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        
     public static String[] getMessagesByUserId(int userId) throws SQLException {
     List<String> resultList = new ArrayList<>();

        // SQL query
        String sql = "SELECT networkdb.users.name, networkdb.messages.message FROM networkdb.users " +
                     "JOIN networkdb.messages ON networkdb.messages.sender_id = networkdb.users.id " +
                     "WHERE networkdb.messages.receiver_id = ?";

        // Create a prepared statement
        try (Connection connection = DriverManager.getConnection(connectionUrl, DBuser, DBPassword)) {
            
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Process the result set
                while (resultSet.next()) {
                    String userName = resultSet.getString("name");
                    String message = resultSet.getString("message");
                    resultList.add(userName + ", " + message);
                }
            }
        }

        // Convert the list to an array
        String[] resultArray = new String[resultList.size()];
        resultArray = resultList.toArray(resultArray);

        return resultArray;
    }
     
}
