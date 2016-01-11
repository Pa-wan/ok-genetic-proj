package com.job.shop.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Mewa on 2015-12-30.
 */
public class Main {
    protected static final int kPopulationSize = 50;
    public static final int MAX_JOB_TIME = 20;
    public static final int BREAKS = 2;
    public static final int TASKS_NUMBER = 10;
    public static ArrayList<Task> originalTasks = new ArrayList<>();
    public static int minTime = 999999999;
    public static ArrayList<Integer> breaks = new ArrayList<>();
    public static ArrayList<Schedule> schedules = new ArrayList<>();

    static ArrayList<Schedule> generuj() {
        ArrayList<Schedule> list = new ArrayList<>();
        for (int i = 0; i < kPopulationSize; i++) {
            ArrayList<Task> copyTasks = new ArrayList<>();
            copyTasks.addAll(originalTasks);
            Schedule schedule = new Schedule();
            while (copyTasks.size() > 0) {
                int random = new Random().nextInt(copyTasks.size());
                schedule.addTask(copyTasks.get(random));
                copyTasks.remove(random);
            }
            schedule.addAllDown();
            list.add(schedule);
        }
        return list;
    }

    public static int nextBreak(int time) {
        for (int i = 1; i < breaks.size(); i++) {
            if (time < breaks.get(i)) {
                if (time < breaks.get(i - 1)) {
                    return breaks.get(i - 1);
                } else {
                    return breaks.get(i);
                }
            }
        }
        return minTime * 2;
    }

    static Schedule cross(Schedule s1, Schedule s2) {
        return null;
    }

    static Schedule mutate(Schedule schedule) {
        return null;
    }

    static List<Schedule> select(List<Schedule> schedules) {
        return null;
    }

    public static void main(String[] args) {

        before();
        System.out.println("Start");
        schedules = generuj();
        System.out.println("exit");

        /*List<Schedule> population = generuj();
        List<Schedule> subpopulation;
        boolean ready = false;

        while (shouldProceed()) {
            subpopulation = select(population);
            population.clear();
            while (population.size() < 5 * kPopulationSize) {
                Schedule[] parents = getParents(subpopulation);
                Schedule schedule1 = parents[0];
                Schedule schedule2 = parents[1];
                if (shouldCross()) {
                    schedule1 = cross(parents[0], parents[1]);
                    schedule2 = cross(parents[1], parents[0]);
                }
                if (shouldMutate()) {
                    schedule1 = mutate(schedule1);
                    schedule2 = mutate(schedule2);
                }
                population.add(schedule1);
                population.add(schedule2);
            }
        }

        System.out.println("hehe");*/

    }

    private static void before() {
        for (int i = 0; i < TASKS_NUMBER; i++) {
            originalTasks.add(new Task());
        }
        int timeUp = 0;
        int timeDown = 0;
        int i = 0;
        for (Task task : originalTasks) {
            timeUp += task.getUp().getTime();
            timeDown += task.getDown().getTime();
            task.getUp().setNumber(i);
            task.getDown().setNumber(i);
            ++i;
        }
        if (timeDown > timeUp) {
            minTime = timeDown;
        } else {
            minTime = timeUp;
        }
        for (i = minTime / BREAKS; i <= minTime; i += minTime / BREAKS) {
            breaks.add(i);
        }
    }

    static boolean shouldProceed() {
        return false;
    }

    static Schedule[] getParents(List<Schedule> subpopulation) {
        return new Schedule[0];
    }

    static boolean shouldCross() {
        return false;
    }

    static boolean shouldMutate() {
        return false;
    }
}
