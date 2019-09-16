package com.astuetz.viewpager.extensions.sample;

import net.daum.mf.map.api.MapPoint;

public class MapPointFlag {
    public MapPoint mp;
    public boolean fla;
    public int i=0;
    public MapPointFlag(MapPoint mp){
        this.fla=false;
        this.mp=mp;
        i++;
    }

}