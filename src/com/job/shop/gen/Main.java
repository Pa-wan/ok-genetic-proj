package com.job.shop.gen;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by Mewa on 2015-12-30.
 */
public abstract class Main {
    protected final int kPopulationSize = 50;

    abstract List<Schedule> generuj();

    abstract Schedule cross(Schedule s1, Schedule s2);

    abstract Schedule mutate(Schedule schedule);

    abstract List<Schedule> select(List<Schedule> schedules);

    public void main(String[] args) {
        System.out.println("Start");
        List<Schedule> population = generuj();
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
    }

    protected abstract boolean shouldProceed();

    protected abstract Schedule[] getParents(List<Schedule> subpopulation);

    protected abstract boolean shouldCross();

    protected abstract boolean shouldMutate();
}
