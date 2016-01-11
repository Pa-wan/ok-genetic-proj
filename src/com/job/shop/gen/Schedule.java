package com.job.shop.gen;

import java.util.*;

/**
 * Created by Mewa on 2015-12-30.
 */
public class Schedule {
    ArrayList<Job> up = new ArrayList<>();
    ArrayList<Job> down = new ArrayList<>();
    ArrayList<Job> toAdd = new ArrayList<>();

    public void addTask(Task task) {
        if (up.size() == 0) {
            addTaskUp(task, 1);
        } else {
            int whenCanIStart = up.get(up.size() - 1).getWhenJobWillEnd() + 1;
            int przerwa = Main.nextBreak(whenCanIStart);
            if (whenCanIStart < przerwa && whenCanIStart + task.getUp().getTime() < przerwa) {
                addTaskUp(task, whenCanIStart);
            } else {
                addTaskUp(task, przerwa);
            }
        }
    }


    public void addTaskUp(Task task, int beginning) {
        task.getUp().setWhenStarts(beginning);
        task.setDownPossibleStart();
        toAdd.add(task.getDown());
        up.add(task.getUp());
    }

    public void addAllDown() {

        while (toAdd.size() > 0) {
            boolean added = false;
            Job job = toAdd.get(0);
            for (int i = 0; i < down.size() - 1; i++) {
                if (down.get(i + 1).getWhenStarts() - down.get(i).getWhenJobWillEnd() - 1 >= job.getTime() &&
                        Main.nextBreak(down.get(i).getWhenJobWillEnd()) - down.get(i).getWhenJobWillEnd() - 1 >= job.getTime() &&
                        down.get(i + 1).getWhenStarts() - job.getWhenCouldStarts() - 1 >= job.getTime() &&
                        Main.nextBreak(job.getWhenCouldStarts()) - job.getWhenCouldStarts() - 1 >= job.getTime()) {
                    job.setWhenStarts(job.getWhenCouldStarts() > down.get(i).getWhenJobWillEnd() + 1 ?
                            job.getWhenCouldStarts() : down.get(i).getWhenJobWillEnd() + 1);
                    down.add(i+1, job);
                    added = true;
                    break;
                }
            }
            if(down.size()==0){
                if(Main.nextBreak(job.getWhenCouldStarts())-job.getWhenCouldStarts()-1 > job.getTime()){
                    job.setWhenStarts(job.getWhenCouldStarts());
                    added=true;
                    down.add(job);
                }
            }
            if (!added) {
                if (Main.nextBreak(down.get(down.size() - 1).getWhenJobWillEnd()) - down.get(down.size() - 1).getWhenJobWillEnd() - 1 >= job.getTime() &&
                        Main.nextBreak(down.get(down.size() - 1).getWhenJobWillEnd()) - job.getWhenCouldStarts() - 1 >= job.getTime()) {
                    job.setWhenStarts(job.getWhenCouldStarts() > down.get(down.size() - 1).getWhenJobWillEnd() + 1 ?
                            job.getWhenCouldStarts() : down.get(down.size() - 1).getWhenJobWillEnd() + 1);
                } else {
                    job.setWhenStarts(Main.nextBreak(down.get(down.size() - 1).getWhenJobWillEnd()) > job.getWhenCouldStarts()?
                            Main.nextBreak(down.get(down.size() - 1).getWhenJobWillEnd()) : job.getWhenCouldStarts());
                }
                down.add(job);
            }

            toAdd.remove(0);
        }
    }
     }
