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
        System.out.println("\nRound Robin Scheduling (Quantum=" + timeQuantum + ")");
        
        Queue<Process> readyQueue = new LinkedList<>();
        int currentTime = 0;
        int lastSwitchTime = 0;
        List<Process> processList = new ArrayList<>(processes);
        
        Collections.sort(processList, Comparator.comparingInt(p -> p.arrivalTime));
        
        while (!processList.isEmpty() || !readyQueue.isEmpty()) {
            // Add arriving processes
            while (!processList.isEmpty() && processList.get(0).arrivalTime <= currentTime) {
                Process p = processList.remove(0);
                p.remainingBurstTime = p.burstTime;
                readyQueue.add(p);
            }
            
            if (readyQueue.isEmpty()) {
                if (!processList.isEmpty()) {
                    currentTime = processList.get(0).arrivalTime;
                    continue;
                } else {
                    break;
                }
            }
            
            Process current = readyQueue.poll();
            
            if (!current.isResponded) {
                current.responseTime = currentTime - current.arrivalTime;
                current.isResponded = true;
            }
            
            int execTime = Math.min(current.remainingBurstTime, timeQuantum);
            ganttChart.add(new GanttEntry(current.id, currentTime, currentTime + execTime));
            
            currentTime += execTime;
            current.remainingBurstTime -= execTime;
            
            // Add arriving processes during execution
            while (!processList.isEmpty() && processList.get(0).arrivalTime <= currentTime) {
                readyQueue.add(processList.remove(0));
            }

        }
        
        printMetrics();
        printAverages();
    }
}