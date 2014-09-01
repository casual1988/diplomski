/*
 Encrypt and decrypt using the DES private key algorithm
*/
import java.security.*;
import javax.crypto.*;

public class PrivateExample {
    
    public static void main (String[] args) throws Exception {
        //
        // Check args and get plaintext
       
        byte[] plainText = {'a'};
        //
        // Get a DES private key
        System.out.println( "\nStart generating DES key" );
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        
        // If you do not initialize the KeyGenerator, each provider supply a default initialization.
        keyGen.init(56);
        Key key = keyGen.generateKey();
        System.out.println( "Finish generating DES key" );
        //
        // Creates the DES Cipher object (specifying the algorithm, mode, and padding).
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        // Print the provider information       
        System.out.println( "\n" + cipher.getProvider().getInfo() );
        //
        System.out.println( "\nStart encryption" );
        // Initializes the Cipher object.
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // Encrypt the plaintext using the public key
        byte[] cipherText = cipher.doFinal(plainText);
        System.out.println( "Finish encryption: " );
        System.out.println( new String(cipherText, "UTF8") );
        //
        System.out.println( "\nStart decryption" );
        // Initializes the Cipher object.
        cipher.init(Cipher.DECRYPT_MODE, key);
        // Decrypt the ciphertext using the same key
        byte[] newPlainText = cipher.doFinal(cipherText);
        System.out.println( "Finish decryption: " );
        System.out.println( new String(newPlainText, "UTF8") );
    }
}