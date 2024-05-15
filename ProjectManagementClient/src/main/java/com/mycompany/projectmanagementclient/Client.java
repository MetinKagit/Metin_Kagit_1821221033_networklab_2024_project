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
import org.json.JSONObject;

/**
 *
 * @author metinkagit
 */
public class Client extends Thread {

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

    public Client(String serverAddress, int serverPort, String username, String password) throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.username = username;
        this.password = password;
        this.isListening = false;
        this.process = false;

        //Frm_Server.lst_clients_model.addElement(this.socket.getInetAddress().toString() + ":" + this.socket.getPort());
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
                    }else if (code.equals("000") && response.equals("false")) {
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
                    System.out.println("Code: " + code + ", processDone: " + response);

                    if (code.equals("001") && response.equals("true")) {
                        this.process = true;
                        Thread.currentThread().interrupt(); // Interrupt the thread after reading the data
                        break;
                    }else if (code.equals("001") && response.equals("false")) {
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

    @Override
    public void run() {
        //try {
        while (this.isListening) {
        }
    }

}
