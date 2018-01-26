
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class ClientWriter implements Runnable {
    
    Client parent;
    int id;
    
    public ClientWriter(Client parent, int id){
        this.parent = parent;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            String pedido;
            String resposta;
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            
            Socket clientSocket = new Socket("localhost", 2626+id);
            
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            pedido = Integer.toString(parent.id);
            outToServer.writeBytes(pedido + "\n");
            
            resposta = inFromServer.readLine();
            //System.out.println("FROM SERVER: " + resposta);
            clientSocket.close();
            
            if(resposta.equals("1")) {
                parent.permissao[id] = 1;
            }
            else {
                parent.permissao[id] = -1;
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientWriter.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        }
    }
}