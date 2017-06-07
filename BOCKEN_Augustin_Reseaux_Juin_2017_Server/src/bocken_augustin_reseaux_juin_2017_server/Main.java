/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bocken_augustin_reseaux_juin_2017_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 *
 * @author E130486
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String argv[])throws UnknownHostException, InterruptedException, IOException{
        
        ServerSocket welcomeSocket = new ServerSocket(12345);;
        ArrayList<Thread> clients = new ArrayList<Thread>();
        ArrayList<Thread> games = new ArrayList<>();
        Player previous = null;
        while (true) {
         Socket connectionSocket = welcomeSocket.accept();
         Player p = new Player(connectionSocket);
                  
         Thread t = new Thread(p);
         p.setIdentifier("Player"+clients.size());
         clients.add(t);
         t.start();
         
         
         System.out.println("New "+p.getIdentifier()+" connected !");
         if(clients.size()%2 == 0){
             Thread.sleep(100);
             Game g = new Game(previous,p);
             Thread gt = new Thread(g);
             gt.start();
             games.add(gt);
             System.out.println("New game Started with players "+previous.getIdentifier()+" and "+p.getIdentifier());
         }
         previous = p;
        }
    }
    
}
