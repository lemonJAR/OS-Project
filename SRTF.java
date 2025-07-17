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
                    .thenComparingInt(p -> p.arrivalTime)
        );
        
        int currentTime = 0;
        int lastSwitchTime = 0;
        Process currentProcess = null;
        
        while (true) {
            // Add arriving processes
            while (!processes.isEmpty() && processes.get(0).arrivalTime <= currentTime) {
                Process p = processes.remove(0);
                p.remainingBurstTime = p.burstTime;
                readyQueue.add(p);
            }
            
            // Check for preemption
            if (currentProcess != null && !readyQueue.isEmpty() &&
                readyQueue.peek().remainingBurstTime < currentProcess.remainingBurstTime) {
                readyQueue.add(currentProcess);
                currentProcess = readyQueue.poll();
                ganttChart.add(new GanttEntry(currentProcess.id, lastSwitchTime, currentTime));
                lastSwitchTime = currentTime;
            }
            
            if (currentProcess == null && !readyQueue.isEmpty()) {
                currentProcess = readyQueue.poll();
                if (!currentProcess.isResponded) {
                    currentProcess.responseTime = currentTime - currentProcess.arrivalTime;
                    currentProcess.isResponded = true;
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
                if (processes.isEmpty()) break;
                ganttChart.add(new GanttEntry(-1, currentTime, currentTime + 1));
                currentTime++;
            }
        }
        
        printMetrics();
        printAverages();
    }
}