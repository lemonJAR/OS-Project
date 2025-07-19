/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.osfinalproject;

import java.util.*;

public class SJF extends Scheduler {
    public SJF(List<Process> processes) {
        super(processes);
    }

    @Override
    public void schedule() {
        System.out.println("\nSJF Scheduling (Tick-by-Tick Gantt):");
        // Create a copy to avoid modifying the original list
        List<Process> processesCopy = new ArrayList<>(processes);
        Collections.sort(processesCopy, Comparator.comparingInt(p -> p.arrivalTime));

        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
            Comparator.comparingInt((Process p) -> p.burstTime)
                     .thenComparingInt(p -> p.arrivalTime)
        );

        int currentTime = 0;
        int processIndex = 0; // To track processes from the sorted copy

        while (processIndex < processesCopy.size() || !readyQueue.isEmpty()) {
            // Add arriving processes to ready queue
            while (processIndex < processesCopy.size() && processesCopy.get(processIndex).arrivalTime <= currentTime) {
                readyQueue.add(processesCopy.get(processIndex++));
            }

            if (readyQueue.isEmpty()) {
                // Handle idle time
                int nextArrival = processIndex < processesCopy.size() ? processesCopy.get(processIndex).arrivalTime : currentTime + 1;
                for (int i = currentTime; i < nextArrival; i++) {
                    ganttChart.add(new GanttEntry(-1, i, i + 1)); // Record idle tick
                }
                currentTime = nextArrival;
                continue;
            }

            Process p = readyQueue.poll();

            if (!p.isResponded) {
                p.responseTime = currentTime - p.arrivalTime;
                p.isResponded = true;
            }

            // Execute process tick by tick
            for (int i = 0; i < p.burstTime; i++) {
                ganttChart.add(new GanttEntry(p.id, currentTime + i, currentTime + i + 1)); // Record process tick
            }
            currentTime += p.burstTime;

            p.completionTime = currentTime - 1;
            p.turnaroundTime = (currentTime - p.arrivalTime) - 1;
        }

        printMetrics();
        printAverages();
    }
}
