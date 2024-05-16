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
import static projectmanagementserver.ClientDAO.DBPassword;
import static projectmanagementserver.ClientDAO.DBuser;
import static projectmanagementserver.ClientDAO.connectionUrl;

/**
 *
 * @author metinkagit
 */
public class ProjectDAO {

    static String connectionUrl = "jdbc:mysql://network-db.mysql.database.azure.com:3306";
    static String DBuser = "networkRoot";
    static String DBPassword = "13579_Metin";
    
    public static void CreateProject(JSONObject jsonObject, ServerClient serverClient) throws IOException {
        String managerId = jsonObject.getString("manager_id");
        String title = jsonObject.getString("title");
        String explanation = jsonObject.getString("explanation");
        boolean isManager = jsonObject.getBoolean("isManager");
        try (Connection connection = DriverManager.getConnection(connectionUrl, DBuser, DBPassword)) {

            if (isProjectTitleExists(connection, title)) {
                System.out.println("Project with the same title already exists.");
                jsonObject.put("code", "020");
                serverClient.SendMessage(jsonObject);
                return;
            }
            // Generate a unique project key
            String projectKey = generateProjectKey();

            // SQL insert statement
            String sql = "INSERT INTO networkdb.projects (manager_id, title, explanation, project_key) VALUES (?, ?, ?, ?)";
            
            // Create a prepared statement
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(managerId));
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, explanation);
            preparedStatement.setString(4, projectKey);
            
            
            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                
                System.out.println("Project inserted successfully.");
                jsonObject.put("processDone", "true");
                jsonObject.put("projectKey", projectKey);

                int projectId = getProjectIdByTitle(connection, title);
                System.out.println("-----pid: " + projectId);
                insertProjectMember(projectId,Integer.parseInt(managerId),isManager );
                jsonObject.put("projectId", String.valueOf(projectId));
                
                serverClient.SendMessage(jsonObject);
            } else {
                System.out.println("Failed to insert project.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
     

    public static void insertProjectMember(int projectId, int managerId, boolean isManager) {
        try (Connection connection = DriverManager.getConnection(connectionUrl, DBuser, DBPassword)){
            String memberSql = "INSERT INTO networkdb.project_members (project_id, member_id, is_manager) VALUES (?, ?, ?)";
            PreparedStatement memberPreparedStatement = connection.prepareStatement(memberSql);
            memberPreparedStatement.setInt(1, projectId);
            memberPreparedStatement.setInt(2, managerId);
            memberPreparedStatement.setBoolean(3, isManager);
            int rowsAffected = memberPreparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Project member inserted successfully.");
            } else {
                System.out.println("Failed to insert project member.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean isProjectTitleExists(Connection connection, String title) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM networkdb.projects WHERE title = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, title);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt("count");
        return count > 0;
    }

    public static int getProjectIdByTitle(Connection connection, String title) {
        int projectId = -1;
        try {
            // SQL query to retrieve project_id by title
            String sql = "SELECT project_id FROM networkdb.projects WHERE title = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, title);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                projectId = resultSet.getInt("project_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projectId;
    }

    // Method to generate a unique project key (you can implement your own logic here)
    public static String generateProjectKey() {
        // Example: generate a random alphanumeric string
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
    
     public static Project getProjectByKey(String key) {
        try (Connection connection = DriverManager.getConnection(connectionUrl, DBuser, DBPassword)) {
            // SQL query to retrieve project by ID
            String sql = "SELECT * FROM networkdb.projects WHERE project_key = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Project project = new Project();
                project.setProjectId(resultSet.getInt("project_id"));
                project.setTitle(resultSet.getString("title"));
                project.setExplanation(resultSet.getString("explanation"));
                project.setManagerId(resultSet.getInt("manager_id"));
                project.setProjectKey(resultSet.getString("project_key"));
                return project;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
