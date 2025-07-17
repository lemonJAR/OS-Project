/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.osfinalproject;

import java.util.*;

public class RoundRobin extends Scheduler {
    private final int timeQuantum;

    public RoundRobin(List<Process> processes, int timeQuantum) {
        super(processes);
        this.timeQuantum = timeQuantum;
    }

    @Override
    public void schedule() {
        System.out.println("\nRound Robin Scheduling (Quantum=" + timeQuantum + ", Tick-by-Tick Gantt)");

        Queue<Process> readyQueue = new LinkedList<>();
        int currentTime = 0;
        // Create a copy to avoid modifying the original list
        List<Process> processesCopy = new ArrayList<>(processes);
        Collections.sort(processesCopy, Comparator.comparingInt(p -> p.arrivalTime));
        int processIndex = 0; // To track processes from the sorted copy

        while (processIndex < processesCopy.size() || !readyQueue.isEmpty()) {
            // Add arriving processes to ready queue
            while (processIndex < processesCopy.size() && processesCopy.get(processIndex).arrivalTime <= currentTime) {
                Process p = processesCopy.get(processIndex++);
                p.remainingBurstTime = p.burstTime; // Ensure remainingBurstTime is set for the copy
                readyQueue.add(p);
            }

            if (readyQueue.isEmpty()) {
                // Handle idle time
                if (processIndex < processesCopy.size()) {
                    int nextArrival = processesCopy.get(processIndex).arrivalTime;
                    for (int i = currentTime; i < nextArrival; i++) {
                        ganttChart.add(new GanttEntry(-1, i, i + 1)); // Record idle tick
                    }
                    currentTime = nextArrival;
                    continue;
                } else {
                    break; // All processes finished and no more arriving
                }
            }

            Process current = readyQueue.poll();

            if (!current.isResponded) {
                current.responseTime = currentTime - current.arrivalTime;
                current.isResponded = true;
            }

            int execTime = Math.min(current.remainingBurstTime, timeQuantum);

            // Execute process tick by tick
            for (int i = 0; i < execTime; i++) {
                ganttChart.add(new GanttEntry(current.id, currentTime + i, currentTime + i + 1)); // Record process tick
            }
            currentTime += execTime;
            current.remainingBurstTime -= execTime;

            // Add arriving processes during execution (important for RR)
            while (processIndex < processesCopy.size() && processesCopy.get(processIndex).arrivalTime <= currentTime) {
                Process p = processesCopy.get(processIndex++);
                p.remainingBurstTime = p.burstTime; // Ensure remainingBurstTime is set for the copy
                readyQueue.add(p);
            }

            if (current.remainingBurstTime > 0) {
                readyQueue.add(current); // Re-add to the end of the queue
            } else {
                current.completionTime = currentTime;
                current.turnaroundTime = currentTime - current.arrivalTime;
            }
        }

        printMetrics();
        printAverages();
    }
}
