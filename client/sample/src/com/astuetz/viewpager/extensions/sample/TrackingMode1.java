package com.astuetz.viewpager.extensions.sample;

import android.graphics.Color;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Vector;

public class TrackingMode1 extends MainActivity {

    //private Vector<point> vector_create_region_x = new Vector<>();
    //private Vector<point> vector_create_region_y = new Vector<>();
    private point first_point;
    private point last_point;
    private Vector<Double> vec_latitude;
    private Vector<Double> vec_longtitudel;
    private int size;
    private Vector<point> transaction = new Vector<point>();
    private point current_point= new point(0, 0);// 현재좌표에따라 실시간으로 값이 바뀐다.
    private MapView mapView;
    MapCircle[] circle;

    public TrackingMode1(Vector tmp_latitude, Vector tmp_longtitude, MapView tmp_mapView) {
        mapView = tmp_mapView;
        this.size = tmp_latitude.size();


        for(int i=0; i<size-1; i++) {
            first_point = new point((double)tmp_latitude.get(i), (double)tmp_longtitude.get(i));
            last_point = new point((double)tmp_latitude.get(i+1), (double)tmp_longtitude.get(i+1));

            transaction.add(first_point);//첫점안찍혀있어서 첫점 찍어준다.
            Fill_transaction(first_point, last_point);//두점간의 점들을 찍는다
            circle = new MapCircle[transaction.size()];
            Marking(mapView);
        }
    }

    ////////////////////////////////////////////////////////////실시간 좌표랑 Transaction비교 나갔나 안나갔나확인
    public Boolean Outcheck() {
        current_point.x = currentpoint.latitude;
        current_point.y = currentpoint.longitude;
        //  System.out.printf("현재좌표 x: %f, y:%f \n",currentpoint.latitude,currentpoint.longitude);
        int count = -1;

        for (int i = 0; i < transaction.size(); i++) {

            //Out Circle
            if (current_Cal_distance(current_point.x,current_point.y, transaction.elementAt(i)) < 100) {
                count = i;
                break;
            }
        }

        if (count != -1) {
            mapView.removeAllCircles();
            for (int i = 0; i < transaction.size(); i++) {
                circle[i].setFillColor(Color.argb(128, 0, 255, 0));
                mapView.addCircle(circle[i]);
            }
            return true;
        }
        else
        {
            mapView.removeAllCircles();
            for (int i = 0; i < transaction.size(); i++) {
                circle[i].setFillColor(Color.argb(128, 255, 0, 0));
                mapView.addCircle(circle[i]);
            }
            return false;
        }


    }
    ///////////////////////////////////////////////////////////두 좌표 거리 계산
    public double current_Cal_distance(double x, double y, point last_point) {
        //   point temp = new point((first_point.x+last_point.x)/2, (first_point.y+last_point.y)/2) ;
        double distance;
        double R = 6378.137; // Radius of earth in KM

        double dLat, dLon;
        dLat = last_point.x * Math.PI / 180 - x * Math.PI / 180;
        dLon = last_point.y * Math.PI / 180 - y * Math.PI / 180;
        double tmp = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(x * Math.PI / 180) * Math.cos(last_point.x * Math.PI / 180) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double ttmp = 2 * Math.atan2(Math.sqrt(tmp), Math.sqrt(1 - tmp));
        distance = R * ttmp;
        return distance * 1000;

    }
    public double Cal_distance(point first_point, point last_point) {
        //   point temp = new point((first_point.x+last_point.x)/2, (first_point.y+last_point.y)/2) ;
        double distance;
        double R = 6378.137; // Radius of earth in KM

        double dLat, dLon;
        dLat = last_point.x * Math.PI / 180 - first_point.x * Math.PI / 180;
        dLon = last_point.y * Math.PI / 180 - first_point.y * Math.PI / 180;
        double tmp = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(first_point.x * Math.PI / 180) * Math.cos(last_point.x * Math.PI / 180) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double ttmp = 2 * Math.atan2(Math.sqrt(tmp), Math.sqrt(1 - tmp));
        distance = R * ttmp;
        return distance * 1000;

    }
    ///////////////////////////////////////////////////////////singleTap 시작, 도착점 사이 Transaction 채우기
    public void Fill_transaction(point a, point b) {
        if ((Cal_distance(a, b)) > 200) {
            point temp = new point((a.x + b.x) / 2, (a.y + b.y) / 2);

            Fill_transaction(a, temp);
            Fill_transaction(temp, b);
        } else {
            transaction.add(b);

        }
    }
    ///////////////////////////////////////////////////////////Transaction 맵에다 원표시
    public void Marking(MapView mapView) {


        for (int i = 0; i < transaction.size(); i++) {
            //System.out.printf("bbbbb %f, %f\n", transaction.elementAt(i).x,transaction.elementAt(i).y);
            circle[i] = new MapCircle(
                    MapPoint.mapPointWithGeoCoord(transaction.elementAt(i).x, transaction.elementAt(i).y), // center
                    50, // radius
                    Color.argb(128, 255, 0, 0), // strokeColor
                    Color.argb(128, 0, 255, 0) // fillColor
            );
            mapView.addCircle(circle[i]);
        }
    }

    //////////////////////x,y하나로 받는 클래스
    public class point{
        private double x;
        private double y;
        private boolean current_state_safe_checked;
        public point(double x, double y)
        {
            this.x=x;
            this.y=y;
            current_state_safe_checked=false;
        }



    } // 겟셋메소드 없어도 접근 잘만되네? 질문
}








//double incline = (last_point.getY()-first_point.getY()) / (last_point.getX()-first_point.getX());
//for (int i=0; i<vector_longitude.size(); i++){
//  double temp = (last_x-first_x)/vector_longitude.size();
//vector_create_region_x.add(first_x+temp*i);
//vector_create_region_y.add(first_y+temp*i);
