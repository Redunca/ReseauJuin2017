/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bocken_augustin_reseaux_juin_2017_server;

import java.io.IOException;
import java.util.Random;

/**
 *
 * @author E130486
 */
public class Game implements Runnable{

    
    private Player[] players = new Player[2];
    private boolean isWon = false;
    private boolean isPlayer1Turn = true;
    private boolean[][] eggs = new boolean[4][4];
    public Game(Player p1,Player p2){
        players[0] = p1;
        players[1] = p2;
        p1.setGame(this);
        p2.setGame(this);
        int cpt = 0;
        Random ran = new Random();
        while(cpt <9){
            int x,y;
            x = ran.nextInt(4);
            y = ran.nextInt(4);
            if(!eggs[x][y]){
                cpt++;
                eggs[x][y] = true;
            }
        }
    }
    @Override
    public void run() {
        String startMessage = "GO "+players[0].getIdentifier()+" "+players[1].getIdentifier()+ '\n';
        try{
            players[0].getOutToClient().writeBytes(startMessage);
            players[1].getOutToClient().writeBytes(startMessage);
        }
        catch(IOException ex){
            System.err.println(ex.getMessage());
        }
        NextTurn();
        while(!isWon){/*
            try{
                int player;
                if(isPlayer1Turn){
                    player = 0;
                }
                else{
                    player = 1;
                }
                players[player].getOutToClient().writeBytes("YOU\n");
            }catch(IOException ex){
                System.err.println(ex.getMessage());
            }*/
            
        }
    }
    private void NextTurn(){
        try{
            if(!isWon){
                GetPlayerTurn().getOutToClient().writeBytes("YOU\n");
                System.out.println("Turn of "+GetPlayerTurn().getIdentifier());
            }
            else{
                System.out.println("Game ended.");
                if(players[0].getScore()>players[1].getScore()){
                    for(Player p : players){
                        p.getOutToClient().writeBytes("WIN "+players[0].getIdentifier()+"\n");
                    }
                }
                else if (players[0].getScore()<players[1].getScore()){
                    for(Player p : players){
                        p.getOutToClient().writeBytes("WIN "+players[1].getIdentifier()+"\n");
                    }
                }
                else{
                    for(Player p : players){
                        p.getOutToClient().writeBytes("WIN "+"Nobody"+"\n");
                    }
                }
            }
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
    }
    private Player GetPlayerTurn(){
        int player;
        if(isPlayer1Turn){
            player = 0;
        }
        else{
            player = 1;
        }
        return players[player];
    }
    boolean CheckZone(int zone) {
        System.out.println("Zone to be checked : "+zone);
        zone--;
        
        System.out.println("Coordonates : "+zone/4+":"+zone%4);
        boolean ok = eggs[zone/4][zone%4];
        System.out.println("Egg : "+ok);
        if(ok){
            if(GetPlayerTurn().IncrementScoreAndIsOver()){
                isWon = true;
            }
        }
        isPlayer1Turn = !isPlayer1Turn;
        NextTurn();
        return ok;
    }

    void ForceQuit() {
        if(!isWon){
            isWon = true;
            NextTurn();
        }
    }
    
}
