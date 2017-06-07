/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bocken_augustin_reseaux_juin_2017;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author E130486
 */
public class ServerListenner implements Runnable{

    private BufferedReader inFromServer;
    private String identifier;
    private int errorCount = 0;
    private GameView gameView;
    private int lastZoneChecked;
    private DataOutputStream outToServer;
    ServerListenner(BufferedReader bufferedReader,DataOutputStream outToServer) {
        inFromServer = bufferedReader;
        this.outToServer = outToServer;
    }

    @Override
    public void run() {
        while(errorCount < 3){
            try{
                System.out.println("Waiting for server...");
                String fromServer = inFromServer.readLine(); 
                System.out.println("Received "+fromServer);
                switch(analyse(fromServer)){
                    case Welcome:
                            identifier = fromServer.substring(8);
                            System.out.println("Received Welcome from Server. Id : "+identifier);
                        break;
                    case Found :
                        gameView.ChangeButtonColor(true, lastZoneChecked);
                        break;
                    case Go : 
                        String[] names = fromServer.split(" ");
                        if(identifier.equalsIgnoreCase(names[1])){
                            gameView = new GameView(names[1],names[2],this);
                        }
                        else{
                            gameView = new GameView(names[2],names[1],this);
                        }
                        gameView.SetEnabledButtons(false);
                        System.out.println("Window visible.");
                        break;
                    case Missed :
                        gameView.ChangeButtonColor(false, lastZoneChecked);
                        break;
                    case Win :
                        
                        JOptionPane.showMessageDialog(gameView,"Game Over !\n"+fromServer.substring(4)+" win the game.");
                        //System.exit(0);
                        break;
                    case You : 
                        System.out.println("Enable buttons");
                        gameView.SetEnabledButtons(true);
                        break;
                }
            
                errorCount = 0;
            }
            catch(IOException ex){
                System.err.println(ex.getMessage());
                errorCount++;
            }
        }
        
    }
    private static ResponseType analyse(String message){
        Pattern pattern = Pattern.compile("(WELCOME|GO|WIN|YOU|FOUND|MISSED)( ((\\S+)|(\\S+ ))*)*");
        Matcher m = pattern.matcher(message);
        if(m.matches()){
            switch(m.group(1)){
                case "WELCOME": 
                    System.out.println("Type WELCOME");
                    return ResponseType.Welcome;
                case "GO" : 
                    System.out.println("Type GO");
                    return ResponseType.Go;
                case "WIN" : 
                    System.out.println("Type WIN");
                    return ResponseType.Win;
                case "YOU" : 
                    System.out.println("Type YOU");
                    return ResponseType.You;
                case "FOUND" : 
                    System.out.println("Type FOUND");
                    return ResponseType.Found;
                case "MISSED":
                    System.out.println("Type MISSED");
                    return ResponseType.Missed;
            }
        }
        System.out.println("Type NONE");
        return ResponseType.None;
    }

    void SendLook(int number)  {
        try {
            lastZoneChecked = number;
            outToServer.writeBytes("LOOK "+number+"\n");
        } catch (IOException ex) {
            Logger.getLogger(ServerListenner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void SendQuit() {
        try {
            outToServer.writeBytes("QUIT\n");
        } catch (IOException ex) {
            Logger.getLogger(ServerListenner.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            System.exit(0);
        }
    }
    
    private enum ResponseType{
        Welcome,
        Go,
        Win,
        You,
        Found,
        Missed,
        None
    }
}
