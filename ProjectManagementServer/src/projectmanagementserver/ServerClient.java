/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectmanagementserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author metinkagit 1821221033
 */
public class ServerClient extends Thread {

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
        this.isListening = false;
        System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "-> Connected.");
    }

    public void Listen() {
        this.isListening = true;
        this.start();
    }

    public void Stop() {
        try {
            this.isListening = false;
            this.input.close();
            this.output.close();
            this.socket.close();
            //this.server.RemoveClient(this);
        } catch (IOException ex) {
            System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "-> Stopped.");
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
                String message = (new String(bytes, StandardCharsets.UTF_8));
                System.out.println("Message is:" + message);
                System.out.println("------ ------ ------");

                String[] parts = message.split(",");
                String code = parts[0];

                if (code.equals("000")) {
                    System.out.println("Log 2");
                    String username = parts[1];
                    String password = parts[2];
                    if (DBLayer.insertUserIntoDatabase(username, password)) {
                      ServerClient ser = new ServerClient(this.socket, this.server);
                      String a = (" " + "true");
                      ser.SendMessage(a.getBytes());
                    }
                } else if (code.equals("001")) {

                }
            }
        } catch (IOException ex) {
            this.Stop();

            System.out.println(this.socket.getInetAddress().toString() + ":" + this.socket.getPort() + "-> Closed.");
            //Logger.getLogger(ServerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
