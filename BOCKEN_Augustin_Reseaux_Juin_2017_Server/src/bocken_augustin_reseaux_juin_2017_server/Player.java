/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bocken_augustin_reseaux_juin_2017_server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.Timestamp;
import java.sql.Time;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 *
 * @author E130486
 */
public class Player implements Runnable{
    private Socket connectionSocket;
    private DataOutputStream outToClient;
    private String identifier;
    private Game game;
    private int Score;
    
    public Player(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        Score = 0;
    }
    public boolean IncrementScoreAndIsOver(){
        Score++;
        return getScore()==9;
    }
    
    @Override
    public void run() {
        String clientSentence;
        BufferedReader inFromClient = null;
        try{
            inFromClient = new BufferedReader(new InputStreamReader(getConnectionSocket().getInputStream()));
            setOutToClient(new DataOutputStream(getConnectionSocket().getOutputStream()));
            //System.out.println("Waiting..." +new Date().getTime());
            //Thread.sleep(2000);
            //System.out.println("Wait ended " +new Date().getTime());
            outToClient.writeBytes("WELCOME "+identifier+ '\n');
            System.out.println("Sending Welcome to "+identifier);
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        
        }
        try {
        while(true){
                clientSentence = inFromClient.readLine();
                System.out.println("Received: " + clientSentence);
                ProcessClientMessage(clientSentence);
        }
        } catch (IOException ex) {
                System.err.println(ex.getMessage());
            } 
    }
    private void ProcessClientMessage(String message){
       Pattern pattern = Pattern.compile("(LOOK|QUIT)( (\\d+)*)*");
       Matcher m = pattern.matcher(message);
       if(m.matches()){
           switch(m.group(1)){
               case "LOOK":
                   checkZone(Integer.parseInt(m.group(2).trim()));
                   break;
               case "QUIT":
                   game.ForceQuit();
                   break;
           }
       }
    }
    private void checkZone(int zone){
        try{
            if(game.CheckZone(zone)){
                outToClient.writeBytes("FOUND\n");
                System.out.println("Sending FOUND");
            }
            else{
                outToClient.writeBytes("MISSED\n");
                System.out.println("Sending MISSED");
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        
    }
    /**
     * @return the connectionSocket
     */
    public Socket getConnectionSocket() {
        return connectionSocket;
    }

    /**
     * @param connectionSocket the connectionSocket to set
     */
    public void setConnectionSocket(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    /**
     * @return the outToClient
     */
    public DataOutputStream getOutToClient() {
        return outToClient;
    }

    /**
     * @param outToClient the outToClient to set
     */
    public void setOutToClient(DataOutputStream outToClient) {
        this.outToClient = outToClient;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * @param game the game to set
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * @return the Score
     */
    public int getScore() {
        return Score;
    }
}
