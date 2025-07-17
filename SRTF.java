/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.osfinalproject;

import java.util.*;

public class SRTF extends Scheduler {
    public SRTF(List<Process> processes) {
        super(processes);
    }

    @Override
    public void schedule() {
        System.out.println("\nSRTF Scheduling:");
        Collections.sort(processes, Comparator.comparingInt(p -> p.arrivalTime));
        
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
            Comparator.comparingInt((Process p) -> p.remainingBurstTime)
                      .thenComparingInt(p -> p.id)
        );
        
        int currentTime = 0;
        int index = 0;
        Process currentProcess = null;
        int lastSwitchTime = 0;
        
        while (index < processes.size() || !readyQueue.isEmpty() || currentProcess != null) {
            // Add arriving processes to ready queue
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                Process p = processes.get(index++);
                p.remainingBurstTime = p.burstTime;
                readyQueue.add(p);
            }
            
            // Check if we should preempt
            if (currentProcess != null && !readyQueue.isEmpty() &&
                readyQueue.peek().remainingBurstTime < currentProcess.remainingBurstTime) {
                readyQueue.add(currentProcess);
                currentProcess = readyQueue.poll();
                ganttChart.add(new GanttEntry(currentProcess.id, lastSwitchTime, currentTime));
                lastSwitchTime = currentTime;
            }
            
            if (currentProcess == null && !readyQueue.isEmpty()) {
                currentProcess = readyQueue.poll();
                if (currentProcess.responseTime == -1) {
                    currentProcess.responseTime = currentTime - currentProcess.arrivalTime;
                }
                lastSwitchTime = currentTime;
            }
            
            if (currentProcess != null) {
                currentTime++;
                currentProcess.remainingBurstTime--;
                
                if (currentProcess.remainingBurstTime == 0) {
                    ganttChart.add(new GanttEntry(currentProcess.id, lastSwitchTime, currentTime));
                    currentProcess.completionTime = currentTime;
                    currentProcess.turnaroundTime = currentTime - currentProcess.arrivalTime;
                    currentProcess = null;
                }
            } else {
                // Idle time
                int nextArrival = index < processes.size() ? processes.get(index).arrivalTime : currentTime + 1;
                ganttChart.add(new GanttEntry(-1, currentTime, nextArrival));
                currentTime = nextArrival;
            }
        }
        
        printMetrics();
    }
}