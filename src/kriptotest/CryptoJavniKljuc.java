package kriptotest;

/*
 * Enkripcija javnim kljucem primjer
 * Enkriptira i dekriptira poruku koristeci RSA algoritam
 */
import java.security.*;
import javax.crypto.*;

public class CryptoJavniKljuc {

    public static void main(String[] args) throws Exception {
        // provjera argumenata i dohvacanje poruke
        try {
            if (args.length != 1) {
                System.err.println("Upotreba: java CryptoJavniKljuc tekst");
                System.exit(1);
            }
            byte[] plainText = args[0].getBytes("UTF8");
		//
            // generiranje RSA para kljuceva
            System.out.println("\nGeneriram RSA par kljuceva (molim pricekajte) ...");
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair key = keyGen.generateKeyPair();
            System.out.println("Generiranje RSA para kljuceva dovrseno!");
		//
            // instanciranje RSA cipher objekta i ispis providera
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            System.out.println("\n" + cipher.getProvider().getInfo());
		//
            // enkripcija plainteksta koristenjem javnog kljuca
            System.out.println("\nEnkripcija zapoceta ...");
            cipher.init(Cipher.ENCRYPT_MODE, key.getPublic());
            byte[] cipherText = cipher.doFinal(plainText);
            System.out.println("Enkripcija zavrsena !");
            System.out.println(new String(cipherText, "UTF8"));
		//
            // dekripcija sifriranog teksta koristenjem privatnog kljuca
            System.out.println("\nDekripcija zapoceta ...");
            cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
            byte[] newPlainText = cipher.doFinal(cipherText);
            System.out.println("Dekripcija zavrsena !");
            System.out.println(new String(newPlainText, "UTF8"));
        } catch (Exception e) {
            System.err.println("Greska: " + e.getMessage());
        }
    }

    public KeyPair generateKeyPair() {
        try {
            System.out.println("\nGeneriram RSA par kljuceva (molim pricekajte) ...");
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair key = keyGen.generateKeyPair();
            System.out.println("Generiranje RSA para kljuceva dovrseno!");
            return key;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public byte[] encrypt(byte[] plainText, PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            System.out.println("\n" + cipher.getProvider().getInfo());
		//
            // enkripcija plainteksta koristenjem javnog kljuca
            System.out.println("\nEnkripcija zapoceta ...");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] cipherText = cipher.doFinal(plainText);
            return cipherText;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public byte[] decrypt(byte[] cipherText, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            System.out.println("\nDekripcija zapoceta ...");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] newPlainText = cipher.doFinal(cipherText);
            System.out.println("Dekripcija zavrsena !");
            return newPlainText;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
