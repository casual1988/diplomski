/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.net.Socket;

/**
 *
 * @author student
 */
public class ReaderThread extends Thread {

    private Socket sock;
    private BufferedReader in;
    private MessageData data;

    public ReaderThread(Socket sock, BufferedReader in, MessageData data) {
        this.sock = sock;
        this.in = in;
        this.data = data;
        start();
    }

    public void run() {
        System.out.println("primljeno:");
        try {
            String msg;
            while (true) {
                msg = in.readLine();
                if (msg != null) {
                    System.out.println(msg + "\n");
                    
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
