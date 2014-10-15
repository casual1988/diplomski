/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package video;

import client.ClientInfo;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.URLDataSource;
import javax.media.util.BufferToImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import kriptotest.CryptoTajniKljuc;
import org.omg.CORBA.DATA_CONVERSION;

/**
 *
 * @author student
 */
public class VideoClient extends Thread {

    
    ClientInfo client;
    private CryptoTajniKljuc tajni;
    InetAddress address1;
    Component videoScreen;
    
    public VideoClient() {
    }

    public VideoClient(ClientInfo client) {
        try {
            this.client = client;
            tajni = new CryptoTajniKljuc();
            this.address1 = InetAddress.getByName(this.client.getIp());
            System.out.println("adresa servera je " + this.client.getIp());
        } catch (UnknownHostException ex) {
            Logger.getLogger(VideoClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
      public void run() {

        int bufferSize = 40000;
        //int bufferSize=128 ;
        int i = 0;
        try {
            // get a datagram socket
            DatagramSocket socket = new DatagramSocket();

            // send request
            byte[] buf = new byte[bufferSize];//8000
            byte[] decriptData = new byte[bufferSize];
            byte[] encriptData = new byte[bufferSize];
     //   InetAddress address = InetAddress.getByName(args[0]);
            //    InetAddress address = InetAddress.getByName("192.168.1.8");
            System.out.println("Klijent za zvuk upaljen, adresa servera je " + this.client.getIp());
            InetAddress address = InetAddress.getByName(this.client.getIp());  // //ne znam zasto ovo ne radi???????
            encriptData = tajni.encrypt(buf, client.getSecretKey());
            DatagramPacket packet = new DatagramPacket(encriptData, encriptData.length, address1, 4445);

            //DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            System.out.println("encriptData" + encriptData.length);
            System.out.println("salje" + buf.length + buf[0]);
            socket.send(packet);
     //ovde malo treba srediti cod da se dobije bffer
            // get response

            //   QuoteClient q= getInstancePlay(packet.getData());
         //   this.initData(packet.getData());
JFrame frm = new JFrame();
 frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
       frm.setBounds(10, 10, 640, 480);
           // frm.setSize(640, 480);
     //       frm.setLocationByPlatform(true);
            frm.setVisible(true);
JLabel jLabel;
JPanel jPanel = new JPanel();
jPanel.setBounds(10, 10, 640, 480);
frm.add(jPanel);  
ImageIcon icon = new ImageIcon();
jLabel = new JLabel();
//frm.pack();
//File file = new File("a.jpg");
            while (true) {
               
                //   packet = new DatagramPacket(buf, buf.length);
                packet = new DatagramPacket(encriptData, encriptData.length);

                socket.receive(packet);
                System.out.println("primljeno" + encriptData.length);

                decriptData = tajni.decrypt(packet.getData(), client.getSecretKey());
                System.out.println(decriptData.length);
                BufferedImage imag=ImageIO.read(new ByteArrayInputStream(decriptData));
                   frm.getContentPane().validate();
                 frm.getContentPane().repaint();
                icon.setImage(imag);
                jLabel.setIcon(icon);
                 jPanel.add(jLabel);
                 frm.add(jPanel);
         //    frm.add(jPanel);
             //   DataBufferByte data = new DataBufferByte(decriptData, decriptData.length);
              //  Buffer myBuffer = new Buffer();
              //  myBuffer.setData(imag);
//                
//                FileOutputStream fos = new FileOutputStream(file);
//                     fos.write(decriptData);
//                     fos.close();
//                     
                     
                 //  Graphics2D g2 = imag.createGraphics();

         //   g2.drawImage(imag, 0, 0, null);

          //  g2.dispose();     
                     
//               URI   mediaURL = file.toURI();
//               Player player = Manager.createRealizedPlayer(mediaURL.toURL());
//               player.start();
//               
//            frm.setBounds(10, 10, 200, 200);//sets the size of the screen

// setting close operation to the frame
            frm.addWindowListener(new WindowAdapter() {

                public void windowClosing(WindowEvent we) {
                    System.exit(0);
                }
            });

////place player and video screen on the frame
//            frm.add(videoScreen, BorderLayout.CENTER);
//            frm.add(player.getControlPanelComponent(), BorderLayout.SOUTH);
//            frm.setVisible(true);
//               BufferToImage btoi = new BufferToImage((VideoFormat) myBuffer.getFormat());
//        ImageIO.write(imag, "jpg", );
//               BufferToImage  btoi = new BufferToImage((VideoFormat) decriptData);
//               Image img = btoi.createImage(imag);
//               URL url = new URL();
//                 DataSource dataSource = Manager.createDataSource(;
          //      this.playRealTimeAudio(decriptData, bufferSize);
    //   this.playRealTimeAudio(packet.getData(),bufferSize);
                //      out.write(packet.getData());
                //  playAudio(packet.getData());  //ovo radi
                i++;
            }
        } catch (SocketException ex) {
            Logger.getLogger(VideoClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(VideoClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VideoClient.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      
      public static void main(String[] args) {
        JFrame frm = new JFrame();
 frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
       frm.setBounds(10, 10, 640, 480);
           // frm.setSize(640, 480);
     //       frm.setLocationByPlatform(true);
            frm.setVisible(true);
JLabel jLabel;
JPanel jPanel = new JPanel();
//jPanel.setBounds(10, 10, 640, 480);
frm.add(jPanel);  
ImageIcon icon = new ImageIcon();
jLabel = new JLabel();
jLabel.setSize(640, 480);
    }
}
