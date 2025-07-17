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
        
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
            Comparator.comparingInt((Process p) -> p.burstTime)
                     .thenComparingInt(p -> p.arrivalTime)
        );
        
        int currentTime = 0;
        int index = 0;
        
        while (index < processes.size() || !readyQueue.isEmpty()) {
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                readyQueue.add(processes.get(index++));
            }
            
            if (readyQueue.isEmpty()) {
                int nextArrival = index < processes.size() ? processes.get(index).arrivalTime : currentTime + 1;
                ganttChart.add(new GanttEntry(-1, currentTime, nextArrival));
                currentTime = nextArrival;
                continue;
            }
            
            Process p = readyQueue.poll();
            
            if (!p.isResponded) {
                p.responseTime = currentTime - p.arrivalTime;
                p.isResponded = true;
            }
            
            ganttChart.add(new GanttEntry(p.id, currentTime, currentTime + p.burstTime));
            currentTime += p.burstTime;
            p.completionTime = currentTime;
            p.turnaroundTime = currentTime - p.arrivalTime;
        }
        
        printMetrics();
        printAverages();
    }
}