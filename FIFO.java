/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.osfinalproject;

import java.util.*;

public class FIFO extends Scheduler {
    public FIFO(List<Process> processes) {
        super(processes);
    }

    @Override
    public void schedule() {
        System.out.println("\nFIFO Scheduling (Tick-by-Tick Gantt):");
        // Create a copy to avoid modifying the original list
        List<Process> processesCopy = new ArrayList<>(processes);
        Collections.sort(processesCopy, Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        for (Process p : processesCopy) { // Use the copy
            // Handle idle time
            if (currentTime < p.arrivalTime) {
                for (int i = currentTime; i < p.arrivalTime; i++) {
                    ganttChart.add(new GanttEntry(-1, i, i + 1)); // Record idle tick
                }
                currentTime = p.arrivalTime;
            }

            if (!p.isResponded) {
                p.responseTime = currentTime - p.arrivalTime;
                p.isResponded = true;
            }

            // Execute process tick by tick
            for (int i = 0; i < p.burstTime; i++) {
                ganttChart.add(new GanttEntry(p.id, currentTime + i, currentTime + i + 1)); // Record process tick
            }
            currentTime += p.burstTime;

            p.completionTime = currentTime;
            p.turnaroundTime = currentTime - p.arrivalTime;
        }

        printMetrics();
        printAverages();
    }
}
