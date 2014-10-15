/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextArea;
import kriptotest.CryptoJavniKljuc;
import kriptotest.CryptoTajniKljuc;
import kriptotest.Frame;
import soundMic.MicClient;
import soundMic.ServerCaptureMic;
import video.VideoGrab;

/**
 *
 * @author Aleksandar
 */
public class ClientForm extends javax.swing.JFrame {

    public static final int TCP_PORT = 9000;
    public static final int clientPort = 9001;
    private ClientInfo currentClient = new ClientInfo();

    BufferedReader in;
    InetAddress addr;
    Socket sock;
    PrintWriter out;
    String message;

    
    ArrayList<ClientInfo> onlineUser = new ArrayList<ClientInfo>();
    ObjectOutputStream oos;
    ObjectInputStream ois;
    ClientInfo recipient;

   
    KeyPair key;
    CryptoJavniKljuc javni;
    CryptoTajniKljuc tajni;
    String ipAddress = "localhost";

    /**
     * Creates new form ClientForm
     */
    public ClientForm() {
        initComponents();
        javni = new CryptoJavniKljuc();
        System.out.println("\nGenerisanje RSA par kljuceva (molim pricekajte) ...");
        this.key = javni.generateKeyPair();
        System.out.println("Generiranje RSA para kljuceva dovrseno!");
        currentClient.setKey(key.getPublic());
        initCurrentClient();
//        if (connectToServer()) {
//            message = "Uspjesna konekcija na server";
//
//            //pokretanje servera za prijem poruke
//            (new Thread(new ClientServer(this))).start();
//        } else {
//            message = "konekcija nije uspjela";
//        }
    }

    public void initCurrentClient() {
        Random random = new Random();
        String userName = "User " + (random.nextInt(1000) + 1000);
        currentClient.setUsername(userName);
        int port = 9000 + random.nextInt(100);
        int videoPort = 4400 + random.nextInt(200);
        int micPort = 4601 + random.nextInt(200);
        System.out.println("port "+ port + " videoport " + videoPort + "   mic port  " + micPort);
        this.currentClient.setPort(port);
        this.currentClient.setVideoPort(videoPort);
        this.currentClient.setMicPort(micPort);
        setTitle(userName);
    }

