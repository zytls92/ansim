/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.astuetz.viewpager.extensions.sample;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.BindView;

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
import java.util.StringTokenizer;
import java.util.Vector;

import static com.astuetz.viewpager.extensions.sample.R.id.pager;

public class Menu_friend  extends ActionBarActivity {

    Vector<String> friend_list;
    Vector<String> sms_number_list;

    /////////////////////////////////////////////////////////////디자인
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @BindView(R.id.pager)
    ViewPager pager;

    private MainActivity.MyPagerAdapter adapter;
    private Drawable oldBackground = null;
    private int currentColor;
    private SystemBarTintManager mTintManager;
    private TextView text;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_friend);
        text = (TextView) findViewById(R.id.tttext);
        ImageButton plus_friend = (ImageButton) findViewById(R.id.pplus);

        plus_friend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Menu_friend.this,AddFriendActivity.class);
                startActivity(intent);
                finish();
            }
        });

        new FriendListTask().execute(SessionMake.httpUrls+"retrieve/friends");
    }



////////////////////////////////////////////////////////////////////디자인





    /////////////////////////////////////////////////////////////////////////디자인코드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact:
                com.astuetz.viewpager.extensions.sample.QuickContactFragment.newInstance().show(getSupportFragmentManager(), "QuickContactFragment");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //changecolor 없애버림
    private void changeColor(int newColor) {
        tabs.setBackgroundColor(newColor);
        mTintManager.setTintColor(newColor);
        // change ActionBar color just if an ActionBar is available
        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = new ColorDrawable(ContextCompat.getColor(getBaseContext(), android.R.color.transparent));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
        if (oldBackground == null) {
            getSupportActionBar().setBackgroundDrawable(ld);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});
            getSupportActionBar().setBackgroundDrawable(td);
            td.startTransition(200);
        }

        oldBackground = ld;
        currentColor = newColor;
    }
    public void onColorClicked(View v) {
        int color = Color.parseColor(v.getTag().toString());
        changeColor(color);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        changeColor(currentColor);
    }

    ////여기까지 색바꾸는ㄱㄴ거

    public class FriendListTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String urls[]) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("session", SessionMake.getSession());
                jsonObject.accumulate("signal","hi");
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
//                    String cookieString = CookieManager.getInstance().getCookie("http://192.168.0.121:3000/");
//                    if (cookieString != null) {
//                        con.setRequestProperty("Cookie", cookieString);
//                    }

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

                    //JSON 토큰화

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
      //      System.out.println(result);
            //토큰 구분
            StringTokenizer st = new StringTokenizer(result, "\",[]");
            int i=0;
            while (st.hasMoreTokens()) {
                if(i%2==0){
       //             if(st.nextToken() != null)
     //                   System.out.println(st.nextToken().toString());
                  //      friend_list.add(st.nextToken().toString());
                } else {
                    if(st.nextToken() != null)
                        System.out.println(st.nextToken().toString());
                //        sms_number_list.add(st.nextToken().toString());
                }
                i++;
            }
            for(int k = 0; k<friend_list.size(); k++){
                System.out.println(friend_list.get(k));
            }
            String tmp="";
            for(int j=0;j<friend_list.size()-1;j++) {
                tmp+=String.format(text.getText().toString() +"번호 : " +sms_number_list.elementAt(j).toString()+"\n");
                text.setText(tmp);
            }//친구들 화면에 보이기 추가

        }
    }
}

