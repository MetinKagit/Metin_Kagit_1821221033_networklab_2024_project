/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projectmanagementclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author metinkagit
 * 1821221033
 */
public class Client extends Thread {

    int clientId;
    BufferedReader reader;
    PrintWriter writer;
    String username;
    String password;
    Socket socket;
    OutputStream output;
    InputStream input;
    String serverAddress;
    int serverPort;
    boolean isListening;
    boolean process;
    String[] projects;

    public Client(String serverAddress, int serverPort, String username, String password) throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.username = username;
        this.password = password;
        this.isListening = false;
        this.process = false;
    }

    public void Listen() {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.output = socket.getOutputStream();
            this.input = socket.getInputStream();
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(this.output, true);
            this.isListening = true;
            this.start();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Stop() {
        try {
            this.isListening = false;
            this.input.close();
            this.output.close();
            this.socket.close();
            this.writer.close();
            this.reader.close();
        } catch (IOException ex) {

        }
    }

    public String receiveMessage() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        return reader.readLine();
    }

    public void SendMessage(byte[] messageBytes) throws IOException {

        this.output.write(messageBytes);

    }

    public void ClientRegisteration(JSONObject jsonObject) throws IOException {
        // Convert JSON object to string
        String jsonString = jsonObject.toString();

        // Send JSON string to server
        this.writer = new PrintWriter(this.output, true);
        writer.println(jsonString);

        // Create a new thread to read the response from the server
        Thread responseThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String message = this.reader.readLine();
                    JSONObject responseJsonObject = new JSONObject(message);

                    String code = responseJsonObject.getString("code");
                    String response = responseJsonObject.getString("processDone");
                    System.out.println("Code: " + code + "processDone: " + response);

                    if (code.equals("000") && response.equals("true")) {
                        this.process = true;
                        Thread.currentThread().interrupt(); // Interrupt the thread after reading the data
                        break;
                    } else if (code.equals("000") && response.equals("false")) {
                        this.process = false;
                        Thread.currentThread().interrupt(); // Interrupt the thread after reading the data
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("No data available to read.");
                // Handle the case where no data is available
            }
        });

        responseThread.start();

        try {
            responseThread.join(); // Wait for the responseThread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ClientLogin(JSONObject jsonObject) throws IOException {
        // Convert JSON object to string
        String jsonString = jsonObject.toString();

        // Send JSON string to server
        this.writer = new PrintWriter(this.output, true);
        writer.println(jsonString);

        Thread loginResponseThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String message = this.reader.readLine();
                    JSONObject responseJsonObject = new JSONObject(message);

                    String code = responseJsonObject.getString("code");
                    String response = responseJsonObject.getString("processDone");
                    String userId = responseJsonObject.getString("userId");
                    this.clientId = Integer.parseInt(userId);

                    JSONArray projectArray = new JSONArray();
                    if (responseJsonObject.has("projectArray")) {
                        projectArray = responseJsonObject.getJSONArray("projectArray");
                        System.out.println("Project Array: " + projectArray.toString());
                    }

                    this.projects = new String[projectArray.length()];
                    for (int i = 0; i < projectArray.length(); i++) {
                        this.projects[i] = projectArray.getString(i);
                        System.out.println("Project Array: " + this.projects[i]);
                    }
                    for (String project : this.projects) {
                        String[] projectDetails = project.split(", ");
                        String id = projectDetails[0];
                        String title = projectDetails[1];
                        String key = projectDetails[2];
                        //Add 'tems to jList
                        Frm_HomePage.lst_userProjects_model.addElement(id + "  |  " + title + "  |  " + key);
                    }
                    System.out.println("log11");
                    // Print the Java array

                    // System.out.println("----Code: " + code + ", processDone: " + response + "Projects: " + projectArray);
                    System.out.println("log12");
                    if (code.equals("001") && response.equals("true")) {
                        //GetProjectByUser(responseJsonObject);
                        this.process = true;
                        Thread.currentThread().interrupt(); // Interrupt the thread after reading the data
                        break;
                    } else if (code.equals("001") && response.equals("false")) {
                        this.process = false;
                        Thread.currentThread().interrupt(); // Interrupt the thread after reading the data
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("No data available to read.");
                // Handle the case where no data is available
            }
        });
        loginResponseThread.start();
        try {
            loginResponseThread.join(); // Wait for the responseThread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void CreateProject(JSONObject jsonObject) {
        String jsonString = jsonObject.toString();

        // Send JSON string to server
        this.writer = new PrintWriter(this.output, true);
        writer.println(jsonString);

        // Create a new thread to read the response from the server
        Thread createProjectThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String message = this.reader.readLine();
                    JSONObject responseJsonObject = new JSONObject(message);

                    String code = responseJsonObject.getString("code");
                    String response = responseJsonObject.getString("processDone");
                    String projectId = responseJsonObject.getString("projectId");
                    String title = responseJsonObject.getString("title");
                    String key = responseJsonObject.getString("projectKey");
                    System.out.println("Code: " + code + " processDone: " + response + " projectId: " + projectId);

                    if (code.equals("002") && response.equals("true")) {
                        Frm_HomePage.lst_userProjects_model.addElement(projectId + "  |  " + title + "  |  " + key);
                        this.process = true;
                        Thread.currentThread().interrupt(); // Interrupt the thread after reading the data
                        break;
                    } else if (code.equals("020") && response.equals("false")) {
                        this.process = false;
                        Thread.currentThread().interrupt(); // Interrupt the thread after reading the data
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("No data available to read.");
                // Handle the case where no data is available
            }
        });

        createProjectThread.start();

        try {
            createProjectThread.join(); // Wait for the responseThread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void JoinProject(JSONObject jsonObject) {
        String jsonString = jsonObject.toString();

        // Send JSON string to server
        this.writer = new PrintWriter(this.output, true);
        writer.println(jsonString);

        // Create a new thread to read the response from the server
        Thread createProjectThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String message = this.reader.readLine();
                    JSONObject responseJsonObject = new JSONObject(message);

                    String code = responseJsonObject.getString("code");
                    String response = responseJsonObject.getString("processDone");
                    int projectId = responseJsonObject.getInt("projectId");
                    String title = responseJsonObject.getString("title");
                    String key = responseJsonObject.getString("projectKey");
                    System.out.println("Code: " + code + " processDone: " + response + " projectId: " + projectId);

                    if (code.equals("003") && response.equals("true")) {
                        Frm_HomePage.lst_userProjects_model.addElement(projectId + "  |  " + title + "  |  " + key);
                        this.process = true;
                        Thread.currentThread().interrupt(); // Interrupt the thread after reading the data
                        break;
                    } else if (code.equals("003") && response.equals("false")) {
                        this.process = false;
                        Thread.currentThread().interrupt(); // Interrupt the thread after reading the data
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("No data available to read.");
                // Handle the case where no data is available
            }
        });

        createProjectThread.start();

        try {
            createProjectThread.join(); // Wait for the responseThread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void OpenProject(JSONObject jsonObject) {
        String jsonString = jsonObject.toString();

        // Send JSON string to server
        this.writer = new PrintWriter(this.output, true);
        writer.println(jsonString);

        Thread projectResponseThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String message = this.reader.readLine();
                    JSONObject responseJsonObject = new JSONObject(message);

                    String code = responseJsonObject.getString("code");
                    String response = responseJsonObject.getString("processDone");
                    System.out.println("Code: " + code + "processDone: " + response);

                    if (code.equals("004") && response.equals("true")) {
                        JSONArray memberArray = new JSONArray();
                        if (responseJsonObject.has("memberArray")) {
                            memberArray = responseJsonObject.getJSONArray("memberArray");
                        }
                        // clear the list to avoid duplication
                        Frm_ProjectPage.lst_teamMembers_model.clear();
                        for (int i = 0; i < memberArray.length(); i++) {
                            String member = memberArray.getString(i);
                            String[] memberDetails = member.split(", ");
                            String id = memberDetails[0];
                            String name = memberDetails[1];
                            System.out.println(id + "  |   " + name);
                            Frm_ProjectPage.lst_teamMembers_model.addElement(id + "  |  " + name);
                        }
                        this.process = true;
                        Thread.currentThread().interrupt(); // Interrupt the thread after reading the data
                        break;
                    } else if (code.equals("004") && response.equals("false")) {
                        this.process = false;
                        Thread.currentThread().interrupt(); // Interrupt the thread after reading the data
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("No data available to read.");
            }
        });

        projectResponseThread.start();

        try {
            projectResponseThread.join(); // Wait for the responseThread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void GetMessages(JSONObject jsonObject) {

        String jsonString = jsonObject.toString();

        // Send JSON string to server
        this.writer = new PrintWriter(this.output, true);
        writer.println(jsonString);

        Thread getMessageResponseThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String message = this.reader.readLine();
                    JSONObject responseJsonObject = new JSONObject(message);

                    String code = responseJsonObject.getString("code");
                    String response = responseJsonObject.getString("processDone");
                    System.out.println("Code: " + code + " processDone: " + response);

                    if (code.equals("006") && response.equals("true")) {
                        JSONArray messageArray = new JSONArray();
                        if (responseJsonObject.has("messageArray")) {
                            messageArray = responseJsonObject.getJSONArray("messageArray");
                        }
                         Frm_HomePage.lst_messageBox_model.clear();
                        for (int i = 0; i < messageArray.length(); i++) {
                            String messageParts = messageArray.getString(i);
                            String[] parts = messageParts.split(",", 2);
                            String name = parts[0];
                            String messageBody = parts.length > 1 ? parts[1] : "";
                            
                            Frm_HomePage.lst_messageBox_model.addElement(name + "  :  " + messageBody);
                        }
                        this.process = true;
                        Thread.currentThread().interrupt(); // Interrupt the thread after reading the data
                        break;
                    } else if (code.equals("006") && response.equals("false")) {
                        this.process = false;
                        Thread.currentThread().interrupt(); // Interrupt the thread after reading the data
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("No data available to read.");
            }
        });

        getMessageResponseThread.start();

        try {
            getMessageResponseThread.join(); // Wait for the responseThread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void SendMessage(JSONObject jsonObject) {
        String jsonString = jsonObject.toString();

        // Send JSON string to server
        this.writer = new PrintWriter(this.output, true);
        writer.println(jsonString);
    }

    @Override
    public void run() {
        //try {
        while (this.isListening) {
        }
    }

}
