package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    String keyValue;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        String username = DataManager.getString("myName", getApplicationContext());
        String addition = "";
        int index = 0;
        for(int i = username.length()-1; i >= 0; i--){
            if(username.charAt(i) != '-'){
                addition = username.charAt(i) + addition;
            }
            else{
                addition = username.charAt(i) + addition;
                index = i-1;
                break;
            }
        }
        String name = "";
        for(int i = index; i >= 0; i--){
            name = username.charAt(i) + name;
        }
        String finalname = "username:\n" + name + "\n" + addition;
        ((TextView) findViewById(R.id.show_current_username_information)).setText(finalname);

        ((TextView) findViewById(R.id.licenses)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OssLicensesMenuActivity.setActivityTitle("");
                startActivity(new Intent(SettingActivity.this, OssLicensesMenuActivity.class));
            }
        });

        ((TextView) findViewById(R.id.help)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OssLicensesMenuActivity.setActivityTitle("");
                startActivity(new Intent(SettingActivity.this, OssLicensesMenuActivity.class));
            }
        });

        String hashType = DataManager.getString("hashType", getApplicationContext());

        if(hashType.equals("sha_512")) {
            ((RadioButton) findViewById(R.id.sha_256_button)).setChecked(false);
            ((RadioButton) findViewById(R.id.sha_512_button)).setChecked(true);
        }
        else{
            ((RadioButton) findViewById(R.id.sha_256_button)).setChecked(true);
            ((RadioButton) findViewById(R.id.sha_512_button)).setChecked(false);
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            keyValue = bundle.getString("keyValue");
        }

        ((TextView) findViewById(R.id.show_current_passwd)).setText(keyValue);
        String version = "-- version " + BuildConfig.VERSION_NAME + " --";
        ((TextView) findViewById(R.id.version_field)).setText(version);

        TextView textview = (TextView) findViewById(R.id.show_settings_title);
        textview.setPaintFlags(textview.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        textview = (TextView) findViewById(R.id.show_current_passwd_information);
        textview.setPaintFlags(textview.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        textview = (TextView) findViewById(R.id.show_current_hash_type_information);
        textview.setPaintFlags(textview.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

    }

    public void toSha256(View view){
        DataManager.setString("hashType", "sha_256", getApplicationContext());
        DataManager.setString("passwd", Hash.sha256(keyValue), getApplicationContext());
    }

    public void toSha512(View view){
        DataManager.setString("hashType", "sha_512", getApplicationContext());
        DataManager.setString("passwd", Hash.sha512(keyValue), getApplicationContext());
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra("keyValue", keyValue);
        startActivity(intent);
    }

    public void refreshPassword(View view){
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra("keyValue", keyValue);
        startActivity(intent);
    }
}