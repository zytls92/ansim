package com.astuetz.viewpager.extensions.sample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class BeginView extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.begin);
/*        Button button=(Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeginView.this, Login.class);
                startActivity(intent);
            }
        });*/
       @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            public void handleMessage (Message msg){
                super.handleMessage(msg);
                //startActivity(new Intent(BeginView.this,Login.class));
                startActivity(new Intent(BeginView.this, Login.class));
                //setContentView(R.layout.login);
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0,3000);
    }
}
