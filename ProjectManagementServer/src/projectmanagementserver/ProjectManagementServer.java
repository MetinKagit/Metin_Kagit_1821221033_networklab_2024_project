/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package projectmanagementserver;

import java.lang.System.Logger;
import static java.lang.Thread.sleep;
import java.util.logging.Level;

/**
 *
 * @author metinkagit
 */
public class ProjectManagementServer {

    /**
     * @param args the command line arguments
     * @author metinkagit
     * 1821221033
     */
    public static void main(String[] args) {
        Server server = new Server(5000);
        System.out.println("Hello");
        server.Listen();
        while (server.isListening) {

            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(ProjectManagementServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
