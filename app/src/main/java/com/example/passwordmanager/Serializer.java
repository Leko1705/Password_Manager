package com.example.passwordmanager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Wandelt ein Objekt in ein byte[] oder einen String um (Serialisierung) und wieder zurueck (Deserialisierung)
 * Die Uebergebenen Objekte muessen hierzu das interface java.io.Serializable implementieren.
 * Diese Klasse dient dazu Objekte so zu veraendern, dass sie verschickt werden koennen (umwandlung in Srtings oder
 * byte[])
 * Die Klasse ist abstract und kann somit keine Instanzen erzeugen. Sie enthaelt dementsprechend
 * statische methoden.
 * PublicKeys und PrivateKeys, so wie die mehrheit der Klassen des java.Frameworks implementieren
 * das Interface automatisch.
 */
public abstract class Serializer{
	
	// ---------- de-/serialisation from/to byte[] ----------
	
	/**
	 * Object --> byte[]
	 */
	public static byte[] serializeByte(Object obj){
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os;
		try {
			os = new ObjectOutputStream(out);
			os.writeObject(obj);
			return out.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	    return null;
	}
	
	/**
	 * byte[] --> Object
	 */
	public static Object deserializeByte(byte[] data){
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is;
		try {
			is = new ObjectInputStream(in);
			return is.readObject();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;
	}
	
	// ---------- de-/serialisation from/to String ----------
	/**
	 * Object --> String
	 */
	public static String serializeString(Object obj){
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os;
		try {
			os = new ObjectOutputStream(out);
			os.writeObject(obj);
			String b = "";
			for(int i = 0; i < out.toByteArray().length; i++) b += Byte.toString(out.toByteArray()[i]) + "_";
			return b;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	    return null;
	}
	
	/**
	 * String --> Object
	 */
	public static Object deserializeString(String data){
		String[] splitted = data.split("_");
		byte[] b2 = new byte[splitted.length];
		for(int i = 0; i < b2.length; i++) b2[i] = Byte.parseByte(splitted[i]);
	    ByteArrayInputStream in = new ByteArrayInputStream(b2);
	    ObjectInputStream is;
		try {
			is = new ObjectInputStream(in);
			return is.readObject();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;
	}
}
