/*
 * Gomoku - Mouad Douieb
 */

package gomoku;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JFrame;


public class Serveur extends JFrame {
    private Hashtable<Socket,DataOutputStream> lesClients =new Hashtable<>();
    
    public Serveur(int port){
        ecouter(port);
    }

    private void ecouter(int port) {
       try{
		   ServerSocket ss= new ServerSocket(port);
		   System.out.println("En ecoute sur :"+ss);
		   while(true){
			    Socket s= ss.accept();
			   System.out.println("Connextion de :"+s);
			   DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			   lesClients.put(s,dout);
			   new ServeurThread(s,this).start();
			   System.out.println(s);

			}
       } catch(Exception e){
           System.out.println("erreure dans la boucle d'écoute :"+e);
       }
    }
    
    public void envoyer(int ligne, int col){
        synchronized(lesClients) {
            for(Enumeration enm=lesClients.elements();enm.hasMoreElements();){
              try{
                 DataOutputStream dout =(DataOutputStream) enm.nextElement();
                 dout.writeInt(ligne);
                 dout.writeInt(col);
              } catch(Exception ex) {
                 System.out.println("erreure dans le sendToAll\n"+ex); 
              }
            }
        }
     }

    public void deleteConnection(Socket s){
        synchronized(lesClients) {
           try {
               lesClients.remove(s);
             } catch(Exception e) {
                System.out.println("erreure dans le deleteConnection\n"+e); 
             }
       }
    }
    
    public static void main(String[] args) {
		
		new Serveur(9092);

    }

}
