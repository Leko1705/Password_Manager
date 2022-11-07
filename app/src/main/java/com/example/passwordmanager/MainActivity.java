package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import java.util.List;

public class MainActivity extends AppCompatActivity{
    SharedPreferences preferences;
    EditText passwdField;
    Button confirmButton;
    TextView wrongPasswd;
    AppUpdateManager appUpdateManager;
    boolean visible = false;
    List<String> params = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appUpdateManager = AppUpdateManagerFactory.create(this);
        checkForUpdate();
        wrongPasswd = (TextView) findViewById(R.id.wrong_password);
        wrongPasswd.setPaintFlags(wrongPasswd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        passwdField = (EditText) findViewById(R.id.passwdEntering);
        confirmButton = (Button) findViewById(R.id.loginButton);
        Uri uri = getIntent().getData();
        if(uri != null){
            params = uri.getPathSegments();
        }
        firstLoginCheck();
    }

    private void checkForUpdate() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, MainActivity.this, 22);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                        Log.d("updateError", "onSuccess " + e.toString());
                    }

                }
            }
        });
    }

    /**
     * checks for the very first app execution.
     * if it is the very fist one, a password must be created,
     * otherwise the password must be entered.
     */
    private void firstLoginCheck(){
        preferences = this.getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = preferences.getBoolean("first_execution", true);
        if(firstStart){
            preferences.edit().putInt("lastKnownAppVersion", BuildConfig.VERSION_CODE).apply();
            preferences.edit().putInt("rateAskCount", 0).apply();
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
        else{
            updateAppContent();
            login();
        }
    }

    private void updateAppContent() {
        int lastKnownVersion = preferences.getInt("lastKnownAppVersion", 0);
        if (lastKnownVersion < BuildConfig.VERSION_CODE) {
            preferences.edit().putInt("lastKnownAppVersion", BuildConfig.VERSION_CODE).apply();

            //TODO update stuff
            if(lastKnownVersion == 1){

            }
        }
    }

    private void login(){
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                String passwdHash = preferences.getString("passwd", "NONE");
                String passwdInput = passwdField.getText().toString();
                passwdInput = Spicer.spice(passwdInput, getApplicationContext());
                if(DataManager.getString("hashType", getApplicationContext()).equals("sha_512"))
                {
                    passwdInput = Hash.sha512(passwdInput, 2000);
                }
                else{
                    passwdInput = Hash.sha256(passwdInput, 2000);
                }
                if(passwdHash.equals("NONE")){
                    toast("Something went wrong!");
                }
                else{
                    if(passwdHash.equals(passwdInput)){
                        onCreateContentActivity(passwdField.getText().toString());
                    }else{
                        passwdField.clearFocus();
                        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                        passwdField.setText("");
                        wrongPasswd.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        ImageView visibilityIcon = (ImageView) findViewById(R.id.adding_visibility_change);
        visibilityIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(visible) {
                    visibilityIcon.setImageResource(R.drawable.ic_eye);
                    passwdField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    visible = false;
                }
                else
                {
                    visibilityIcon.setImageResource(R.drawable.ic_eye_off);
                    passwdField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    visible = true;
                }
            }
        });
    }

    private void onCreateContentActivity(String msg){
        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra("keyValue", msg);
        if(params != null){
            String[] data = new String[params.size()];
            for(int i = 0; i < data.length; i++){
                data[i] = params.get(i);
            }
            intent.putExtra("receivedData", data);
        }
        startActivity(intent);
    }

    private void toast(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, MainActivity.this, 22);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                        Log.d("updateError", "onSuccess " + e.toString());
                    }

                }
            }
        });
    }
}