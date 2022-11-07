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

public class ChangePasswordActivity extends AppCompatActivity {
    EditText first, second;
    Button confirmButton;
    String oldKey;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            oldKey = bundle.getString("keyValue");
        }

        //init stuff
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

                 /**
                  * if both inputs are equal the password will be saved
                  * by hashing it first.
                  * otherwise a 'error message' will be shown.
                  */
                 if(firstInput.length() != 0 && secondInput.length() != 0){
                     if(firstInput.equals(secondInput)){

                         refreshDataEncryption(oldKey, firstInput);
                         String hashed = Spicer.spice(firstInput, getApplicationContext());
                         if(DataManager.getString("hashType", getApplicationContext()).equals("sha_512"))
                         {
                             hashed = Hash.sha512(hashed, 2000);
                         }
                         else{
                             hashed = Hash.sha256(hashed, 2000);
                         }
                         //hash and safe password
                         DataManager.setString("passwd", hashed, getApplicationContext());
                         Intent intent = new Intent(ChangePasswordActivity.this, SettingActivity.class);
                         intent.putExtra("keyValue", firstInput);
                         startActivity(intent);

                     }else{
                         String errorMessage = "passwords does not match!";
                         Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_SHORT)
                                 .show();
                     }
                 }
                 else{
                     String errorMessage = "fill in both password-fields!";
                     Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_SHORT)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateContent(ArrayList<String[]> content, String key){
        String data = Serializer.serializeString(content);
        data = new AES().encrypt(data, key);
        DataManager.setString("saved_stuff", data, getApplicationContext());
    }


}