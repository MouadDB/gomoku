/*
 * Gomoku - Mouad Douieb
 */
package gomoku;

import java.io.DataInputStream;
import java.net.Socket;


public class ServeurThread extends Thread {
    private Socket s;
    private Serveur serv;    
    //private DataInputStream dim;

   public ServeurThread(Socket s,Serveur serv) {
       this.s=s;       
       this.serv=serv;

   }
   public void run(){
       try{
           DataInputStream din= new DataInputStream(s.getInputStream());
         while(true){
        	int ligne = din.readInt();
			int col = din.readInt();
            serv.envoyer(ligne, col);
         }
       }catch(Exception e){
           System.out.println("Erreur dans la boucle d'ecoute du Thread\n"+e);
       }finally{
       serv.deleteConnection(s);
   }
   }
}
