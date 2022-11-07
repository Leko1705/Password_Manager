package com.example.passwordmanager;

import android.content.Context;

import java.util.Random;

public class Spicer {
    private static final String pepper = "meow";

    public static String spice(String password, Context context){
        String salt = DataManager.getString("salt", context);
        if(salt.length() == 0){
            salt = genSalt();
            DataManager.setString("salt", salt, context);
        }
        password += salt + pepper;
        return password;
    }

    private static String genSalt() {
        char[] table = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$%&/()={}+*~#-_.,;:\"\\".toCharArray();
        String salt = "";
        Random random = new Random();
        for(int i = 0; i < 6; i++){
            salt += table[random.nextInt(table.length)];
        }
        return salt;
    }
}
