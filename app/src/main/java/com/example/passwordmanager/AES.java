package com.example.passwordmanager;

import android.os.Build;

import androidx.annotation.RequiresApi;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Mit der Klasse AES (Advanced Encryption Standard) koennen nachrichten verschluesselt werden.
 * Dazu muessen sowohl der Schluessel, das Salz, als auch die Anzahl der Iterationen
 * uebereinstimmen.
 * Das Salz ist per deafault auf "a" gesetzt und kann weder null noch "" werden.
 * Die anzahl der Iterationen ist per deafault auf 65536 gesetzt.
 */
public class AES {
	
	private String salt;
	private int iterations;
	
	/**
	 * Erzeugt ein AES-Objekt
	 */
	public AES() {
		salt = "a";
		iterations = 65536;
	}
	
	/**
	 * Setzt das Salz fest
	 * @param salt Salz
	 */
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	/**
	 * Setzt die Anzahl der Iterationen fest
	 * @param iterations Anzahl Iterationen
	 */
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}
	
	/**
	 * Verschluesselt die angegebene Nachricht mit dem angegebenen Schluessel.
	 * @param strToEncrypt zu verschluesselnde Nachricht
	 * @param key Schluessel zum verschluesseln
	 * @return verschluesselte Nachricht
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public String encrypt(String strToEncrypt, String key) {
	    try {
	    	
	      byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	      IvParameterSpec ivspec = new IvParameterSpec(iv);
	 
	      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	      KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), iterations, 256);
	      SecretKey tmp = factory.generateSecret(spec);
	      SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
	 
	      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	      cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
	      return Base64.getEncoder()
	          .encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
	      
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	System.exit(-1);
	    }
	    
	    return null;
	 }
	
	/**
	 * entschluesselt die angegebene Nachricht mit dem angegebenen Schluessel.
	 * @param strToDecrypt zu entschluesselnde Nachricht
	 * @param key Schluessel zum entschluesseln
	 * @return entschluesselte Nachricht
	 */
	 @RequiresApi(api = Build.VERSION_CODES.O)
	 public String decrypt(String strToDecrypt, String key) {
	    try {
	    	
	      byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	      IvParameterSpec ivspec = new IvParameterSpec(iv);
	 
	      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	      KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), iterations, 256);
	      SecretKey tmp = factory.generateSecret(spec);
	      SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
	 
	      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	      cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
	      return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	      System.exit(-1);
	    }
	    
	    return null;
	 }

}
