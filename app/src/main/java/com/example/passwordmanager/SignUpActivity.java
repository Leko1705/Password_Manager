package com.example.passwordmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {
    EditText first, second, userField;
    Button confirmButton;
    String oldKey;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //init stuff
        userField = (EditText) findViewById(R.id.UsernameInput);
        first = (EditText) findViewById(R.id.firstPasswdInput);
        second = (EditText) findViewById(R.id.secondPasswdInput);
        confirmButton = (Button) findViewById(R.id.accept_button);

        listenButton();
    }

    private void listenButton(){
        /**
         * listen to button
         */
        confirmButton.setOnClickListener(new View.OnClickListener(){
                 @RequiresApi(api = Build.VERSION_CODES.O)
                 @Override
                 public void onClick(View view){

                     String firstInput = first.getText().toString();
                     String secondInput = second.getText().toString();
                     String name = userField.getText().toString();

                     /**
                      * if both inputs are equal the password will be saved
                      * by hashing it first.
                      * otherwise a 'error message' will be shown.
                      */
                     if(userField.length() != 0) {
                         if (firstInput.length() != 0 && secondInput.length() != 0) {
                             if (firstInput.equals(secondInput)) {
                                 name = name.trim();
                                 String nn = "";
                                 for(int i = 0; i < name.length(); i++){
                                     if(name.charAt(i) != ' ') nn += name.charAt(i);
                                     else nn += '_';
                                 }
                                 name = nn;
                                 name += "-" + Integer.toString(new Random().nextInt(10000));
                                 DataManager.setString("myName", name, getApplicationContext());
                                 //sign up complete
                                 SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);

                                 String toHash = Spicer.spice(firstInput, getApplicationContext());
                                 //hash and safe password
                                 String hash = Hash.sha512(toHash, 2000);
                                 DataManager.setString("passwd", hash, getApplicationContext());
                                 DataManager.setString("hashType", "sha_512", getApplicationContext());
                                 preferences.edit().putBoolean("first_execution", false).apply();
                                 ArrayList<String[]> toSave = new ArrayList<>();
                                 updateContent(toSave, firstInput);

                                 RSA rsa = new RSA();
                                 String serialized = Serializer.serializeString(rsa);
                                 serialized = new AES().encrypt(serialized, firstInput);
                                 DataManager.setString("rsa", serialized, getApplicationContext());

                                 ArrayList<String> friends = new ArrayList<>();
                                 serialized = Serializer.serializeString(friends);
                                 serialized = new AES().encrypt(serialized, firstInput);
                                 DataManager.setString("friends", serialized, getApplicationContext());
                                 //auto login
                                 onCreateContentActivity(firstInput);

                             } else {
                                 String errorMessage = "passwords does not match!";
                                 Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT)
                                         .show();
                             }
                         } else {
                             String errorMessage = "fill in both password-fields!";
                             Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT)
                                     .show();
                         }
                     }else {
                         String errorMessage = "enter a username";
                         Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT)
                                 .show();
                     }
                 }
             }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void refreshDataEncryption(String oldKey, String newKey) {
        String data = DataManager.getString("saved_stuff", getApplicationContext());
        data = new AES().decrypt(data, oldKey);
        data = new AES().encrypt(data, newKey);
        DataManager.setString("saved_stuff", data, getApplicationContext());
    }

    private void onCreateContentActivity(String msg){
        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra("keyValue", msg);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateContent(ArrayList<String[]> content, String key){
        String data = Serializer.serializeString(content);
        data = new AES().encrypt(data, key);
        DataManager.setString("saved_stuff", data, getApplicationContext());
    }


}