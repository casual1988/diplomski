package soundMic;

/**
 *
 * @author Aleksandar
 */
import client.ClientInfo;
import java.io.*;
import java.net.*;
import kriptotest.CryptoTajniKljuc;

public class ServerMicThread extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;
    public byte[] buf1;
    public int count;
    private static ServerMicThread instance = null;
    DatagramPacket packet;
    InetAddress address1;
    int port = 0;
    private ClientInfo recipient;
    private ClientInfo currentClient;
    private CryptoTajniKljuc tajni;

    public ServerMicThread(double i) {
        try {
      //      socket = new DatagramSocket(4445);
            buf1 = new byte[8000];
            packet = new DatagramPacket(buf1, buf1.length);
            tajni = new CryptoTajniKljuc();
            //    address1 = InetAddress.getByName("192.168.1.5");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ServerMicThread getInstance() {  //singlton
        if (instance == null) {
            instance = new ServerMicThread(1.0);
        }
        return instance;
    }

    public void setRecipientClientAddress(ClientInfo recipient) {
        try {
            address1 = InetAddress.getByName(recipient.getIp());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setRecipient(ClientInfo recipient) {
        this.recipient = recipient;
    }
     public void setCurrentClient(ClientInfo currentClient) throws SocketException {
        this.currentClient = currentClient; 
         System.out.println("setujem port na " + this.currentClient.getMicPort());
        socket = new DatagramSocket(this.currentClient.getMicPort());
        
    }

    //metoda za setovanje buffera iz metode za snimanje zvuka ,SendCaptureMic klasa .
    public void setBuffer(byte[] buf, int count) {
        this.buf1 = buf;
        this.count = count;
    }

    public void run() {
        byte[] buf = new byte[256];
        InetAddress address1 = null;
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        DatagramPacket packet1 = new DatagramPacket(buf1, buf.length);////ovo je za mikrofon

        while (moreQuotes) {
            try {

                //      buf1 = new byte[256];//ovo je za mikrofon
                // receive request
                // socket.receive(packet);
                socket.receive(packet1);
                if (address1 == null) {
                    address1 = packet1.getAddress();//ovo je za mikrofon
                }
                // figure out response
                //    String dString = null;
                //    if (in == null)
                //        dString = new Date().toString();
                //     else
                //        dString = getNextQuote();
//
                //    buf = dString.getBytes();

                // send the response to the client at "address" and "port"
                //   InetAddress address = packet.getAddress();
                //     int port = packet.getPort();
                System.out.println(buf1[1]);
                int port1 = packet1.getPort();//ovo je za mikrofon
                //    packet = new DatagramPacket(buf, buf.length, address, port);
                packet1 = new DatagramPacket(buf1, buf.length, address1, port1); //ovo je za mikrofon
                //    socket.send(packet);

                socket.send(packet1);//ovo je za mikrofon
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            }
        }
        socket.close();
    }

    public void sendBuffer(byte[] buf, int count) {
        try {
            
            
            if (port == 0) {
                System.out.println("cekam paket");
                socket.receive(packet);
                //    if(address1==null)
                //         address1 = packet.getAddress();

                port = packet.getPort();//ovo je za mikrofon
                System.out.println(port);
            }
            //    packet = new DatagramPacket(buf, buf.length, address, port);
            //   System.out.println("prije enkripcije " + buf.length + " prvi" +buf[0]);
            System.out.println("poslao paket");
            byte[] cryptoData = tajni.encrypt(buf, recipient.getSecretKey());
            System.out.println("poslje" + cryptoData.length);
            packet = new DatagramPacket(cryptoData, 0, cryptoData.length, address1, port); //ovo je za mikrofon ,count 8000 radi
            //     packet = new DatagramPacket(buf, 0, count, address1, port); //ovo je za mikrofon ,count 8000 radi
            //    socket.send(packet);
            //      System.out.println(buf[5]);
            socket.send(packet);//ovo je za mikrofo

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    public MicThread() throws IOException {
//        this("MicServerThread");
//    }
//    public MicThread(ClientInfo client) {
//        this.client = client;
//        
//    }
//    public MicThread(String name) throws IOException {
//        super(name);
//        socket = new DatagramSocket(4445);
//
//        try {
//            in = new BufferedReader(new FileReader("proba.txt"));
//        } catch (FileNotFoundException e) {
//            System.err.println("Could not open quote file. Serving time instead.");
//        }
//    }
    //novi konstruktor kojeg pozivam iz metode koja snima zvuk,,
//    public MicThread(int bufferSize) throws IOException {   // za mikrofon pokusaj da se ovo ubaci u metodu za snimanje zvuka i da joj se prosljedi buffer
//
//        socket = new DatagramSocket(4445);
//
//        try {
//            buf1 = new byte[256];//zbog mikrofona da ne ide prazan na pocetku 
//            in = new BufferedReader(new FileReader("proba.txt"));
//            start();
//            //     new SendCaptureMic().start();  ////ovo je za mikrofon
//        } catch (Exception e) {
//            System.err.println("Could not open quote file. Serving time instead.");
//        }
//    }
//    protected String getNextQuote() {
//        String returnValue = null;
//        try {
//            if ((returnValue = in.readLine()) == null) {
//                in.close();
//                moreQuotes = false;
//                returnValue = "No more quotes. Goodbye.";
//            }
//        } catch (IOException e) {
//            returnValue = "IOException occurred in server.";
//        }
//        return returnValue;
//    }
    public void sendVideoBuffer(byte[] buf, int count) {
        try {
            if (port == 0) {
                System.out.println("cekam paket");
                socket.receive(packet);
                //    if(address1==null)
                //         address1 = packet.getAddress();

                port = packet.getPort();//ovo je za mikrofon
                System.out.println(port);
            }
            //    packet = new DatagramPacket(buf, buf.length, address, port);
            //   System.out.println("prije enkripcije " + buf.length + " prvi" +buf[0]);
            System.out.println("poslao paket");
            byte[] cryptoData = tajni.encrypt(buf, recipient.getSecretKey());
            System.out.println("poslje" + cryptoData.length);
            packet = new DatagramPacket(cryptoData, 0, cryptoData.length, address1, port); //ovo je za mikrofon ,count 8000 radi
            //     packet = new DatagramPacket(buf, 0, count, address1, port); //ovo je za mikrofon ,count 8000 radi
            //    socket.send(packet);
            //      System.out.println(buf[5]);
            socket.send(packet);//ovo je za mikrofo

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
