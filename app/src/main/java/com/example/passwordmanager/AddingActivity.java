package com.example.passwordmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

public class AddingActivity extends AppCompatActivity {
    Button addButton;
    EditText appNameField;
    EditText userNameField;
    EditText passwordField;
    String keyValue;
    boolean visible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);

        //init stuff
        addButton = (Button) findViewById(R.id.final_add);
        appNameField = (EditText) findViewById(R.id.app_adding_field);
        userNameField = (EditText) findViewById(R.id.user_adding_field);
        passwordField = (EditText) findViewById(R.id.password_adding_field);


        addButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                Bundle bundle = getIntent().getExtras();
                if(bundle != null){
                    keyValue = bundle.getString("keyValue");
                }

                String appInput = appNameField.getText().toString();
                String userInput = userNameField.getText().toString();
                String passwordInput = passwordField.getText().toString();

                ArrayList<String[]> savedContent = getSavedContent();
                String[] adding = new String[]{appInput, userInput, passwordInput};
                savedContent.add(adding);
                updateContent(savedContent);
                apply();
            }
        });

        ImageView visibilityIcon = (ImageView) findViewById(R.id.adding_visibility_change);
        visibilityIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(visible) {
                    visibilityIcon.setImageResource(R.drawable.ic_eye);
                    passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    visible = false;
                }
                else
                {
                    visibilityIcon.setImageResource(R.drawable.ic_eye_off);
                    passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    visible = true;
                }
            }
        });

        ((Button) findViewById(R.id.gen_password_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passwordGen = "";
                Random random = new Random();
                char[] table = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"$%&/(){}=\\?+-*.:,;#<>_".toCharArray();
                for(int i = 0; i < 15; i++){
                    passwordGen += table[random.nextInt(table.length)];
                }
                visibilityIcon.setImageResource(R.drawable.ic_eye_off);
                passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                passwordField.setText(passwordGen);
                visible = true;
            }
        });
    }

    /**
     * returns the content in an decryted format
     * @return decrypred content
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String[]> getSavedContent(){
        String data = DataManager.getString("saved_stuff", getApplicationContext());
        data = new AES().decrypt(data, keyValue);
        return (ArrayList<String[]>) Serializer.deserializeString(data);
    }

    /**
     * Updates the current content.
     * the content will get encrypred first.
     * @param content content to update
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateContent(ArrayList<String[]> content){
        String data = Serializer.serializeString(content);
        data = new AES().encrypt(data, keyValue);
        DataManager.setString("saved_stuff", data, getApplicationContext());
    }

    private void apply(){
        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra("keyValue", keyValue);
        startActivity(intent);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        this.finishAffinity();
    }

}