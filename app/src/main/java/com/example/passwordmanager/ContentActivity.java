package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.security.NoSuchProviderException;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Objects;

public class ContentActivity extends AppCompatActivity implements RecyclerViewInterface{
    RecyclerView list;
    ArrayList<LoginData> contentList;
    ImageButton addButton;
    String keyValue;
    String[] added;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            keyValue = bundle.getString("keyValue");
            added = bundle.getStringArray("receivedData");
        }

        int count = DataManager.getInt("rateAskCount", getApplicationContext());
        if(count == 5){
            View view = getLayoutInflater().inflate(R.layout.rate_ask, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();

            ((RatingBar) bottomSheetDialog.findViewById(R.id.ratingBar)).setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    openPlaystore();
                }
            });

        }
        DataManager.setInt("rateAskCount", ++count, getApplicationContext());

        //init stuff
        contentList = getSavedContent(); //fills contentList

        list = (RecyclerView) findViewById(R.id.contentList);
        addButton = (ImageButton) findViewById(R.id.addButton);

        //create recyclerView
        list.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        list.setAdapter(new ContentAdapter(contentList, getApplicationContext(), this));

        TextView zeroCount = findViewById(R.id.zero_count);
        if(list.getAdapter().getItemCount() > 0){
            zeroCount.setVisibility(View.INVISIBLE);
        }

        ((ImageView) findViewById(R.id.rate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlaystore();
            }
        });

        ((ImageView) findViewById(R.id.share_app)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Password Manager");
                    String shareMessage = "\nLet me recommend you this application\n\n";
                    shareMessage += "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
                finishAffinity();
            }
        });

        ((Button) findViewById(R.id.share_token)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enc = DataManager.getString("rsa", getApplicationContext());
                String dec = new AES().decrypt(enc, keyValue);
                RSA rsa = (RSA) Serializer.deserializeString(dec);
                assert rsa != null;
                PublicKey pk = rsa.getPublicKey();
                byte[] byte_pubkey = pk.getEncoded();
                String publicKey = Base64.getEncoder().encodeToString(byte_pubkey);
                String myName = DataManager.getString("myName", getApplicationContext());
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String msg = "Password Manager\n\n"
                        + "Here is my Token:\nhttps://www.pwm.com/pk/" + myName + "/" + publicKey + "/";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                }
                finishAffinity();
            }
        });
    }

    private String toHexString(String publicKey) {
        String[] split = publicKey.split("_");
        String hex[] = new String[split.length];
        boolean[] negative = new boolean[hex.length];
        for(int i = 0; i < split.length; i++){
            int p = Integer.parseInt(split[i]);
            if(p < 0){
                p *= -1;
                negative[i] = true;
            }
            hex[i] = Integer.toHexString(p);
        }
        String res = "";
        for(int i = 0; i < hex.length; i++){
            if(negative[i]) res += "n";
            res += hex[i] + "_";
        }
        res = res.substring(0, res.length()-1);
        return res;
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    @Override
    public void onTrimMemory(int level) {

        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            finishAffinity();
        }
    }


    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<LoginData> getSavedContent(){
        String data = DataManager.getString("saved_stuff", getApplicationContext());
        data = new AES().decrypt(data, keyValue);
        ArrayList<String[]> l = (ArrayList) Serializer.deserializeString(data);
        ArrayList<LoginData> login = new ArrayList<>();
        for(int i = 0; i < l.size(); i++){
            String app = l.get(i)[0];
            String user = l.get(i)[1];
            String passwd = l.get(i)[2];

            login.add(new LoginData(app, user, passwd));
        }
        if(added != null){
            if(added[0].equals("ec")) {
                String encMessage = added[1];
                String rsaString = DataManager.getString("rsa", getApplicationContext());
                rsaString = new AES().decrypt(rsaString, keyValue);
                RSA rsa = (RSA) Serializer.deserializeString(rsaString);
                String decMessage = rsa.decryptString(encMessage);
                String[] acc = decMessage.split(" ");
                String app = acc[0];
                String user = acc[1];
                String passwd = acc[2];
                login.add(new LoginData(app, user, passwd));

                //instant update
                data = Serializer.serializeString(login);
                data = new AES().encrypt(data, keyValue);
                DataManager.setString("saved_stuff", data, getApplicationContext());
            }
            else if(added[0].equals("pk")){
                PublicKey pk = null;
                byte[] byte_pubkey  = Base64.getDecoder().decode(added[2]);
                KeyFactory factory;
                try {
                    factory = KeyFactory.getInstance("ECDSA", "BC");
                    pk = (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(byte_pubkey));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String enc = DataManager.getString("rsa", getApplicationContext());
                String dec = new AES().decrypt(enc, keyValue);
                RSA rsa = (RSA) Serializer.deserializeString(dec);
                rsa.add(added[1], pk);
                enc = DataManager.getString("friends", getApplicationContext());
                dec = new AES().decrypt(enc, keyValue);
                ArrayList<String> friends = (ArrayList<String>) Serializer.deserializeString(dec);
                friends.add(added[1]);
                dec = Serializer.serializeString(friends);
                enc = new AES().encrypt(dec, keyValue);
                DataManager.setString("friends", enc, getApplicationContext());
            }
        }
        return login;
    }

    public void addInformation(View view) {
        Intent intent = new Intent(this, AddingActivity.class);
        intent.putExtra("keyValue", keyValue);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("keyValue", keyValue);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemLongClick(int pos){
        contentList.remove(pos);
        String data = DataManager.getString("saved_stuff", getApplicationContext());
        data = new AES().decrypt(data, keyValue);
        ArrayList<String[]> l = (ArrayList) Serializer.deserializeString(data);
        l.remove(pos);
        data = Serializer.serializeString(l);
        data = new AES().encrypt(data, keyValue);
        DataManager.setString("saved_stuff", data, getApplicationContext());
        TextView zeroCount = findViewById(R.id.zero_count);
        Objects.requireNonNull(list.getAdapter()).notifyItemRemoved(pos);
        if(list.getAdapter().getItemCount() > 0){
            zeroCount.setVisibility(View.INVISIBLE);
        }
        else{
            zeroCount.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int pos) {
        String data = DataManager.getString("saved_stuff", getApplicationContext());
        data = new AES().decrypt(data, keyValue);
        ArrayList<String[]> l = (ArrayList) Serializer.deserializeString(data);
        Intent intent = new Intent(this, ContentInfoActivity.class);
        intent.putExtra("stuff", l.get(pos));
        intent.putExtra("keyValue", keyValue);
        intent.putExtra("pos", pos);
        startActivity(intent);
    }

    private void openPlaystore(){
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            finishAffinity();
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            finishAffinity();
        }
    }

}