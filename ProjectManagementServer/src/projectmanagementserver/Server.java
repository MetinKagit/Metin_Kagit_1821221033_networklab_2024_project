/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectmanagementserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author metinkagit
 * 1821221033
 */
public class Server extends Thread{
    ServerSocket serverSocket;
    int port;
    boolean isListening;
    ArrayList<ServerClient> clientList;

    public Server(int port) {
        this.port = port;
        this.isListening = false;
        this.clientList = new ArrayList<>();
        if (isPortAvailable(this.port)) {
            try {
                this.serverSocket = new ServerSocket(this.port);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "Port " + this.port + " is already in use");
        }
    }
    
    private boolean isPortAvailable(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    public void Listen() {
        if (this.serverSocket != null) {
            this.isListening = true;
            this.start();
        }
    }

    public void Stop() {
        try {
            this.isListening = false;
            this.serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void AddClient(ServerClient serverClient) {
        this.clientList.add(serverClient);
    }
 
//    public void SendBroadcast(String message) throws IOException {
//        String broadcostMessage = (" " + message);
//        for (ServerClient client : clientList) {
//            client.SendMessage(broadcostMessage);
//        }
//    }

   

    @Override
    public void run() {
        while (this.isListening) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                ServerClient newClient = new ServerClient(clientSocket, this);
                this.AddClient(newClient);
                newClient.Listen();
                System.out.println("--ip: " + clientSocket.getInetAddress().toString());
                System.out.println("--port: " + clientSocket.getPort());
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
