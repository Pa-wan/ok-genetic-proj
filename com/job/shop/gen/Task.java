package com.job.shop.gen;

import java.util.Random;

/**
 * Created by Mewa on 2015-12-30.
 */
public class Task {
    private Job up = new Job();
    private Job down = new Job();

    public Job getUp() {
        return up;
    }


    public Job getDown() {
        return down;
    }

    public void setDownPossibleStart() {
        getDown().setWhenCouldStarts(up.getWhenJobWillEnd() + 1);
    }

    public Task() {
    }

    public Task(Task task) {
        Job down = task.getDown();
        Job up = task.getUp();
        this.down = new Job(down.getWhenJobWillEnd(), down.getNumber(), down.getWhenCouldStarts(),down.getTime());
        this.up = new Job(up.getWhenJobWillEnd(), up.getNumber(), up.getWhenCouldStarts(), up.getTime());
    }
    public Task(Job up, Job down) {
        this.down = new Job(down.getWhenJobWillEnd(), down.getNumber(), down.getWhenCouldStarts(), down.getTime());
        this.up = new Job(up.getWhenJobWillEnd(), up.getNumber(), up.getWhenCouldStarts(), up.getTime());
    }

    public boolean equals(Task obj) {
        return up.getNumber() == obj.getUp().getNumber();
    }
}
