/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JTextArea;

/**
 *
 * @author Aleksandar
 */
public class ClientServer extends Thread{
    
    
    private Socket sock;
    private BufferedReader in;
    private PrintWriter out;
    private MessageData msg;
    private ClientForm frm;
    private ClientInfo currentClient;
  
    public ClientServer(){}
    
    public ClientServer(ClientForm frm, ClientInfo currentClient)
    {
     this.frm = frm;
     this.currentClient = currentClient;
    }
     public void  run(){//acceptClientMessage(){
         msg = new MessageData();
try {
// slušaj zahteve na datom portu, message port
//int port=9001;
ServerSocket ss = new ServerSocket(currentClient.getPort());
    System.out.println("Pokrenut klijentski server na portu" + currentClient.getPort());
while (true) {
 sock = ss.accept();
 in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
 out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
String request; 
InputStream is = sock.getInputStream();
OutputStream os = sock.getOutputStream();
//while ((request = in.readLine()) != null) {
//        System.out.println(request);
//    out.println("haloo klijenntu 2 ,primio sam poruku");
//   }
  
//  msg.setIn(in);
 // msg.setOut(out);
          
 // WriterThread writer = new WriterThread(out, msg);        
 // ReaderThread reader = new ReaderThread(sock, in, msg,frm);

    setClientOnSelectList(sock);
 ReaderThread reader = new ReaderThread(sock, is, msg,frm);
 // this.textArea.setText(msg.getMessager());
       
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
    public MessageData getMessage(){
        return this.msg;
            }
    public void setMessage(MessageData msg){
        this.msg = msg;
            } 
    
    public void setClientOnSelectList(Socket sock)
    {
        String ip = sock.getInetAddress().getHostAddress();
        ArrayList<ClientInfo> onlineClients = this.frm.getOnlineUser();
        for (int i = 0; i < onlineClients.size(); i++) {
            if(ip.equals(onlineClients.get(i).getIp())){
                    frm.getjList1().setSelectedValue(onlineClients.get(i), true);
               
            }
        }
    }
}
