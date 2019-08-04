package com.maff.codingcounter.data;

import groovy.util.Factory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CodingStats {

    public int version = 1;
    public long lastEventTime;
    public Map<Period, PeriodStats> periods;

    public CodingStats(){
        this.periods = new HashMap<>();
    }

    public CodingStats(CodingStats other){
        this();

        this.version = other.version;
        this.lastEventTime = other.lastEventTime;

        for (Map.Entry<Period, PeriodStats> entry : other.periods.entrySet()) {
            this.periods.put((Period) entry.getKey(), new PeriodStats((PeriodStats) entry.getValue()));
        }

    }
}
