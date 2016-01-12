package com.job.shop.gen;

import java.util.*;

/**
 * Created by Mewa on 2015-12-30.
 */
public class Schedule {
    ArrayList<Job> up = new ArrayList<>();
    ArrayList<Job> down = new ArrayList<>();
    ArrayList<Job> toAdd = new ArrayList<>();

    public void mutate() {
        print();
        int x = new Random().nextInt(up.size());
        int y = new Random().nextInt(up.size());
        int min;
        int max;
        if (y == x) {
            if (y > 0) {
                y--;
            } else {
                y++;
            }
        }
        min = x > y ? y : x;
        max = x > y ? x : y;
        x = max - min;
        ArrayList<Task> tasks = removeTo(min);
        Task temp = tasks.get(x);
        tasks.remove(x);
        tasks.add(x, tasks.get(0));
        tasks.remove(0);
        tasks.add(0, temp);
        for(Task task : tasks){
            addTask(task);
        }
        addAllDown();
        print();
    }

    public void print(){
        for(Job job:up){
            System.out.print(job.getWhenStarts() + " " + job.getNumber() + " " + job.getWhenJobWillEnd() + "\t");
        }
        System.out.println();
        for(Job job:down){
            System.out.print(job.getWhenStarts() + " " + job.getNumber() + " " + job.getWhenJobWillEnd() + "\t");
        }
        System.out.println();
    }

    private ArrayList<Task> removeTo(int x) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = up.size() - 1; i >= x; i--) {
            Job upJobToCopy, downJobToCopy = null;
            upJobToCopy = up.get(i);
            up.remove(i);
            for (int j = down.size() - 1; j >= 0; j--) {
                if (upJobToCopy.equals(down.get(i))) {
                    downJobToCopy = down.get(i);
                    down.remove(i);
                    break;
                }
            }
            tasks.add(new Task(upJobToCopy, downJobToCopy));
        }
        Collections.reverse(tasks);
        return tasks;
    }

    public Schedule generuj() {
        ArrayList<Task> copyTasks = new ArrayList<>();
        for (Task task : Main.originalTasks) {
            copyTasks.add(new Task(task));
        }
        while (copyTasks.size() > 0) {
            int random = new Random().nextInt(copyTasks.size());
            addTask(copyTasks.get(random));
            copyTasks.remove(random);
        }
        addAllDown();
        return this;
    }

    private void addTask(Task task) {
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


    private void addTaskUp(Task task, int beginning) {
        task.getUp().setWhenStarts(beginning);
        task.setDownPossibleStart();
        toAdd.add(task.getDown());
        up.add(task.getUp());
    }

    private void addAllDown() {

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
                    down.add(i + 1, job);
                    added = true;
                    break;
                }
            }
            if (down.size() == 0) {
                if (Main.nextBreak(job.getWhenCouldStarts()) - job.getWhenCouldStarts() - 1 > job.getTime()) {
                    job.setWhenStarts(job.getWhenCouldStarts());
                    added = true;
                    down.add(job);
                }
            }
            if (!added) {
                if (Main.nextBreak(down.get(down.size() - 1).getWhenJobWillEnd()) - down.get(down.size() - 1).getWhenJobWillEnd() - 1 >= job.getTime() &&
                        Main.nextBreak(down.get(down.size() - 1).getWhenJobWillEnd()) - job.getWhenCouldStarts() - 1 >= job.getTime()) {
                    job.setWhenStarts(job.getWhenCouldStarts() > down.get(down.size() - 1).getWhenJobWillEnd() + 1 ?
                            job.getWhenCouldStarts() : down.get(down.size() - 1).getWhenJobWillEnd() + 1);
                } else {
                    job.setWhenStarts(Main.nextBreak(down.get(down.size() - 1).getWhenJobWillEnd()) > job.getWhenCouldStarts() ?
                            Main.nextBreak(down.get(down.size() - 1).getWhenJobWillEnd()) : job.getWhenCouldStarts());
                }
                down.add(job);
            }

            toAdd.remove(0);
        }
    }
}
