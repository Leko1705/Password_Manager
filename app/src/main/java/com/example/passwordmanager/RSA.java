package com.example.passwordmanager;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.*;
import javax.crypto.Cipher;
import java.util.HashMap;

/**
 * Die Klasse RSA erzeugt ein schluesselpaar aus privatekey und PublicKey fur eine asymmetrische Verschluesselung
 * Andere PublicKeys fuer die Verschluesselung fuer einen bestimmten nutzer koennen hinzugefuegt werden.
 * Fuer die Nutzung der Schluessel ist ggf. der import des Packeges java.security.* notwendig
 */
public class RSA implements Serializable {
	private PrivateKey sk;
	private PublicKey pk;
	
	private HashMap<String, PublicKey> pks;
	
	/**
	 * Deafault Konstruktor
	 */
	public RSA() {
		pks = new HashMap<String, PublicKey>();
		KeyPairGenerator keyGen = null;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyGen.initialize(2048);
		KeyPair pair = keyGen.generateKeyPair();
		sk = pair.getPrivate();
		pk = pair.getPublic();
	}
	
	/**
	 * @param keySize schluessellaenge
	 */
	public RSA(int keySize) {
		pks = new HashMap<String, PublicKey>();
		KeyPairGenerator keyGen = null;
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyGen.initialize(keySize);
		KeyPair pair = keyGen.generateKeyPair();
		sk = pair.getPrivate();
		pk = pair.getPublic();
	}
	
	/**
	 * fuegt einen Nutzer inkl. seines PublicKeys hinzu
	 * @param username 	Nutzername
	 * @param pk 		publicKey des Nutzers
	 */
	public void add(String username, PublicKey pk) {
		pks.put(username, pk);
	}
	
	/**
	 * liefert den eigenen generierten PublicKey
	 */
	public PublicKey getPublicKey() {
		return pk;
	}
	
	/**
	 * liefert den eigenen generierten PrivateKey
	 */
	public PublicKey getPrivateKey() {
		return pk;
	}
	
	/**
	 * 
	 * @param username user to send the encrypt message
	 * @param message message to encrypt as String
	 * @return encrypted message as byte[]
	 */
	public byte[] encrypt(String username, String message) {
		byte[] encryptedMessage = null;
		try {
			Cipher encrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			encrypt.init(Cipher.ENCRYPT_MODE, pks.get(username));
			encryptedMessage = encrypt.doFinal(message.getBytes(StandardCharsets.UTF_8));
			
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return encryptedMessage;
	}
	
	/**
	 * @param chiffre chiffre to decrypt as byte[]
	 * @return decrypted message as String
	 */
	public String decrypt(byte[] chiffre) {
		String dec = null;
		try {
			Cipher decrypt=Cipher.getInstance("RSA/ECB/PKCS1Padding");
			decrypt.init(Cipher.DECRYPT_MODE, sk);
			String decryptedMessage = new String(decrypt.doFinal(chiffre), StandardCharsets.UTF_8);
			dec = new String(decryptedMessage);
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return dec;
	}
	
	
	/**
	 * 
	 * @param username user to send the encrypt message
	 * @param message message to encrypt
	 * @return encrypted message as String
	 */
	public String encryptString(String username, String message) {
		byte[] encryptedMessage = null;
		try {
			Cipher encrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			encrypt.init(Cipher.ENCRYPT_MODE, pks.get(username));
			encryptedMessage = encrypt.doFinal(message.getBytes(StandardCharsets.UTF_8));
			
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		String b = "";
		for(int i = 0; i < encryptedMessage.length; i++) b += Byte.toString(encryptedMessage[i]) + " ";
		return b;
	}
	
	/**
	 * @param chiffre chiffre to decrypt as String
	 * @return decrypted message as String
	 */
	public String decryptString(String chiffre) {
		String[] splitted = chiffre.split(" ");
		byte[] b2 = new byte[splitted.length];
		for(int i = 0; i < b2.length; i++) b2[i] = Byte.parseByte(splitted[i]);
		String dec = null;
		try {
			Cipher decrypt=Cipher.getInstance("RSA/ECB/PKCS1Padding");
			decrypt.init(Cipher.DECRYPT_MODE, sk);
			String decryptedMessage = new String(decrypt.doFinal(b2), StandardCharsets.UTF_8);
			dec = new String(decryptedMessage);
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return dec;
	}
	
	/**
	 * 
	 * @param username user to send the encrypt message as byte[]
	 * @param message message to encrypt
	 * @return encrypted message as byte[]
	 */
	public byte[] encryptByte(String username, byte[] message) {
		byte[] encryptedMessage = null;
		try {
			Cipher encrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			encrypt.init(Cipher.ENCRYPT_MODE, pks.get(username));
			encryptedMessage = encrypt.doFinal(message);
			
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return encryptedMessage;
	}
	
	/**
	 * @param chiffre chiffre to decrypt as byte[]
	 * @return decrypted message as byte[]
	 */
	public byte[] decryptByte(byte[] chiffre) {
		byte[] dec = null;
		try {
			Cipher decrypt=Cipher.getInstance("RSA/ECB/PKCS1Padding");
			decrypt.init(Cipher.DECRYPT_MODE, sk);
			dec = decrypt.doFinal(chiffre);
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return dec;
	}

}
