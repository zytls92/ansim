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
//
package com.astuetz.viewpager.extensions.sample;
import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import java.util.Vector;



import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;

import net.daum.mf.map.api.MapView;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.astuetz.viewpager.extensions.sample.R.id.pager;

public class MainActivity extends ActionBarActivity implements MapView.MapViewEventListener, MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener,MapView.POIItemEventListener{

    private boolean DrawToggle; //MapMode On/off
    private boolean GpsToggle; //GpsTrackMode On/off
    private boolean TrackToggle; // Tracking begin/end
    private MapView myMapview; //Mapview
    private MapPolyline mypolyline;
    private MapPOIItem mymarker;
    private MapPoint CurrentLocation;
    private int trackingindex;
    private Vector<MapPoint> points;
    private Vector<MapPoint> newpoints;
    private int circleid;
    //////////////////////////Jungwoo
    private Vector vec_latitude = new Vector();
    private Vector vec_longitude = new Vector();
    private Vector vec_PlainX = new Vector();
    private Vector vec_PlainY = new Vector();
    private Vector vec_mapPoint = new Vector();
    private Context cctext=this;
    private TrackingMode1 Tracking1;
   // private TrackingMode2 Tracking2 = new TrackingMode2(cctext);
    //private TrackingMode3 Tracking3;
    private TrackingMode4 Tracking4 = new TrackingMode4();
    private Boolean Track_status1 = false;
    private Boolean Track_status2 = false;
    private Boolean Track_status3 = false;
    private Boolean Track_status4 = false;
    private Boolean Check_Track0 = false;
    private Boolean Check_Track1 = false;
    private Boolean Check_Track2 = false;
    private Boolean Check_Track3 = false;
    private Boolean Check_Track4 = false;
    private int Check_Track_Count = 0;
    private int Safe = -1;
    private Boolean SendMessage = false;
    private String Message;
    private String phoneNumber;
    ///////////////////////////////   minsu
    double mySpeed, maxSpeed;
    private LocationManager lm;             //speed를 구하기위해 쓰는거인데 잘모름
    private LocationListener ll;            //speed를 구하기위해 쓰는거인데 잘모름
    private Vector<MapPointFlag> vec_MapPointFlag = new Vector<>();
    //////////////////////////// song
    MapPoint DEFAULT_MARKER_POINT ;
    public static MapPoint.GeoCoordinate currentpoint;
    private MapPOIItem mDefaultMarker;
    //////////////////////////
    private static final double limitmeter = 17;


// myMapview.setDaumMapApiKey("343c6f878361c5801dd4cda2533023d9");
    /////////////////////////////////////////////////////////////디자인
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @BindView(R.id.pager)
    ViewPager pager;

    private MyPagerAdapter adapter;
    private Drawable oldBackground = null;
    private int currentColor;
    private SystemBarTintManager mTintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
        //sendSMS("01051432258", "쓰레기새끼네");
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    0);
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},
                    0);
        }if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    0);
        }if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //return;
        }
        //XML 버튼 불러오기
        ImageButton draw_begin=(ImageButton) findViewById(R.id.button1);
        ImageButton draw_end=(ImageButton) findViewById(R.id.button2);
        ImageButton gps_toggle=(ImageButton) findViewById(R.id.button3);
        ImageButton track_toggle=(ImageButton) findViewById(R.id.button4);
        ImageButton bustercall=(ImageButton) findViewById(R.id.button5);
        //이벤트 리스너
        draw_begin.setOnClickListener(DrawBeginListener);
        draw_end.setOnClickListener(DrawEndListener);
        gps_toggle.setOnClickListener(GpsToggleListener);
        track_toggle.setOnClickListener(TrackingToggleListener);
        bustercall.setOnClickListener(BusterListener);
        //private variable 초기화
        DrawToggle=false;
        GpsToggle=false;
        TrackToggle = false;
        mypolyline=new MapPolyline();
        mymarker=new MapPOIItem();
        points = new Vector<>();
        circleid = 10;
        ///////
        //////////////////////// minsu
        maxSpeed = mySpeed = 0;
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ll = new com.astuetz.viewpager.extensions.sample.MainActivity.SpeedoActionListener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        /////////////////////////
        //MapView Initialized
        myMapview = new MapView(this);
        myMapview.setDaumMapApiKey("343c6f878361c5801dd4cda2533023d9");
        RelativeLayout container = (RelativeLayout) findViewById(R.id.map_view);
        myMapview.setMapViewEventListener(this);
        myMapview.setCurrentLocationEventListener(this);
        myMapview.setZoomLevel(1, true);
        //내 현재위치 표시
        myMapview.setShowCurrentLocationMarker(true);
        container.addView(myMapview);



