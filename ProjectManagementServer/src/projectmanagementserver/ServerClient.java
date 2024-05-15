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
import org.json.JSONObject;

/**
 *
 * @author metinkagit 1821221033
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
                        System.out.println("Log 2");
                        RegisterProcess(jsonObject);

                    } else if (code.equals("001")) {
                        System.out.println("Log 22");
                        LoginProcess(jsonObject);
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
