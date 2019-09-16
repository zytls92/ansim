package com.astuetz.viewpager.extensions.sample;

import net.daum.mf.map.api.MapPoint;

import java.util.Vector;

public class TrackingMode4 {
    private double firstDis;  //첫번째 점 flag on을 위해서 처음찍은점과 현재 gps간의 거리
    private boolean start = false; // 첫번째 flag온시 start가 true가 된다.
    private double h, r;       //h는 현재 gps에서 polyline까지의 수선의발 ,r은 현재 gps에서 꺽인점 사이의 거리
    private double ocha = 120; // r과 h의 오차인데 이부분을 우리가 test하면서 실험해야한다.
    private double gpsX,gpsY;
    private double gpsla,gpslo;
    private Vector<Double> LoVec=new Vector<>();
    private Vector<Double> LaVec=new Vector<>();
    private Vector<Double> WcongVec_x=new Vector<>();
    private Vector<Double> WcongVec_y=new Vector<>();
    private Vector <MapPointFlag> mpFlagVector = new Vector<>();
    private int starti=-1;
    private MapPoint currentPoint;

    public static double distanceTwoPoint(MapPoint a, MapPoint b){
        double distance;
        double R=6378.137; // Radius of earth in KM
        double lat1,lon1,lat2,lon2;
        double dLat,dLon;

        lat1 = a.getMapPointGeoCoord().latitude;
        lon1 = a.getMapPointGeoCoord().longitude;

        lat2 = b.getMapPointGeoCoord().latitude;
        lon2 = b.getMapPointGeoCoord().longitude;

        dLat= lat2 * Math.PI / 180 - lat1 * Math.PI/180;
        dLon= lon2 * Math.PI / 180 - lon1 * Math.PI/180;

        double tmp = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double temp = 2 * Math.atan2(Math.sqrt(tmp), Math.sqrt(1-tmp));

        distance = R * temp;
        return distance * 1000;
    }

    public Boolean algor() {

        if(LaVec.size()>1) {
            firstDis = distanceTwoPoint(currentPoint, mpFlagVector.elementAt(0).mp);
            //처음 mappoint와 gps사이의 거리
            if (firstDis < 10 && WcongVec_x.size() > 1 && start == false) { //처음 거리가 10m미만이고 start가 false이면서 점이 2개이상 찍혔다면
                mpFlagVector.elementAt(0).fla = true;            //처음 mappoint의 flag를 true처리하고
                start = true;
                starti++;//start를 true로한다
            }
        }
        if (mpFlagVector.size() > 2 && start && starti < mpFlagVector.size()-1) {
            h = calcu();
            double gps_x = gpsX;
            double gps_y = gpsY;
            double point_x = mpFlagVector.elementAt(starti + 1).mp.getMapPointWCONGCoord().x;
            double point_y = mpFlagVector.elementAt(starti + 1).mp.getMapPointWCONGCoord().y;
            r = (point_x - gps_x) * (point_x - gps_x) + (point_y - gps_y) * (point_y - gps_y);
            if (r - ocha < h) {                                                 //거리와 수선의발 의 차이가 어느정도 좁혀지면 starti를 ++해서 다음 polyline과 비교한다.
                mpFlagVector.elementAt(starti + 1).fla = true;                 //해당 mappoint fla를 true로 한다.
                starti++;
            }
            else {
                return false;
            }
        }
        return true;
    }
    public double calcu() {
        double ans = 0;
        if (WcongVec_x.size() >= 2) {
            double y0 = mpFlagVector.elementAt(starti).mp.getMapPointWCONGCoord().y;
            double y1 = mpFlagVector.elementAt(starti + 1).mp.getMapPointWCONGCoord().y;
            double y2 = gpsY;
            double x0 = mpFlagVector.elementAt(starti).mp.getMapPointWCONGCoord().x;
            double x1 = mpFlagVector.elementAt(starti + 1).mp.getMapPointWCONGCoord().x;
            double x2 = gpsX;
            double up = ((y0 - y1) * x2) + ((x1 - x0) * y2) + (x0 * y1) - (x1 * y0);
            up *= up;
            double down = ((y0 - y1) * (y0 - y1)) + ((x1 - x0) * (x1 - x0));
            ans = up / down;
        }
        return ans;
    }

    public void setWcongVec(Vector x,Vector y){
        WcongVec_x = x;
        WcongVec_y = y;
    }
    public void setGps(double x,double y){
        gpsX=x;
        gpsY=y;
    }
    public void setMPFVec(Vector mpVec){
        mpFlagVector=mpVec;
    }
    public void setGpsLonLa(double la,double lo){
        gpsla=la;
        gpslo=lo;
    }
    public void setlaloVec(Vector la,Vector lo){
        LaVec=la;
        LoVec=lo;
    }

    public void setCurrent(MapPoint mp){
        this.currentPoint = mp;
    }
}