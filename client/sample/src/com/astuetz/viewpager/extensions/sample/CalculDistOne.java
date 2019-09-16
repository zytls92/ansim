package com.astuetz.viewpager.extensions.sample;

import net.daum.mf.map.api.MapPoint;

import java.util.Vector;
import java.lang.Math;

public class CalculDistOne {
    private Vector<MapPoint> points;
    private Vector<MapPoint> makedPoint;
    public static final double ALLOWED_DISTANCE = 20;

    public CalculDistOne (Vector<MapPoint> pts){
        this.points = pts;
        makedPoint = new Vector<>();
    }

    public Vector<MapPoint> getMakedPoint() {
        return makedPoint;
    }

    public void setMakedPoint() {
        for(int i=0;i<points.size()-1;i++){
            fillBetween(points.elementAt(i),points.elementAt(i+1));
        }
        makedPoint.add(points.elementAt(points.size()-1));
    }

    // MapPoint 간의 거리
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


    private void fillBetween(MapPoint a, MapPoint b){
        double d=distanceTwoPoint(a,b);
        double lat1,lon1,lat2,lon2;

        lat1 = a.getMapPointGeoCoord().latitude;
        lon1 = a.getMapPointGeoCoord().longitude;
        lat2 = b.getMapPointGeoCoord().latitude;
        lon2 = b.getMapPointGeoCoord().longitude;

        MapPoint mid = MapPoint.mapPointWithGeoCoord((lat1+lat2)/2,(lon1+lon2)/2);

        if(d > ALLOWED_DISTANCE){
            fillBetween(a,mid);
            fillBetween(mid,b);
        }
        else{
            makedPoint.add(a);
        }
    }
}