    public boolean connectToServer() {
        try {
            // addr = InetAddress.getByName("192.168.1.5");
            System.out.println("Adresa servera " + ipAddress.toString());
            addr = InetAddress.getByName(ipAddress);
            sock = new Socket(addr, TCP_PORT);
            oos = new ObjectOutputStream(sock.getOutputStream());
            ois = new ObjectInputStream(sock.getInputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login() throws IOException {
        //    out.println("login " + user.getUsername() + " " + user.getPassword());
        if (in.readLine().equals("200")) {
            return true;
        } else {
            return false;
        }
    }

    public void closeConnection() {
        try {
            oos.close();
            ois.close();
            sock.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getOnlineUserOnServer() throws IOException, ClassNotFoundException {
        sendPublicKey();
        oos.writeObject("getOnlineUser");
        onlineUser = (ArrayList<ClientInfo>) ois.readObject();
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < onlineUser.size(); i++) {
            model.addElement(onlineUser.get(i));
        }
        jList1.setModel(model);
    }

    public void signOut() throws IOException, ClassNotFoundException {
        if (sock != null) {
            oos.writeObject("signout");
            String response = (String) ois.readObject();
            if (response.equals("200")) {
                closeConnection();
                System.out.println("Konekcija prema serveru zatvorena!");
                dispose();
                System.exit(0);
            }
        } else {
            dispose();
            System.exit(0);
        }
    }

    //metoda za salnje
    public void sendMessageToClint() {
        try {
            this.recipient = (ClientInfo) jList1.getSelectedValue();
            if (this.recipient.getSock() == null) {
                this.recipient.setSock(getClientSocket(this.recipient));
            }
            String msg = jTextArea2.getText().trim();
            if (this.recipient.getSecretKey() == null) {
                byte[] secretKey = generateSecretKeyProba(recipient);
                sendSecretKey(recipient, secretKey);   // napravio novu samo za slanje kljuca
                sendMSG(recipient, msg.getBytes());  // prvo slanje kljuca pa onda poruke
                jTextArea1.append(currentClient.getUsername() + ":   " + msg + "\n");
                jTextArea2.setText("");
            } else {
                //  byte[] encript = tajni.encrypt(msg.getBytes(), this.recipient.getSecretKey());
                //sendMSG(recipient, encript); //dodao gdet bytes
                sendMSG(recipient, msg.getBytes());
                jTextArea1.append(currentClient.getUsername() + ":   " + msg + "\n");
                jTextArea2.setText("");
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //proba ,slanje kljuca na server za ovog usera,da bi server cuvao i razmjenio kljuceve mejdu korisnicima
    public void sendPublicKey() throws IOException, ClassNotFoundException {
        System.out.println("Slanje javnog kljuca");
        oos.writeObject(currentClient);
        String response = (String) ois.readObject();
        System.out.println("Slanje javnog kljuca uspjesno");
    }

    public String generateSecretKey(ClientInfo client) throws UnsupportedEncodingException {
        tajni = new CryptoTajniKljuc();
        System.out.println("Generisanje DES kljuca (molim pricekajte)...");
        Key secretKey = tajni.generateKey();
        System.out.println("Generisanje DES kljuca zavrseno!");
        this.recipient.setSecretKey(secretKey);
        // CryptoJavniKljuc javni = new CryptoJavniKljuc();
        byte[] byteText = javni.encrypt(secretKey.getEncoded(), client.getKey());
        String cipherText = new String(byteText, "UTF8");
        return cipherText;
    }

    //dekriptovanje tajnog kljuca
    public Key recivedSecretKey(byte[] msg, ClientInfo currentClient, KeyPair key) throws UnsupportedEncodingException {
        javni = new CryptoJavniKljuc();
        byte[] text = javni.decrypt(msg, key.getPrivate());
        SecretKey spec = new SecretKeySpec(text, "DES");
        Key secretKey = (Key) spec;
        currentClient.setSecretKey(secretKey);
        return secretKey;
    }

    public byte[] generateSecretKeyProba(ClientInfo client) throws UnsupportedEncodingException {
        tajni = new CryptoTajniKljuc();
        System.out.println("Generisanje DES kljuca (molim pricekajte)...");
        Key secretKey = tajni.generateKey();
        System.out.println("Generisanje DES kljuca zavrseno!");
        this.recipient.setSecretKey(secretKey);
        byte[] byteText = javni.encrypt(secretKey.getEncoded(), client.getKey());
        return byteText;
    }

    public void sendMSG(ClientInfo client, byte[] msg) throws UnknownHostException, IOException {
        OutputStream out = null;
        out = client.getSock().getOutputStream();

        ByteArrayInputStream serverinput = new ByteArrayInputStream(msg);
        int count;
        byte[] bytearray = new byte[128];
        byte[] cipherbuffer;
        while ((count = serverinput.read(bytearray)) > 0) {
            cipherbuffer = (tajni.encrypt(bytearray, client.getSecretKey()));
            out.write(cipherbuffer, 0, cipherbuffer.length);
        }
        out.flush();
    }

    private void sendSecretKey(ClientInfo client, byte[] msg) throws UnknownHostException, IOException {
        OutputStream out = null;
        out = client.getSock().getOutputStream();
        out.write(msg);
        out.flush();
    }

    public Socket getClientSocket(ClientInfo client) throws IOException {
        InetAddress addr1 = InetAddress.getByName(client.getIp());
        //  Socket sock = new Socket(addr1, clientPort);
        Socket sock = new Socket(addr1, client.getPort());
        System.out.println("ovo iznad dodao port j e " + client.getPort());
        return sock;
    }

    public void call() {
        try {
            System.out.println("Pokrecem ServerCaptureMic");
            ServerCaptureMic mic = new ServerCaptureMic(this.recipient, this.currentClient);
            mic.captureAudio();
            String call = "callCall";
            System.out.println("Pozivam klijenta...");
            sendMSG(this.recipient, call.getBytes());
            System.out.println("Poziviv prihvacen..");
            // ServerCaptureMic mic = new ServerCaptureMic(this.recipient);
            //  mic.captureAudio();
            //pokretanje klijenta
            //   System.out.println("Pokrecem klijenta na drugom portu i zapocinejm razgovor");
            //   (new Thread(new MicClient(this.recipient))).start();
        } catch (IOException ex) {
            Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void videoCall() {
        try {
            // TODO add your handling code here:
            System.out.println("Pokrecem Video");
            String call = "videoCall";
            System.out.println("Pozivam klijenta...");
            sendMSG(this.recipient, call.getBytes());
            System.out.println("Poziviv prihvacen..");
            VideoGrab video = new VideoGrab(this.recipient);
            // video.startVideoGrab();
            Thread.sleep(1000);
            System.out.println("Cekam 5 sekundi");

        } catch (IOException ex) {
            Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextArea2KeyTyped(evt);
            }
        });
        jScrollPane3.setViewportView(jTextArea2);

        jButton1.setText("Send");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Call");
        jButton2.setToolTipText("");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Video Call");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        jMenuItem1.setText("Connect to Server");
        jMenuItem1.setToolTipText("Connect to Server");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem5.setText("Online Users");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem2.setText("Exit");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Call");

        jMenuItem3.setText("Call");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem4.setText("Video Call");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuBar1.add(jMenu2);

        jMenu3.setLabel("Tools");

        jMenuItem6.setText("Server options");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 245, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2))
                .addGap(26, 26, 26))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton2)
                                    .addComponent(jButton3)))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton1))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        sendMessageToClint();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

