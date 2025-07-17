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
        System.out.println("\nFIFO Scheduling:");
        Collections.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));
        
        int currentTime = 0;
        for (Process p : processes) {
            if (currentTime < p.arrivalTime) {
                ganttChart.add(new GanttEntry(-1, currentTime, p.arrivalTime));
                currentTime = p.arrivalTime;
            }
            
            if (!p.isResponded) {
                p.responseTime = currentTime - p.arrivalTime;
                p.isResponded = true;
            }
            
            int startTime = currentTime;
            currentTime += p.burstTime;
            ganttChart.add(new GanttEntry(p.id, startTime, currentTime));
            
            p.completionTime = currentTime;
            p.turnaroundTime = currentTime - p.arrivalTime;
        }
        
        printMetrics();
        printAverages();
    }
}