////////////////////////////////////////////////////////////////////디자인
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        // create our manager instance after the content view is set
        mTintManager = new SystemBarTintManager(this);
        // enable status bar tint
        mTintManager.setStatusBarTintEnabled(true);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        //  tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(1);
        changeColor(ContextCompat.getColor(getBaseContext(), R.color.banana));
    /*   // tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) { Toast.makeText(MainActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    ImageButton.OnClickListener BusterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendSMS(phoneNumber, Message);
        }
    };
    ImageButton.OnClickListener DrawBeginListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if(DrawToggle){
                Toast.makeText(getApplicationContext(),"DrawToggle : Off",Toast.LENGTH_LONG).show();
                DrawToggle=false;
                // Thread_Switch=false;  song`s Thread
                myMapview.removeAllPOIItems();
                myMapview.removeAllPolylines();
                myMapview.removeAllCircles();
                mypolyline = new MapPolyline();
                points = new Vector<>();
                circleid = 10;
                vec_mapPoint = new Vector<>();
                vec_PlainY = new Vector<>();
                vec_PlainX = new Vector<>();
                vec_latitude = new Vector<>();
                vec_longitude = new Vector<>();
                vec_MapPointFlag = new Vector<>();
                Tracking1 = new TrackingMode1(vec_latitude, vec_longitude, myMapview);
                Track_status1 = false;
                /////
                Track_status3 = false;
                Tracking4.setlaloVec(vec_latitude,vec_longitude);
                Tracking4.setMPFVec(vec_MapPointFlag);
                Tracking4.setWcongVec(vec_PlainX, vec_PlainY);
                Track_status4 = false;
            }
            else{
                Toast.makeText(getApplicationContext(),"DrawToggle : On",Toast.LENGTH_LONG).show();
                DrawToggle=true;
                //initial polyline
                mypolyline.setTag(1000);
                mypolyline.setLineColor(Color.argb(128, 255, 51, 0));
            }
        }
    };
    //Draw_end 버튼 리스너
    ImageButton.OnClickListener DrawEndListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            drawPolyLine();
        }
    };


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



    //////////////////////////////////////////////////////////////DaumApi 추상메소드

    private void drawPolyLine(){
        if(DrawToggle) {
            myMapview.removeAllPolylines();
            myMapview.addPolyline(mypolyline);
            CalculDistOne cal = new CalculDistOne(points);
            cal.setMakedPoint();
            newpoints=cal.getMakedPoint();
            for(int i=0 ; i<newpoints.size() ; i++){
                MapCircle tmp = new MapCircle(newpoints.elementAt(i),2,Color.argb(128, 255, 0, 0),Color.argb(128, 255, 0, 0));
                tmp.setTag(circleid+i);
                myMapview.addCircle(tmp);
            }
            //////song
            DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord((double)vec_latitude.elementAt((vec_latitude.size())-1),(double)vec_longitude.elementAt((vec_latitude.size())-1));
            createDefaultMarker(myMapview);
            /////
            MapPointBounds mapPointBounds = new MapPointBounds(mypolyline.getMapPoints());
            int padding = 100; // px
            myMapview.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
        }
    }
    //GPS Track 버튼 리스너
    ImageButton.OnClickListener GpsToggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            if(GpsToggle){
                Toast.makeText(getApplicationContext(),"Gps : Off",Toast.LENGTH_LONG).show();
                GpsToggle = false;
                myMapview.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            }
            else{
                Toast.makeText(getApplicationContext(),"Gps : On",Toast.LENGTH_LONG).show();
                GpsToggle = true;
                myMapview.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            }
        }
    };
    ImageButton.OnClickListener TrackingToggleListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if(TrackToggle){
                Toast.makeText(getApplicationContext(),"Tracking : Off",Toast.LENGTH_LONG).show();
                TrackToggle=false;
            }
            else{
                if(GpsToggle) {
                    if(CalculDistOne.distanceTwoPoint(CurrentLocation,newpoints.elementAt(0))<=limitmeter) {
                        Toast.makeText(getApplicationContext(), "Tracking : On", Toast.LENGTH_LONG).show();
                        trackingindex=0;
                        TrackToggle = true;
                        Tracking1 = new TrackingMode1(vec_latitude, vec_longitude, myMapview);
                        Track_status1 = true;
                        /////
                        Track_status3= true;
                        Tracking4.setlaloVec(vec_latitude,vec_longitude);
                        Tracking4.setMPFVec(vec_MapPointFlag);
                        Tracking4.setWcongVec(vec_PlainX, vec_PlainY);
                        Track_status4 = true;
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "시작지점으로 가", Toast.LENGTH_LONG).show();
                        TrackToggle = false;
                    }
                    //////
                    Tracking1 = new TrackingMode1(vec_latitude, vec_longitude, myMapview);
                    Track_status1 = true;
                    /////
                    Tracking4.setlaloVec(vec_latitude,vec_longitude);
                    Tracking4.setMPFVec(vec_MapPointFlag);
                    Tracking4.setWcongVec(vec_PlainX, vec_PlainY);
                    Track_status4 = true;
                } else{
                    Toast.makeText(getApplicationContext(), "GpsTrack 켜", Toast.LENGTH_LONG).show();
                    TrackToggle = false;
                }
            }
            ///jungwoo
            /*
            Tracking2.setVector(vec_mapPoint, vec_latitude, vec_latitude, vec_PlainX, vec_PlainY);
            Track_status2 = true;
            Tracking3 = new TrackingMode3(cctext, vec_mapPoint);
            Track_status3 = true;
            */
            ///jungwoo
        }
    };

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {

    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        CurrentLocation = mapPoint;
        if(TrackToggle){
            double dist = CalculDistOne.distanceTwoPoint(CurrentLocation,newpoints.elementAt(trackingindex));
            if(dist > limitmeter){
                if (CalculDistOne.distanceTwoPoint(CurrentLocation, newpoints.elementAt(trackingindex + 1)) <= limitmeter) {
                    trackingindex++;
                    if(trackingindex == newpoints.size()-1) {
                        Toast.makeText(getApplicationContext(), "도착", Toast.LENGTH_LONG).show();
                        TrackToggle = false;
                    }
                    Check_Track0 = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Out Track", Toast.LENGTH_LONG).show();
                    TrackToggle = false;
                    Check_Track0 = false;
                }
            }
            if(Check_Track0)
                Check_Track_Count++;

            if(Track_status1){
                currentpoint = CurrentLocation.getMapPointGeoCoord();
                Check_Track1=Tracking1.Outcheck();
                if(Check_Track1)
                    Check_Track_Count++;
            }

            if(Track_status3){
                String a = "speed"+mySpeed;
                Log.i("here",a);
                if(mySpeed ==0)
                    Check_Track3 = false;
                else if(mySpeed > 5)
                    Check_Track3 = false;
                else
                    Check_Track3 = true;
                if(Check_Track3)
                    Check_Track_Count++;
            }

            if(Track_status4){
                Tracking4.setGpsLonLa(mapPoint.getMapPointGeoCoord().latitude,mapPoint.getMapPointGeoCoord().longitude);
                Tracking4.setGps(mapPoint.getMapPointWCONGCoord().x,mapPoint.getMapPointWCONGCoord().y);
                Tracking4.setCurrent(mapPoint);
                Check_Track4 =Tracking4.algor();
                if(Check_Track4)
                    Check_Track_Count++;
            }
        }
        if(Check_Track_Count >= 2)
            Safe = 1;
        else
            Safe = 0;
        if(Safe==0 && SendMessage == false && TrackToggle)
        {
            //sendSMS(phoneNumber,Message);
            SendMessage = true;
        }
        /*if(Track_status2) {
            Tracking2.setCurrentPosition(CurrentLocation);
            Tracking2.track();
            Check_Track2 = Tracking2.Check_Track();
            if(Check_Track2)
                Check_Track_Count++;
        }*/
        /*
        if(Track_status3){
            Check_Track3 = Tracking3.Check_Track(mapPoint, Check_count);
            Check_count++;
            if(Check_Track3)
                Check_Track_Count++;
        }
        */
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        if(DrawToggle){
            mypolyline.addPoint(mapPoint);
            MapPOIItem marker = new MapPOIItem();
            marker.setItemName("Default Marker");
            marker.setTag(0);
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            mapView.addPOIItem(marker);
            points.add(mapPoint);
            vec_mapPoint.add(mapPoint);
            vec_latitude.add(mapPoint.getMapPointGeoCoord().latitude);
            vec_longitude.add(mapPoint.getMapPointGeoCoord().longitude);
            vec_PlainX.add(mapPoint.getMapPointWCONGCoord().x);
            vec_PlainY.add(mapPoint.getMapPointWCONGCoord().y);
            /////minsu
            //MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
            //MapPoint mp = MapPoint.mapPointW
            // ithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
            //vec_latitude.add(mapPointGeo.latitude);                //찍은점 위도 위도벡터에 집어넣
            //vec_latitude.add(mapPointGeo.longitude);               //찍은점 경도 경도벡터에 집어넣
            MapPointFlag mpflag = new MapPointFlag(mapPoint);              //flag를 위해서 새로운 생성자 생성
            vec_MapPointFlag.add(mpflag);                           //생성자를 mpVec에 집어넣는다
        }
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"안심귀가", "경로즐겨찾기", "지인추가", "환경설정"};

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return com.astuetz.viewpager.extensions.sample.SuperAwesomeCardFragment.newInstance(position);
        }

    }
    public void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }
    public void createDefaultMarker(MapView mapView) {
        mDefaultMarker = new MapPOIItem();
        String name = "도착점";
        mDefaultMarker.setItemName(name);
        mDefaultMarker.setTag(0);
        mDefaultMarker.setMapPoint(DEFAULT_MARKER_POINT);
        mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(mDefaultMarker);
        mapView.selectPOIItem(mDefaultMarker, true);
        //  mapView.setMapCenterPoint(DEFAULT_MARKER_POINT, false);
    }


    private class SpeedoActionListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                mySpeed = location.getSpeed();
                if (mySpeed > maxSpeed) {
                    maxSpeed = mySpeed;
                }
            }
        }
        //요기까지
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }
        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    }
}