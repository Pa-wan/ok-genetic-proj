package com.job.shop.gen;

import java.util.Random;

/**
 * Created by Maciej on 11.01.2016.
 */
public class Job {
    private int whenJobWillEnd;
    final private int time = new Random().nextInt(Main.MAX_JOB_TIME) + 1;
    private int number;
    private int whenCouldStarts;

    public int getWhenCouldStarts() {
        return whenCouldStarts;
    }

    public void setWhenCouldStarts(int whenCouldStarts) {
        this.whenCouldStarts = whenCouldStarts;
    }

    public int getTime() {
        return time;
    }

    public int getWhenJobWillEnd() {
        return whenJobWillEnd;
    }

    public void setWhenJobWillEnd(int whenJobWillEnd) {
        this.whenJobWillEnd = whenJobWillEnd;
    }

    public void setWhenStarts(int whenStarts){
        this.whenJobWillEnd = whenStarts + this.time - 1;
    }

    public int getWhenStarts(){
        return whenJobWillEnd - time + 1;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
