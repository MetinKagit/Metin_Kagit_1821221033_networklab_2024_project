/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projectmanagementclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author metinkagit
 */
public class Client extends Thread {
    String username;
    String password;
    Socket socket;
    OutputStream output;
    InputStream input;
    String serverAddress;
    int serverPort;
    boolean isListening;

    public Client(String serverAddress, int serverPort, String username, String password) throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.username = username;
        this.password = password;
        this.isListening = false;
       
        //Frm_Server.lst_clients_model.addElement(this.socket.getInetAddress().toString() + ":" + this.socket.getPort());
    }

    public void Listen() {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.output = socket.getOutputStream();
            this.input = socket.getInputStream();
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
        } catch (IOException ex) {
           
        }
    }

    public void SendMessage(byte[] messageBytes) throws IOException {

        this.output.write(messageBytes);
        
    }

    @Override
    public void run() {
        try {
            while (this.isListening) {
                int byteSize = this.input.read();
                byte bytes[] = new byte[byteSize];
                this.input.read(bytes);

                System.out.println("------ ------ ------");
                System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "-> Message reacted.");
                System.out.println(new String(bytes, StandardCharsets.UTF_8));
                System.out.println("------ ------ ------");
            }
        } catch (IOException ex) {
            this.Stop();
           
            //Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
