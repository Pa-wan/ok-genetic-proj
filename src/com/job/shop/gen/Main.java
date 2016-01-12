package com.job.shop.gen;

import java.util.*;

/**
 * Created by Mewa on 2015-12-30.
 */
public class Main {
    protected static final int kPopulationSize = 50;
    public static final int MAX_JOB_TIME = 120;
    public static final int BREAKS = 14;
    public static final int TASKS_NUMBER = 110;
    public static final int HOW_LONG = 10;
    public static long time;
    public static ArrayList<Task> originalTasks = new ArrayList<>();
    public static int minTime = 999999999;
    public static ArrayList<Integer> breaks = new ArrayList<>();
    public static ArrayList<Schedule> schedules = new ArrayList<>();

    static ArrayList<Schedule> generuj() {
        ArrayList<Schedule> list = new ArrayList<>();
        for (int i = 0; i < kPopulationSize; i++) {
            list.add(new Schedule().generuj());
        }
        return list;
    }

    public static int nextBreakUp(int time) {
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

    public static int nextBreakDown(int time) {
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
        System.out.println("start");
        before();
        ArrayList<Schedule> subpopulation = new ArrayList<>();
        while (shouldProceed()) {
            while (subpopulation.size() < 8 * kPopulationSize) {
                int x = new Random().nextInt(schedules.size());
                int y = new Random().nextInt(schedules.size());
                if (y == x) {
                    if (y > 0) {
                        y--;
                    } else {
                        y++;
                    }
                }


                subpopulation.add(schedules.get(x).crossing(schedules.get(y)));
                subpopulation.add(schedules.get(y).crossing(schedules.get(x)));
                subpopulation.add(schedules.get(y).crossingReverse(schedules.get(x)));
                subpopulation.add(schedules.get(y).crossingReverse(schedules.get(x)));
                Schedule schedule1 = new Schedule(schedules.get(x));
                Schedule schedule2 = new Schedule(schedules.get(y));
                schedule1.mutate();
                schedule2.mutate();
                subpopulation.add(schedule1);
                subpopulation.add(schedule2);
            }
            subpopulation.addAll(schedules);

            Collections.sort(subpopulation, new Comparator<Schedule>() {
                @Override
                public int compare(Schedule o1, Schedule o2) {
                    if (o1.getTime() > o2.getTime()) {
                        return 1;
                    }
                    if (o1.getTime() < o2.getTime()) {
                        return -1;
                    }
                    return 0;
                }
            });
            System.out.println("posortowane");
            schedules.clear();
            for (int i = 0; i < 5; i++) {
                schedules.add(subpopulation.get(0));
                subpopulation.remove(0);
            }
            for (int i = 0; i < kPopulationSize - 5; i++) {
                double where = Math.log10(subpopulation.size());
                int which = (int) Math.floor(Math.pow(Math.random() * where, 10));
                if (which > subpopulation.size() - 1) {
                    which = subpopulation.size() - 1;
                }
                schedules.add(subpopulation.get(which));
                subpopulation.remove(which);
            }
            subpopulation.clear();
        }
        System.out.println("exit");

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
        schedules = generuj();
        time = Calendar.getInstance().getTimeInMillis();
    }

    static boolean shouldProceed() {
        return time + (HOW_LONG * 1000) > Calendar.getInstance().getTimeInMillis();
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
