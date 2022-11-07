package com.example.passwordmanager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Die abstrakte Klasse Hash implementiert statische Methoden zum hashen von Strings.
 * Sie enthaelt auch statische Variablen fuer die auswahl von Hashfunktionen als int die fuer
 * andere Klassen zur besseren lesbarkeit genutzt werden koennen
 */
public abstract class Hash
{
	public static final int SHA1 = 1;
	public static final int SHA256 = 256;
	public static final int SHA512 = 512;
	
    
    public static String sha512(String s){
        byte[] hash;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
            	sb.append(String.format("%02x", b));
            }
            return sb.toString();
	}catch(Exception e){}
	return null;
    }
    
    public static String sha256(String s){
        byte[] hash;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
            	sb.append(String.format("%02x", b));
            }
            return sb.toString();
	}catch(Exception e){}
	return null;
    }
    
    public static String sha1(String s){
        byte[] hash;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
            	sb.append(String.format("%02x", b));
            }
            return sb.toString();
	}catch(Exception e){}
	return null;
    }

    public static String sha512(String s, int iterations){
        for(int i = 0; i < iterations; i++){
            s = sha512(s);
        }
        return s;
    }

    public static String sha256(String s, int iterations){
        for(int i = 0; i < iterations; i++){
            s = sha256(s);
        }
        return s;
    }
}
