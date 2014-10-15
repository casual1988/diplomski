package kriptotest;

/*
 * Enkripcija tajnim kljucem primjer
 * Enkriptira i dekriptira poruku koristeci DES algoritam
 */
import java.security.*;
import javax.crypto.*;

public class CryptoTajniKljuc {

    public static void main(String[] args) throws Exception {
        // provjera argumenata i dohvacanje poruke
        try {
		//if (args.length !=1) {
            //	System.err.println("Upotreba: java CryptoTajniKljuc tekst");
            //	System.exit(1);
            //}
            //byte[] plainText = args[0].getBytes("UTF8");
            byte[] plainText = {'a'};
            // generiranje DES tajnog kljuca
            System.out.println("\nGeneriram DES kljuc (molim pricekajte) ...");
            KeyGenerator keyGen = KeyGenerator.getInstance("DESede");
            keyGen.init(168);
            System.out.println(keyGen.getAlgorithm()   + " "+ keyGen.toString());
            Key key = keyGen.generateKey();
            System.out.println("Generiranje DES kljuca dovrseno!");
            // instanciranje DES cipher objekta i ispis providera
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            System.out.println("\n" + cipher.getProvider().getInfo());

            // enkripcija poruke koristenjem tajnog kljuca
            System.out.println("\nEnkripcija zapoceta ...");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            System.out.println("\n" + key.getFormat());
            System.out.println("\n" + key.toString());
            System.out.println("\n" + key.getAlgorithm());
            byte[] cipherText = cipher.doFinal(plainText);
            System.out.println("Enkripcija zavrsena: ");
            System.out.println(new String(cipherText, "UTF8"));

            // dekripcija koristenjem tajnog kljuca
            System.out.println("\nDekripcija zapoceta ...");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] newPlainText = cipher.doFinal(cipherText);
            System.out.println("Dekripcija zavrsena: ");
            System.out.println(new String(newPlainText, "UTF8"));
        } catch (Exception e) {
            System.err.println("Greska: " + e.getMessage());
        }
    }

    public Key generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("DES");
            keyGen.init(56);
            Key key = keyGen.generateKey();
            return key;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public byte[] encrypt(byte[] plainText, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] cipherText = cipher.doFinal(plainText);
            return cipherText;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public byte[] decrypt(byte[] cipherText, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] newPlainText = cipher.doFinal(cipherText);
            return newPlainText;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
