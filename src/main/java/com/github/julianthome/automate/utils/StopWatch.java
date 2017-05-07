package com.github.julianthome.automate.utils;

public class StopWatch {
    long start;
    long end;
    long time;
    long overallTime = 0;

    public static StopWatch get() {
        return new StopWatch();
    }

    private StopWatch() { start = 0; end = 0; time = 0; overallTime = 0;}

    public void start() {
        this.start = System.currentTimeMillis();
    }

    public long stop() {
        this.end = System.currentTimeMillis();

        if(this.end > this.start) {
            this.time = this.end - this.start;
        } else {
            this.time = 0L;
        }
        this.end = 0; this.start = 0;
        this.overallTime += time;
        return time;
    }

    public long getTime() {
        return this.time;
    }

    public long getOverallTime() {
        return this.overallTime;
    }

}