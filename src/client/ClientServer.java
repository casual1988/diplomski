/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Aleksandar
 */
public class ClientServer extends Thread{
    
    
    private Socket sock;
    private BufferedReader in;
    private PrintWriter out;
    
    
    
     public void  run(){//acceptClientMessage(){
        MessageData msg = new MessageData();
try {
// slušaj zahteve na datom portu, message port
int port=9001;
ServerSocket ss = new ServerSocket(port);
    System.out.println("pokreut klijentski server");
while (true) {
 sock = ss.accept();
 in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
 out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
String request; 
//while ((request = in.readLine()) != null) {
//        System.out.println(request);
//    out.println("haloo klijenntu 2 ,primio sam poruku");
//   }
  
//  msg.setIn(in);
 // msg.setOut(out);
          
 // WriterThread writer = new WriterThread(out, msg);        
  ReaderThread reader = new ReaderThread(sock, in, msg);
       
      }
} catch (Exception ex) {
ex.printStackTrace();
}
 //return msg;
}

    public Socket getSock() {
        return sock;
    }

    public void setSock(Socket sock) {
        this.sock = sock;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }
     
     
}
