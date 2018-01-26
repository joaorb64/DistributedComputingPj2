import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joao
 */
public class ClientListener implements Runnable  {
    
    Client parent;
    
    ServerSocket welcomeSocket;
    
    public int id;
    
    public ClientListener(Client parent) throws IOException{
        this.parent = parent;
        welcomeSocket = new ServerSocket(2626 + parent.id);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                
                String clientSentence = inFromClient.readLine();
                //System.out.println("Received: " + clientSentence);
                
                String resposta;
                
                if(parent.imprimindo == false && parent.esperandoResposta == false){
                    resposta = "1";
                }
                else{
                    resposta = "-1";
                }
                
                outToClient.writeBytes(resposta + "\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        }
    }
    
}
