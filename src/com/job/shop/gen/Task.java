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
        getDown().setWhenCouldStarts(up.getWhenJobWillEnd()+1);
    }
}
