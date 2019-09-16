package com.astuetz.viewpager.extensions.sample;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignUp extends AppCompatActivity {

    private TextView tvData;
    private EditText emailfield;
    private EditText nameield;
    private EditText passwordfield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        emailfield = (EditText)findViewById(R.id.etEmail);
        nameield = (EditText)findViewById(R.id.user_name);
        passwordfield = (EditText)findViewById(R.id.etPassword);

        Button joinbutton =  (Button)findViewById(R.id.sign_up);

        //Button btn = (Button)findViewById(R.id.httpTest);
        //버튼이 클릭되면 여기 리스너로 옴

        joinbutton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(SessionMake.httpUrls+"signup");
                new SignUpTask().execute(SessionMake.httpUrls+"signup");
            }
        });

//        goLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(SignUp.this, LoginPage.class);
//                startActivity(intent);
//
//                finish();
//            }
//        });

    }

    public class SignUpTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String urls[]) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", emailfield.getText());
                jsonObject.accumulate("name", nameield.getText());
                jsonObject.accumulate("pw", passwordfield.getText());
                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL(“http://192.168.25.16:3000/users“);
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "application/json");//서버에 response 데이터를 html로 받음


                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();
                    //서버로 보내기위해서 스트림 만듬

                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));

                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder buffer = new StringBuilder();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }
                    //System.out.println("memememe"+buffer.toString());
                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            if(result.equals("signupsuccess")){
//                Toast.makeText(getApplicationContext(),"회원가입 성공",Toast.LENGTH_LONG).show();
//            }
            Intent intent=new Intent(SignUp.this, Login.class);
            startActivity(intent);
            finish();
        }
    }


}