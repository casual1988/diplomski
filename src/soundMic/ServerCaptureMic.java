/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soundMic;

import client.ClientInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Aleksandar
 */
// klasa koja ce sluziti za slanje streama sa mikrofona ,,potrebno vidjeti datagram klase 
//
public class ServerCaptureMic extends Thread {

    protected boolean running;
    ByteArrayOutputStream out;
    PrintWriter out1;
    DatagramSocket socket;
    private ClientInfo recipient;
    private ClientInfo currentClient;

    public static void main(String[] args) {
        new ServerCaptureMic().captureAudio();
    }

    public ServerCaptureMic() {
        //    start();
    }

    public ServerCaptureMic(ClientInfo recipient, ClientInfo currentClient) {
        this.recipient = recipient;
        this.currentClient = currentClient;
    }

    public ServerCaptureMic(PrintWriter out1) {//, MessageData msg) {
        this.out1 = out1;
        //    this.msg = msg;
        start();
    }

    public void captureAudio() {
        try {
            final AudioFormat format = getFormat();
            DataLine.Info info = new DataLine.Info(
                    TargetDataLine.class, format);
            final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
    //  final QuoteServerThread sa=  new QuoteServerThread(1);  //za mikrofon dodano
            //   sa.start();   //napisao u konstrutkru start

            final ServerMicThread mic = ServerMicThread.getInstance();
            mic.setRecipientClientAddress(recipient);
            mic.setRecipient(recipient);
            mic.setCurrentClient(currentClient);
            Runnable runner = new Runnable() {
                //  int bufferSize = (int)format.getSampleRate()* format.getFrameSize();
                int bufferSize = 128;
                byte buffer[] = new byte[bufferSize];//bufferSize

                public void run() {

                    out = new ByteArrayOutputStream();
                    running = true;
                    try {
                        while (running) {
                            int count
                                    = line.read(buffer, 0, buffer.length);

                            if (count > 0) {
           //     out.write(buffer, 0, count);
                                // sa.setBuffer(buffer,count);
                                //     System.out.println("pocetak" + count); //stavio neki ispis ,mikrof radi jos smao slanje rjesiti
                                mic.sendBuffer(buffer, count);
                            }
                        }
                        out.close();
                    } catch (IOException e) {
                        System.err.println("I/O problems: " + e);
                        System.exit(-1);
                    }
                }
            };
            Thread captureThread = new Thread(runner);
            captureThread.start();
        } catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
            System.exit(-2);
        } catch (Exception e) {
            //za mikrofon dodan cartch
        }
    }

    private void playAudio() {
        try {
            byte audio[] = out.toByteArray();
            InputStream input
                    = new ByteArrayInputStream(audio);
            final AudioFormat format = getFormat();
            final AudioInputStream ais
                    = new AudioInputStream(input, format,
                            audio.length / format.getFrameSize());
            DataLine.Info info = new DataLine.Info(
                    SourceDataLine.class, format);
            final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            Runnable runner = new Runnable() {
                int bufferSize = (int) format.getSampleRate()
                        * format.getFrameSize();
                byte buffer[] = new byte[bufferSize];

                public void run() {
                    try {
                        int count;
                        while ((count = ais.read(
                                buffer, 0, buffer.length)) != -1) {
                            if (count > 0) {
                                line.write(buffer, 0, count);

                            }
                        }
                        line.drain();
                        line.close();
                    } catch (IOException e) {
                        System.err.println("I/O problems: " + e);
                        System.exit(-3);
                    }
                }
            };
            Thread playThread = new Thread(runner);
            playThread.start();
        } catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e);
            System.exit(-4);
        }
    }

    private AudioFormat getFormat() {
        float sampleRate = 8000;

        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate,
                sampleSizeInBits, channels, signed, bigEndian);
    }

    public ByteArrayOutputStream getOut() {
        return out;
    }

    public void setOut(ByteArrayOutputStream out) {
        this.out = out;
    }

}
