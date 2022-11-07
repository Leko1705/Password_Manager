package com.example.passwordmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContentInfoActivity extends AppCompatActivity {

    String[] data;
    int pos;
    String keyValue;

    private static final String GOOGLE = "google";
    private static final String NETFLIX = "netflix";
    private static final String INSTAGRAM = "instagram";
    private static final String FACEBOOK = "FACEBOOK";
    private static final String TIKTOK = "tiktok";
    private static final String GITHUB = "github";
    private static final String GIT = "git";
    private static final String REDDIT = "reddit";
    private static final String AMAZON = "amazon";
    private static final String YOUTUBE = "youtube";
    private static final String WINDOWS = "windows";
    private static final String APPLE = "apple";
    private static final String LINKED_IN = "linked_in";
    private static final String DROP_BOX = "drop_box";
    private static final String TWITTER = "twitter";
    private static final String DISCODR = "discord";
    private static final String STEAM = "steam";
    private static final String EPICGAMES = "epicgames";
    private static final String SPOTIFY = "spotify";
    private static final String SNAPCHAT = "snapchat";
    private static final String PAYPAL = "paypal";
    private static final String PINTEREST = "pinterest";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_info);

        TextView textview = (TextView) findViewById(R.id.pwd_info_title);
        textview.setPaintFlags(textview.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            data = bundle.getStringArray("stuff");
            pos = bundle.getInt("pos");
            keyValue = bundle.getString("keyValue");
        }

        ((TextView) findViewById(R.id.appname_pwd_info)).setText( "Appname/Website: " + data[0]);
        ((TextView) findViewById(R.id.username_pwd_info)).setText("Username/Email: " + data[1]);
        ((TextView) findViewById(R.id.password_pwd_info)).setText("Password: " + data[2]);


        String password = data[2];

        int strength = 100;
        char[] tableLowerCase = "abcdefghijklmnopqrstuvwxyzß".toCharArray();
        char[] tableUpperCase = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();
        char[] tableSpecial = "!\"$@%&/()=?{[]}/+*#:;.\\,-_<>".toCharArray();
        int length = password.length();
        boolean lc = false, uc = false, n = false, s = false;

        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar_strength);


        if(length < 12) strength -= 5;
        if(length < 8) strength -= 10;
        if(length < 6) strength -= 20;
        if(length < 5) strength -= 20;
        if(length < 4) strength -= 20;
        for (char c : tableLowerCase) {
            for (int j = 0; j < password.length(); j++) {
                if (c == password.charAt(j)) {
                    lc = true;
                    break;
                }
            }
        }
        for (char c : tableUpperCase) {
            for (int j = 0; j < password.length(); j++) {
                if (c == password.charAt(j)) {
                    uc = true;
                    break;
                }
            }
        }
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < password.length(); j++){
                if(i == Character.getNumericValue(password.charAt(j))){
                    n = true;
                    break;
                }
            }
        }
        for (char c : tableSpecial) {
            for (int j = 0; j < password.length(); j++) {
                if (c == password.charAt(j)) {
                    s = true;
                    break;
                }
            }
        }

        if(!lc) strength -= 20;
        if(!uc) strength -= 20;
        if(!n) strength -= 20;
        if(!s) strength -= 10;

        if(strength < 0) strength = 0;
        bar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        if(strength < 33) {
            Drawable progressDrawable = bar.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            bar.setProgressDrawable(progressDrawable);
        }
        else if(33 <= strength && strength < 50){
            Drawable progressDrawable = bar.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.rgb(255, 131, 0), android.graphics.PorterDuff.Mode.SRC_IN);
            bar.setProgressDrawable(progressDrawable);
        }
        else if(50 <= strength && strength < 80) {
            Drawable progressDrawable = bar.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
            bar.setProgressDrawable(progressDrawable);
        }
        else if(strength >= 80) {
            Drawable progressDrawable = bar.getProgressDrawable().mutate();
            progressDrawable.setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
            bar.setProgressDrawable(progressDrawable);
        }



        bar.setProgress(0); // call these two methods before setting progress.
        bar.setMax(100);
        bar.setProgress(strength);

        ((TextView) findViewById(R.id.progress_text_safety)).setText("Password strength: " + strength + "%");

        logoSet((ImageView) findViewById(R.id.logo_view), data[0]);;

        ((Button) findViewById(R.id.pwd_info_copy)).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void onClick(View view) {
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) ContentInfoActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(password);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) ContentInfoActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", password);
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(ContentInfoActivity.this.getApplicationContext(), "password copied", Toast.LENGTH_SHORT).show();
            }
        });

        ((Button) findViewById(R.id.pwd_info_send)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentInfoActivity.this, SendActivity.class);
                intent.putExtra("keyValue", keyValue);
                intent.putExtra("data", data);
                startActivity(intent);
            }
        });

    }

    private void logoSet(ImageView img, String app){
        app = app.toLowerCase();
        app = app.trim();
        switch (app){
            case GOOGLE:
                img.setImageResource(R.mipmap.ic_logo_google);
                return;

            case NETFLIX:
                img.setImageResource(R.mipmap.ic_logo_netflix);
                return;

            case INSTAGRAM:
                img.setImageResource(R.mipmap.ic_logo_instagram);
                return;

            case FACEBOOK:
                img.setImageResource(R.mipmap.ic_logo_facebook);
                return;

            case TIKTOK:
                img.setImageResource(R.mipmap.ic_logo_tiktok);
                return;

            case GITHUB:
                img.setImageResource(R.mipmap.ic_logo_github);
                return;

            case GIT:
                img.setImageResource(R.mipmap.ic_logo_git);
                return;

            case REDDIT:
                img.setImageResource(R.mipmap.ic_logo_reddit);
                return;

            case AMAZON:
                img.setImageResource(R.mipmap.ic_logo_amazon);
                return;

            case YOUTUBE:
                img.setImageResource(R.mipmap.ic_logo_youtube);
                return;

            case WINDOWS:
                img.setImageResource(R.mipmap.ic_logo_windows);
                return;

            case APPLE:
                img.setImageResource(R.mipmap.ic_logo_apple);
                return;

            case LINKED_IN:
                img.setImageResource(R.mipmap.ic_logo_linked_in);
                return;

            case DROP_BOX:
                img.setImageResource(R.mipmap.ic_logo_drop_box);
                return;

            case TWITTER:
                img.setImageResource(R.mipmap.ic_logo_twitter);
                return;

            case DISCODR:
                img.setImageResource(R.mipmap.ic_logo_discord);
                return;

            case STEAM:
                img.setImageResource(R.mipmap.ic_logo_steam);
                return;

            case EPICGAMES:
                img.setImageResource(R.mipmap.ic_logo_epicgames);
                return;

            case SPOTIFY:
                img.setImageResource(R.mipmap.ic_logo_spotify);
                return;

            case SNAPCHAT:
                img.setImageResource(R.mipmap.ic_logo_snapchat);
                return;

            case PAYPAL:
                img.setImageResource(R.mipmap.ic_logo_paypal);
                return;

            case PINTEREST:
                img.setImageResource(R.mipmap.ic_logo_pinterest);
                return;
        }

        if(app.startsWith("http://") || app.startsWith("https://") || app.startsWith("www.")){
            img.setImageResource(R.mipmap.ic_logo_any_website);
            return;
        }

        img.setImageResource(R.mipmap.ic_logo_any_app);
    }
}