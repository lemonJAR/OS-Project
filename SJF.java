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
        System.out.println("\nSJF Scheduling:");
        Collections.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));
        
        int currentTime = 0;
        List<Process> readyQueue = new ArrayList<>();
        int index = 0;
        
        while (index < processes.size() || !readyQueue.isEmpty()) {
            // Add arriving processes to ready queue
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                readyQueue.add(processes.get(index++));
            }
            
            if (readyQueue.isEmpty()) {
                // Idle time
                int nextArrival = index < processes.size() ? processes.get(index).arrivalTime : currentTime + 1;
                ganttChart.add(new GanttEntry(-1, currentTime, nextArrival));
                currentTime = nextArrival;
                continue;
            }
            
            // Sort by burst time (SJF)
            readyQueue.sort(Comparator.comparingInt(p -> p.burstTime));
            Process p = readyQueue.remove(0);
            
            int startTime = currentTime;
            currentTime += p.burstTime;
            ganttChart.add(new GanttEntry(p.id, startTime, currentTime));
            
            p.completionTime = currentTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.responseTime = p.turnaroundTime; // For SJF, response time = turnaround time
        }
        
        printMetrics();
    }
}