        if (connectToServer()) {
            message = "Uspjesna konekcija na server";

            //pokretanje servera za prijem poruke
            (new Thread(new ClientServer(this, this.currentClient))).start();
        } else {
            message = "konekcija nije uspjela";
        }
        jMenuItem1.setEnabled(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        try {
            signOut();
        } catch (IOException ex) {
            Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        System.out.println("proba");
        jTextArea2.requestFocusInWindow();
    }//GEN-LAST:event_jList1ValueChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        call();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        videoCall();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            // TODO add your handling code here:
            signOut();
        } catch (IOException ex) {
            Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        call();

    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        videoCall();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jTextArea2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextArea2KeyTyped
        char c = evt.getKeyChar();
        if (c == KeyEvent.VK_ENTER) {
            jButton1.doClick();
        }
    }//GEN-LAST:event_jTextArea2KeyTyped

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        ServerOptionsForm formOptions = new ServerOptionsForm(this);
        formOptions.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        try {
            // TODO add your handling code here:
            getOnlineUserOnServer();
        } catch (IOException ex) {
            Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientForm.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientForm.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientForm.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientForm.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClientForm().setVisible(true);
            }
        });
    }

    public JTextArea getjTextArea1() {
        return jTextArea1;
    }

    public void setjTextArea1(JTextArea jTextArea1) {
        this.jTextArea1 = jTextArea1;
    }

    public JList getjList1() {
        return jList1;
    }

    public void setjList1(JList jList1) {
        this.jList1 = jList1;
    }

    public KeyPair getKey() {
        return key;
    }

    public void setKey(KeyPair key) {
        this.key = key;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public ArrayList<ClientInfo> getOnlineUser() {
        return onlineUser;
    }

    public void setOnlineUser(ArrayList<ClientInfo> onlineUser) {
        this.onlineUser = onlineUser;
    }
     public ClientInfo getRecipient() {
        return recipient;
    }

    public void setRecipient(ClientInfo recipient) {
        this.recipient = recipient;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables
}
