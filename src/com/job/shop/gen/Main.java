package com.job.shop.gen;

import java.io.*;
import java.util.*;

/**
 * Created by Mewa on 2015-12-30.
 */
public class Main {
    private static final String GEN = "gen";
    private static String GEN_FILENAME;
    private static int MAX_ITERATIONS = -1;
    protected static int kPopulationSize = 10;
    public static int MAX_JOB_TIME = 140;
    public static int BREAKS = 13;
    public static final int TASKS_NUMBER = 120;
    public static int HOW_LONG = 10;
    private static String INSTANCE_FILENAME;
    public static long time;
    public static ArrayList<Task> originalTasks = new ArrayList<>();
    public static int minTime = 999999999;
    public static ArrayList<Integer> breaksBegin = new ArrayList<>();
    public static ArrayList<Integer> breaksEnd = new ArrayList<>();
    public static ArrayList<Schedule> schedules = new ArrayList<>();
    private static int numPasses = 0;

    public static void main(String[] args) {
        if (args.length > 0) {
            System.out.println("start");
            kPopulationSize = Integer.valueOf(args[0]);
            HOW_LONG = Integer.valueOf(args[1]);
            if (args.length > 4) {
                MAX_ITERATIONS = HOW_LONG;
                HOW_LONG = -1;
                System.out.println("Launching " + MAX_ITERATIONS + " iterations");
            }
            BREAKS = Integer.valueOf(args[2]);
            MAX_JOB_TIME = Integer.valueOf(args[3]);
        }
        GEN_FILENAME = GEN + MAX_JOB_TIME + ".txt";
        INSTANCE_FILENAME = String.format(
                "inst%d_maxTjob%d_breaks%d_tasks%d_maxT%d_ITER%d",
                kPopulationSize,
                MAX_JOB_TIME,
                BREAKS,
                TASKS_NUMBER,
                HOW_LONG,
                MAX_ITERATIONS
        );
        before();
        long start = System.currentTimeMillis();
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
            double best = subpopulation.get(0).getTime();
            for (int i = 0; i < Math.min(5, Math.max(0.1 * kPopulationSize, 1)); i++) {
                schedules.add(subpopulation.get(0));
                subpopulation.remove(0);
            }
            Schedule tmp;
            while (schedules.size() < kPopulationSize) {
                int pos = (int) (Math.random() * (subpopulation.size() - 1));
                tmp = subpopulation.get(pos);
                double prob = best / tmp.getTime();
                prob *= prob;
                if (Math.random() < prob) {
                    schedules.add(tmp);
                    subpopulation.remove(tmp);
                }
            }
            subpopulation.clear();
        }
        long end = System.currentTimeMillis();
        int min = schedules.get(0).getTime();
        for (Schedule schedule : schedules) {
            if (schedule.getTime() < min) {
                min = schedule.getTime();
            }
        }
        System.out.println(String.format("Passed %d times. Min: %d", numPasses, min));
        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(INSTANCE_FILENAME + "results.txt"), true);
            writer
                    .append(min + " ")
                    .append(end - start + "")
                    .append(System.lineSeparator());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(INSTANCE_FILENAME + "result_instance.txt");
            PrintStream printStream = new PrintStream(fos);
            for (Schedule schedule : schedules) {
                schedule.printResult(printStream);
            }
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
    }

    static void laduj() {
        BufferedReader reader = null;
        try {
            File file = new File(GEN_FILENAME);
            reader = new BufferedReader(new FileReader(file));
            String line1;
            Job up;
            Job down;
            List<Task> tasks = new ArrayList<>();
            while ((line1 = reader.readLine()) != null) {
                String[] args = line1.split(" ");
                up = new Job();
                down = new Job();

                int time = Integer.valueOf(args[0]);
                up.setTime(time);
                time = Integer.valueOf(args[1]);
                down.setTime(time);

                Task task = new Task(up, down);
                tasks.add(task);
            }
            if (tasks.size() > 0) {
                originalTasks.clear();
                originalTasks.addAll(tasks);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
    }

    static ArrayList<Schedule> generuj() {
        FileOutputStream fos = null;
        ArrayList<Schedule> list = null;
        try {
            fos = new FileOutputStream(GEN_FILENAME);
            PrintStream printStream = new PrintStream(fos);
            list = new ArrayList<>();
            for (int i = 0; i < kPopulationSize; i++) {
                Schedule schedule = new Schedule().generuj();
                list.add(schedule);
            }
            for (Task originalTask : Main.originalTasks) {
                printStream
                        .append(originalTask.getUp().getTime() + " ")
                        .append(originalTask.getDown().getTime() + "")
                        .append(System.lineSeparator());
            }
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
        return list;
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

    private static void before() {
        for (int i = 0; i < TASKS_NUMBER; i++) {
            originalTasks.add(new Task());
        }
        laduj();
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
        schedules = generuj();
        time = Calendar.getInstance().getTimeInMillis();
    }

    static boolean shouldProceed() {
        numPasses++;
        return HOW_LONG > 0
                ? time + (HOW_LONG * 1000) > Calendar.getInstance().getTimeInMillis()
                : numPasses < MAX_ITERATIONS;
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
