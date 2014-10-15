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
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.*;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.util.BufferToImage;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import soundMic.ServerMicThread;

/**
 *
 * @author Aleksandar
 */
public class VideoGrab extends Thread {

    CaptureDeviceInfo device;
    MediaLocator ml;
    Player player;
    Component videoScreen;

    public BufferToImage btoi;
    public BufferedImage bi;
    public Buffer buf;
    public Image img;
    public FrameGrabbingControl fgc;
    //   public ImagePanel imgpanel;
    public ClientInfo recipient;

    public static void main(String args[]) {
      //  new VideoGrab().startVideoGrab();// create a new instance of WebCam in main function
    }

    public VideoGrab(ClientInfo recipient) {
        this.recipient = recipient;
        start();
    }

    public VideoGrab() {
    }

    public void run() {
        try {
//gets a list of devices how support the given videoformat
            String str1 = "vfw:Microsoft WDM Image Capture (Win32):0";
            String str2 = "vfw:Microsoft WDM Image Capture (Win32):0";
            MediaLocator ml = new MediaLocator("vfw://0");

            DataSource dataSource = Manager.createDataSource(ml);

            //Now we create our camera data from the source & start the player  
            player = Manager.createRealizedPlayer(dataSource);
            player.start();

            //Lets have a a second delay as we dinamically grab the frames  
            Thread.currentThread().sleep(1000);

            videoScreen = player.getVisualComponent();
            JFrame frm = new JFrame();
            frm.setBounds(10, 10, 640, 480);//sets the size of the screen
            frm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
// setting close operation to the frame   izmjenio Frame u Jframe
//            frm.addWindowListener(new WindowAdapter() {
//
//                public void windowClosing(WindowEvent we) {
//                    System.exit(0);
//                }
//            });

//place player and video screen on the frame
            frm.add(videoScreen, BorderLayout.CENTER);
            frm.add(player.getControlPanelComponent(), BorderLayout.SOUTH);
            frm.setVisible(true);

//capture image
            final ServerMicThread mic = ServerMicThread.getInstance();
            mic.setRecipientClientAddress(recipient);
            mic.setRecipient(recipient);
            Thread.sleep(4000);//wait 10 seconds before capturing photo

            fgc = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");
            while (true) {
                Thread.sleep(100);
                buf = fgc.grabFrame();//grab the current frame on video screen

                btoi = new BufferToImage((VideoFormat) buf.getFormat());

                img = btoi.createImage(buf);
                byte[] grabFrame = getImageByte(img);
                System.out.println("saljem frame " + grabFrame.length);
                mic.sendVideoBuffer(grabFrame, grabFrame.length);
                System.out.println(grabFrame[1] + " " + grabFrame[10000] + "    /n ");
                System.out.println((grabFrame.length / 1000) + "kB");
            }

        } catch (Exception e) {
      }
    }
//
 //   private void saveImagetoFile(Image img, String string) {
//        try {
//            int w = img.getWidth(null);
//            int h = img.getHeight(null);
//            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//            Graphics2D g2 = bi.createGraphics();
//
//            g2.drawImage(img, 0, 0, null);
//
//            g2.dispose();
//
//            String fileType = string.substring(string.indexOf('.') + 1);
//
//            ImageIO.write(bi, fileType, new File(string));
//
//        } catch (Exception e) {
//        }
//    }

    private byte[] getImageByte(Image img) {
        try {
            int w = img.getWidth(null);
            int h = img.getHeight(null);
            System.out.println("slika " + w + "  " + h);
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bi.createGraphics();
            g2.drawImage(img, 0, 0, null);

            g2.dispose();
            //    String fileType = format.substring(format.indexOf('.') + 1);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", baos);
            byte[] imageInByte = baos.toByteArray();

         //    WritableRaster raster = bi .getRaster();
            //    DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();
            return imageInByte;
        } catch (Exception e) {
            return null;
        }
    }
    
    
//    public  void run()
//        {
//              final ServerMicThread mic = ServerMicThread.getInstance();
//            mic.setClientAdress(client);
//            mic.setClient(client);
//          while (true) {
//              try {
//                  Thread.sleep(100);
//                  buf = fgc.grabFrame();//grab the current frame on video screen
//                  
//                  btoi = new BufferToImage((VideoFormat) buf.getFormat());
//                  
//                  img = btoi.createImage(buf);
//                  byte[] grabFrame = getImageByte(img);
//                  System.out.println("saljem frame " + grabFrame.length);
//                  mic.sendVideoBuffer(grabFrame, grabFrame.length);
//                  System.out.println(grabFrame[1] + " " + grabFrame[10000] + "    /n ");
//                  System.out.println((grabFrame.length / 1000) + "kB");
//              } catch (InterruptedException ex) {
//                  Logger.getLogger(VideoGrab.class.getName()).log(Level.SEVERE, null, ex);
//              }
//          }
 //   }
}
