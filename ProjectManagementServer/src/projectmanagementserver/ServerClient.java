/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectmanagementserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author metinkagit 
 * 1821221033
 */
public class ServerClient extends Thread {

    BufferedReader reader;
    PrintWriter writer;
    Socket socket;
    Server server;
    OutputStream output;
    InputStream input;
    String username;
    String password;
    boolean isListening;

    public ServerClient(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.output = socket.getOutputStream();
        this.input = socket.getInputStream();
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(this.output, true);
        this.isListening = false;
        System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "-> Connected.");
    }

    public void Listen() throws IOException {
        this.isListening = true;
        this.start();
    }

    public void Stop() {
        try {
            this.isListening = false;
            this.input.close();
            this.output.close();
            this.socket.close();
            this.writer.close();
            this.reader.close();
            //this.server.RemoveClient(this);
        } catch (IOException ex) {
            System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "-> Stopped.");
        }
    }

    public void SendMessage(JSONObject jsonObject) throws IOException {
        if (this.writer == null) {
            throw new IOException("PrintWriter is not initialized");
        }
        String jsonString = jsonObject.toString();
        //this.writer = new PrintWriter(this.output, true);
        this.writer.println(jsonString);
//        this.writer.write(message);
//        this.writer.flush(); // Flush the output stream to send data immediately
    }

    public void RegisterProcess(JSONObject jsonObject) throws IOException {

        ServerClient serverClient = new ServerClient(this.socket, this.server);
        ClientDAO.insertUserIntoDatabase(jsonObject, serverClient);
    }

    public void LoginProcess(JSONObject jsonObject) throws IOException {

        ServerClient serverClient = new ServerClient(this.socket, this.server);
        ClientDAO.authenticateUser(jsonObject, serverClient);
    }

    public void CreateProjectProcess(JSONObject jsonObject) throws IOException {

        ServerClient serverClient = new ServerClient(this.socket, this.server);
        ProjectDAO.CreateProject(jsonObject, serverClient);
    }

    public void JoinProjectProcess(JSONObject jsonObject) throws IOException {

        ServerClient serverClient = new ServerClient(this.socket, this.server);
        String memberId = jsonObject.getString("member_id");
        String project_key = jsonObject.getString("key");
        boolean isManager = jsonObject.getBoolean("isManager");

        Project project = ProjectDAO.getProjectByKey(project_key);

        ProjectDAO.insertProjectMember(project.getProjectId(), Integer.parseInt(memberId), isManager);

        jsonObject.put("projectId", project.getProjectId());
        jsonObject.put("projectKey", "-");
        jsonObject.put("title", project.getTitle());
        jsonObject.put("processDone", "true");

        serverClient.SendMessage(jsonObject);

    }

    public void OpenProjectProcess(JSONObject jsonObject) throws IOException {

        try {
            ServerClient serverClient = new ServerClient(this.socket, this.server);
            int projectId = jsonObject.getInt("project_id");
            int usertId = jsonObject.getInt("user_id");

            String[] teamMembers = ProjectDAO.getTeamMembersByProjectId(projectId, usertId);
            System.out.println("Team Members:");
            for (String member : teamMembers) {
                System.out.println(member);
            }

            JSONArray membersJsonArray = new JSONArray();
            System.out.println("Team Members:");
            for (String member : teamMembers) {
                membersJsonArray.put(member);
                System.out.println(member);
            }
            jsonObject.put("memberArray", membersJsonArray);
            jsonObject.put("processDone", "true");
            serverClient.SendMessage(jsonObject);
        } catch (SQLException ex) {
            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void SendMessageProcess(JSONObject jsonObject) throws IOException {

        try {
            ServerClient serverClient = new ServerClient(this.socket, this.server);
            int receiverId = jsonObject.getInt("receiver_id");
            int sendertId = jsonObject.getInt("sender_id");
            String message = jsonObject.getString("message");
            MessageDAO.sendMessage(receiverId, sendertId, message);
        } catch (SQLException ex) {
            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void GetMessagesByUserId(JSONObject jsonObject) throws IOException {

        try {
            ServerClient serverClient = new ServerClient(this.socket, this.server);
            int userId = jsonObject.getInt("user_id");

            String[] messages = MessageDAO.getMessagesByUserId(userId);
            System.out.println("Message Query:");

            JSONArray messagesJsonArray = new JSONArray();

            for (String message : messages) {
                messagesJsonArray.put(message);
                System.out.println(message);
            }

            jsonObject.put("messageArray", messagesJsonArray);
            jsonObject.put("processDone", "true");
            serverClient.SendMessage(jsonObject);
            
            
        } catch (SQLException ex) {
            Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {
        try {
            while (this.isListening) {
                this.reader = new BufferedReader(new InputStreamReader(this.input));
                String jsonString = reader.readLine();
                if (jsonString != null) {
                    System.out.println("Received JSON string from client: " + jsonString);

                    JSONObject jsonObject = new JSONObject(jsonString);
                    String code = jsonObject.getString("code");

                    System.out.println("code : " + code);
                    if (code.equals("000")) {
                        System.out.println("Registeration process...");
                        RegisterProcess(jsonObject);
                    } else if (code.equals("001")) {
                        System.out.println("Login process...");
                        LoginProcess(jsonObject);
                    } else if (code.equals("002")) {
                        System.out.println("Project creating...");
                        CreateProjectProcess(jsonObject);
                    } else if (code.equals("003")) {
                        System.out.println("Joining the project...");
                        JoinProjectProcess(jsonObject);
                    } else if (code.equals("004")) {
                        System.out.println("Open project page");
                        OpenProjectProcess(jsonObject);
                    } else if (code.equals("005")) {
                        System.out.println("Message sending...");
                        SendMessageProcess(jsonObject);
                    } else if (code.equals("006")) {
                        System.out.println("Messages fetching...");
                        GetMessagesByUserId(jsonObject);
                    }
                }

            }
        } catch (IOException ex) {
            this.Stop();

            System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "-> Closed.");
            //Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
