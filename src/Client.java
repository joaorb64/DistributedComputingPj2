
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client implements Runnable {
    
    public static Client[] lista;
    
    public int id;
    public Random rng;
    public long startTimeMillis;
    
    public ClientListener listener;
    public ClientWriter writer;
    public ClientWindow window;
    
    int[] permissao;
    
    boolean imprimindo;
    boolean esperandoResposta;
    
    public Client(int id) throws IOException{
        this.id = id;
        rng = new Random();
        startTimeMillis = System.currentTimeMillis();
        
        imprimindo = false;

        window = new ClientWindow();
        window.setVisible(true);
    }
    
    public void Initialize() throws IOException{
        permissao = new int[Client.lista.length];
        
        listener = new ClientListener(this);
        new Thread(listener).start();      
        
        new Thread(this).start();
    }

    @Override
    public void run() {
        float randNum = 5;
        boolean changed = true;
        
        esperandoResposta = false;

        while(true){
            if(esperandoResposta == false){
                if((System.currentTimeMillis() - startTimeMillis) >= 3000){
                    randNum = rng.nextFloat();
                    startTimeMillis = System.currentTimeMillis();
                    changed = true;
                }
                
                if(randNum <= 0.5f) {
                    Arrays.fill(permissao, 0);
                
                    for(int i = 0; i < Client.lista.length; i++){
                        if(Client.lista[i] != this){
                            new Thread(new ClientWriter(this, i)).start();
                        }
                    }

                    esperandoResposta = true;
                }
            }
            
            String texto = "";
            
            texto += "id: "+id+"\nport: " + (2626+id) + "\n"+"Numero aleatorio:"+randNum;
            texto += "\n";

            for(int i = 0; i < permissao.length; i++){
                if(Client.lista[i] == this){
                   texto += "[me] ";
                }
                else{
                    texto += "["+ Integer.toString(permissao[i]) + "] ";
                }
            }
            
            if(imprimindo == true){
                texto += "\n[IMPRIMINDO]";
                window.jTextArea1.setText(texto);
                
                ConectarEImprimir();
                
                imprimindo = false;
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                continue;
            }
            
            window.jTextArea1.setText(texto);
            
            // se esperando resposta e todo mundo respondeu...
            boolean todosResponderam = true;
            
            for(int i = 0; i < permissao.length; i++){
                if(permissao[i] == 0 && Client.lista[i] != this){
                    todosResponderam = false;
                    break;
                }
            }
            
            if(esperandoResposta == true && todosResponderam == true){
                boolean liberado = true;
                
                for(int i = 0; i < permissao.length; i++){
                    if(permissao[i] == -1 && Client.lista[i] != this){
                        liberado = false;
                        break;
                    }
                }
                
                if(liberado == true){
                    System.out.println("POSSO!!!");
                    imprimindo = true;
                }
                
                esperandoResposta = false;
            }
        }
    }
    
    public void ConectarEImprimir(){
        try {
            String pedido;
            String resposta;
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            
            Socket clientSocket = new Socket("localhost", 2625);
            
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            pedido = "0123456789";
            outToServer.writeBytes(pedido + "\n");
            
            resposta = inFromServer.readLine();
            System.out.println("FROM PRINTER: " + resposta);
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientWriter.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        }
    }
}
