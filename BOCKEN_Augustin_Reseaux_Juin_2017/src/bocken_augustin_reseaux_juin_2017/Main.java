/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bocken_augustin_reseaux_juin_2017;

import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author E130486
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String argv[]) throws IOException{
        String sentence = "";
        String ip ="localhost";
        if(argv.length == 1){
            Pattern p = Pattern.compile("(\\d+).(\\d+).(\\d+).(\\d+)");
            Matcher m = p.matcher(argv[0]);
            if(m.matches()){
                ip = argv[0];
            }
        }
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket(ip, 12345);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        ServerListenner serverListenner = new ServerListenner(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())),outToServer);
        Thread serverThread = new Thread(serverListenner);
        serverThread.start();
        while(!sentence.equalsIgnoreCase("QUIT")){
            sentence = inFromUser.readLine();
            outToServer.writeBytes(sentence + '\n');
        }
        clientSocket.close();
    }
    
}
