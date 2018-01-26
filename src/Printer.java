
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author joao
 */
public class Printer implements Runnable {
    public PrinterWindow window;
    
    ServerSocket welcomeSocket;
    
    public Printer() throws IOException{
        window = new PrinterWindow();
        window.setVisible(true);

        welcomeSocket = new ServerSocket(2625);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                
                window.jTextArea1.setText("");
                
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                
                String clientSentence = inFromClient.readLine();
                
                for(int i = 0; i < clientSentence.length(); i++){
                    window.jTextArea1.append(""+clientSentence.charAt(i));
                    Thread.sleep(500);
                }

                outToClient.writeBytes("ack" + "\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
