package com.job.shop.gen;

import java.io.*;
import java.util.*;

/**
 * Created by Mewa on 2015-12-30.
 */
public class Main {
    protected static final int kPopulationSize = 50;
    public static final int MAX_JOB_TIME = 140;
    public static final int BREAKS = 13;
    public static final int TASKS_NUMBER = 120;
    public static final int HOW_LONG = 10;
    private static final String INSTANCE_FILENAME = String.format(
            "inst%d_maxTjob%d_breaks%d_tasks%d_maxT%d",
            kPopulationSize,
            MAX_JOB_TIME,
            BREAKS,
            TASKS_NUMBER,
            HOW_LONG
    );
    public static long time;
    public static ArrayList<Task> originalTasks = new ArrayList<>();
    public static int minTime = 999999999;
    public static ArrayList<Integer> breaksBegin = new ArrayList<>();
    public static ArrayList<Integer> breaksEnd = new ArrayList<>();
    public static ArrayList<Schedule> schedules = new ArrayList<>();

    static ArrayList<Schedule> laduj() {
        ArrayList<Schedule> loaded = new ArrayList<>();
        BufferedReader reader = null;
        try {
            File file = new File(INSTANCE_FILENAME + ".txt");
            reader = new BufferedReader(new FileReader(file));
            String line1, line2;
            Job up = null;
            Job down = null;
            while ((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null) {
                Schedule schedule = new Schedule();
                String[] args1 = line1.split("\t");
                String[] args2 = line1.split("\t");
                for (int i = 0; i < args1.length; ++i) {
                    String[] params1 = args1[i].split(":");
                    String[] params2 = args2[i].split(":");
                    for (int j = 0; j < params1.length; ++j) {
                        int number = Integer.valueOf(params1[0]);
                        int time = Integer.valueOf(params1[1]);
                        int whenStarts = Integer.valueOf(params1[2]);
                        up = new Job();
                        up.setNumber(number);
                        up.setTime(time);
                        up.setWhenStarts(whenStarts);
                        number = Integer.valueOf(params2[0]);
                        time = Integer.valueOf(params2[1]);
                        whenStarts = Integer.valueOf(params2[2]);
                        down = new Job();
                        down.setNumber(number);
                        down.setTime(time);
                        down.setWhenStarts(whenStarts);
                    }
                    Task task = new Task(up, down);
                    schedule.addTask(task);
                }
                schedule.addAllDown();
                loaded.add(schedule);
            }
            return loaded;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return generuj();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    static ArrayList<Schedule> generuj() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(INSTANCE_FILENAME + ".txt");
            PrintStream printStream = new PrintStream(fos);
            ArrayList<Schedule> list = new ArrayList<>();
            for (int i = 0; i < kPopulationSize; i++) {
                Schedule schedule = new Schedule().generuj();
                schedule.print(printStream);
                list.add(schedule);
            }
            return list;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static int nextBreakUp(int time) {
        for (int i = 1; i < breaksBegin.size(); i++) {
            if (time < breaksBegin.get(i)) {
                if (time < breaksBegin.get(i - 1)) {
                    return breaksBegin.get(i - 1);
                } else {
                    return breaksBegin.get(i);
                }
            }
        }
        return minTime * 2;
    }

    public static int timeOfNextBreakUpEnd(int time) {
        for (int i = 1; i < breaksBegin.size(); i++) {
            if (time < breaksBegin.get(i)) {
                if (time < breaksBegin.get(i - 1)) {
                    return breaksEnd.get(i - 1);
                } else {
                    return breaksEnd.get(i);
                }
            }
        }
        return minTime * 2;
    }

    public static int nextBreakDown(int time) {
        return minTime * 2;
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
        int min = schedules.get(0).getTime();
        for (Schedule schedule : schedules) {
            if (schedule.getTime() < min) {
                min = schedule.getTime();
            }
        }
        System.out.println("min time: " + min);
        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(INSTANCE_FILENAME + "results.txt"), true);
            writer.append(schedules.toString()).append(System.lineSeparator());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
            minTime += 10 * BREAKS;
        }
        for (i = minTime / BREAKS; i <= minTime; i += minTime / BREAKS) {
            breaksBegin.add(i);
            breaksEnd.add(i + 10);
        }
        schedules = laduj();
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
