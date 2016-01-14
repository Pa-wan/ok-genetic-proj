package com.job.shop.gen;

import java.util.Random;

/**
 * Created by Maciej on 11.01.2016.
 */
public class Job {
    private int whenJobWillEnd;
    private int time = new Random().nextInt(Main.MAX_JOB_TIME) + 1;
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

    public void setWhenStarts(int whenStarts) {
        this.whenJobWillEnd = whenStarts + this.time - 1;
    }

    public int getWhenStarts() {
        return whenJobWillEnd - time + 1;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean equals(Job obj) {
        return obj.getNumber() == number;
    }

    public Job() {
    }

    public Job(int whenJobWillEnd, int number, int whenCouldStarts, int time) {
        this.whenJobWillEnd = whenJobWillEnd;
        this.number = number;
        this.whenCouldStarts = whenCouldStarts;
        this.time = time;
    }

    public Job(Job job) {
        this.whenJobWillEnd = job.whenJobWillEnd;
        this.number = job.number;
        this.whenCouldStarts = job.whenCouldStarts;
        this.time = job.time;
    }

    @Override
    public String toString() {
        return String.format("Job%d(%d)", number, time);
    }
}
