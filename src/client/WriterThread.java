/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author student
 */
public class WriterThread extends Thread {

    private PrintWriter out;
    private MessageData msg;

    public WriterThread(PrintWriter out, MessageData msg) {
        this.out = out;
        this.msg = msg;
        start();
    }

    public void run() {
        System.out.println("posalji:");
        try {
            String message;
            while(true){
           message= msg.writeMessage();
           out.println(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   
}
