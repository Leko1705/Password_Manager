package com.example.passwordmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SendActivity extends AppCompatActivity  implements RecyclerViewInterfaceSend{

    RecyclerView list;
    String keyValue;
    String[] data;
    ArrayList<String> contentList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            keyValue = bundle.getString("keyValue");
            data = bundle.getStringArray("specificKey");
        }
        contentList = getContentList();
        if(contentList.size() == 0){
            ((TextView) findViewById(R.id.zero_count_friends)).setVisibility(View.VISIBLE);
        }
        list = (RecyclerView) findViewById(R.id.friend_list);

        list.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        list.setAdapter(new SendingAdapter(contentList, getApplicationContext(), this));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String> getContentList(){
        String encFriends = DataManager.getString("friends", getApplicationContext());
        String decFriends = new AES().decrypt(encFriends, keyValue);
        return (ArrayList<String>) Serializer.deserializeString(decFriends);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int pos) {
        String encrypted = DataManager.getString("rsa", getApplicationContext());
        String decrypted = new AES().encrypt(encrypted, keyValue);
        RSA rsa = (RSA) Serializer.deserializeString(decrypted);
        String toEnc = data[0] + " " + data[1] + " " + data[2];
        String encMsg = rsa.encryptString(contentList.get(pos), toEnc);
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Password Manager");
            String msg = "Here is my Password: https://www.pwm.com/ec/" +  encMsg;
            shareIntent.putExtra(Intent.EXTRA_TEXT, msg);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }

    }
}