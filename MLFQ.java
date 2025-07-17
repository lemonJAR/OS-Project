/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.osfinalproject;

import java.util.*;

public class MLFQ extends Scheduler {
    private final int[] timeQuantums;
    
    public MLFQ(List<Process> processes, int[] timeQuantums) {
        super(processes);
        this.timeQuantums = timeQuantums;
    }

    @Override
    public void schedule() {
        System.out.println("\nMLFQ Scheduling (Quantums: Q0=" + timeQuantums[0] + 
                         ", Q1=" + timeQuantums[1] + ", Q2=" + timeQuantums[2] + 
                         ", Q3=" + timeQuantums[3] + ")");
        
        List<Queue<Process>> queues = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            queues.add(new LinkedList<>());
        }
        
        List<Process> processList = new ArrayList<>(processes);
        Collections.sort(processList, Comparator.comparingInt(p -> p.arrivalTime));
        
        int currentTime = 0;
        Process currentProcess = null;
        int currentLevel = -1;
        int allotmentRemaining = 0;
        
        while (!processList.isEmpty() || currentProcess != null || hasProcesses(queues)) {
            // Add arriving processes to Q0
            while (!processList.isEmpty() && processList.get(0).arrivalTime <= currentTime) {
                Process p = processList.remove(0);
                p.remainingBurstTime = p.burstTime;
                p.level = 0;
                queues.get(0).add(p);
            }
            
            if (currentProcess == null) {
                // Select process from highest priority queue
                for (currentLevel = 0; currentLevel < 4; currentLevel++) {
                    if (!queues.get(currentLevel).isEmpty()) {
                        currentProcess = queues.get(currentLevel).poll();
                        allotmentRemaining = timeQuantums[currentLevel];
                        break;
                    }
                }
                
                if (currentProcess != null && !currentProcess.isResponded) {
                    currentProcess.responseTime = currentTime - currentProcess.arrivalTime;
                    currentProcess.isResponded = true;
                }
            }
            
            if (currentProcess != null) {
                int execTime = Math.min(currentProcess.remainingBurstTime, 1);
                ganttChart.add(new GanttEntry(currentProcess.id, currentTime, currentTime + execTime));
                
                currentTime += execTime;
                currentProcess.remainingBurstTime -= execTime;
                allotmentRemaining -= execTime;
                
                if (currentProcess.remainingBurstTime == 0) {
                    currentProcess.completionTime = currentTime;
                    currentProcess.turnaroundTime = currentTime - currentProcess.arrivalTime;
                    currentProcess = null;
                } else if (allotmentRemaining == 0) {
                    // Time quantum expired
                    int newLevel = Math.min(currentLevel + 1, 3);
                    currentProcess.level = newLevel;
                    queues.get(newLevel).add(currentProcess);
                    currentProcess = null;
                }
            } else {
                // Idle time - advance to next arrival
                int nextTime = processList.isEmpty() ? currentTime + 1 : processList.get(0).arrivalTime;
                ganttChart.add(new GanttEntry(-1, currentTime, nextTime));
                currentTime = nextTime;
            }
        }
        
        printMetrics();
        printAverages();
    }
    
    private boolean hasProcesses(List<Queue<Process>> queues) {
        for (Queue<Process> q : queues) {
            if (!q.isEmpty()) return true;
        }
        return false;
    }
}
