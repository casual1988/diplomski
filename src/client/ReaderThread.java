/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.security.Key;
import javax.swing.JOptionPane;
import kriptotest.CryptoTajniKljuc;
import soundMic.MicClient;
import video.VideoClient;

/**
 *
 * @author student
 */
public class ReaderThread extends Thread {

    private Socket sock;
    private BufferedReader in;
    private MessageData data;
    private ClientInfo client;

    private ClientForm frm;

    private InputStream is;
    CryptoTajniKljuc tajni;

    public ReaderThread(Socket sock, BufferedReader in, MessageData data) {
        this.sock = sock;
        this.in = in;
        this.data = data;
        start();
    }

    public ReaderThread(Socket sock, BufferedReader in, MessageData data, ClientForm frm) {
        this.sock = sock;
        this.in = in;
        this.data = data;
        this.frm = frm;
        this.client = new ClientInfo();
        start();
    }

    public ReaderThread(Socket sock, InputStream is, MessageData data, ClientForm frm) {
        this.sock = sock;
        this.is = is;
        this.data = data;
        this.frm = frm;
        this.client = new ClientInfo();
        this.client.setSock(sock);
        this.client.setIp(this.sock.getInetAddress().getHostAddress());
        ClientInfo recipient  = (ClientInfo)frm.getjList1().getSelectedValue();
        this.client.setUsername(recipient.getUsername());
        tajni = new CryptoTajniKljuc();
        start();
    }
    

   public void run() {
        System.out.println("primljeno:");
        try {
            String msg;
            byte[] decript = new byte[100000];
            ByteArrayOutputStream serverinput = new ByteArrayOutputStream();
            int n;

            byte[] b = new byte[8192];
            while ((n = is.read(b)) > 0) {
                // n = is.read(b);
                // msg = in.readLine();
                serverinput.write(b, 0, n);
                if (client.getSecretKey() == null) {
                    Key secretKey = this.frm.recivedSecretKey(serverinput.toByteArray(), client, this.frm.getKey());
                    this.client.setSecretKey(secretKey);
                    serverinput.reset();
                } else {
                    System.out.println(b.length);
                    System.out.println(b[0]);
                    decript = tajni.decrypt(serverinput.toByteArray(), this.client.getSecretKey());
                    serverinput.reset();

                    msg = new String(decript, "UTF8");
                    if (msg != null) {
                        if (!(msg.trim().equals("callCall") || msg.trim().equals("videoCall"))) {
                            frm.getjTextArea1().append(client.getUsername() +": " +msg.trim() + "\n"); //dodao username
                        } //System.out.println(msg + "\n");
                        else if((msg.trim().equals("callCall"))){
                            int reply = JOptionPane.showConfirmDialog(null, "Call", "poziv", JOptionPane.YES_NO_OPTION);
                            if (reply == JOptionPane.YES_OPTION) {
                                System.out.println("Prihvatam poziv od servera");
                                System.out.println("Pokrecem ServerCaptureMic na drugom portu");
                            //    ServerCaptureMic mic = new ServerCaptureMic(this.client);
                            //    mic.captureAudio();
                                String callAccepted="callAccepted";
                            //    frm.sendMSG(client, callAccepted.getBytes());
                           //     System.out.println("Pokrecem klijenta i zapocinejm razgovor");
                                (new Thread(new MicClient(this.client))).start();
                            } else {
                                JOptionPane.showMessageDialog(null, "GOODBYE");
                                System.exit(0);
                            }
                            //    (new Thread(new  QuoteClient(this.client))).start();
                        }
                        else if((msg.trim().equals("videoCall")))
                        {
                             int reply = JOptionPane.showConfirmDialog(null, "Video Call", "Video poziviv", JOptionPane.YES_NO_OPTION);
                            if (reply == JOptionPane.YES_OPTION) {
                                System.out.println("Prihvatam video poziv od servera");
                                System.out.println("Pokrecem VideoServer na drugom portu");
                                Thread.sleep(6000);
                                (new Thread(new VideoClient(this.client))).start();
                         
                            }
                              else {
                                JOptionPane.showMessageDialog(null, "GOODBYE");
                                System.exit(0);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    public void run() {
//        System.out.println("primljeno:");
//        try {
//            String msg;
//            while (true) {
//                msg = in.readLine();
//                if (msg != null) {
//                    if(client.getKey()== null){
//                    this.client.setSecretKey(this.frm.recivedSecretKey(msg, client, this.frm.getKey()));
//                    }
//                    this.data.setMessage(frm.getjTextArea1() +"\n"+ msg);
//                    //frm.getjTextArea1().append(msg + "\n");
//                    frm.getjTextArea1().setText(this.data.getMessager());
//                    //System.out.println(msg + "\n");
//                    
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//     public void run() {
//        System.out.println("primljeno:");
//        try {
//            String msg;
//            byte[] decript = new byte[100000];
//            ByteArrayOutputStream serverinput = new ByteArrayOutputStream();
//            int n;
//
//            byte[] b = new byte[8192];
//            while ((n = is.read(b)) > 0) {
//                // n = is.read(b);
//                // msg = in.readLine();
//                serverinput.write(b, 0, n);
//                if (client.getSecretKey() == null) {
//                    Key secretKey = this.frm.recivedSecretKey(serverinput.toByteArray(), client, this.frm.getKey());
//                    this.client.setSecretKey(secretKey);
//                    serverinput.reset();
//                } else {
//                    System.out.println(b.length);
//                    System.out.println(b[0]);
//                    decript = tajni.decrypt(serverinput.toByteArray(), this.client.getSecretKey());
//                    serverinput.reset();
//
//                    msg = new String(decript, "UTF8");
//                    if (msg != null) {
//                        frm.getjTextArea1().append(msg + "\n");
//                        //System.out.println(msg + "\n");
//
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
