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
        System.out.println("\nSRTF Scheduling (Tick-by-Tick Gantt):");
        // Create a copy to avoid modifying the original list
        List<Process> processesCopy = new ArrayList<>(processes);
        Collections.sort(processesCopy, Comparator.comparingInt(p -> p.arrivalTime));

        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
            Comparator.comparingInt((Process p) -> p.remainingBurstTime)
                    .thenComparingInt(p -> p.arrivalTime)
        );

        int currentTime = 0;
        int processIndex = 0; // To track processes from the sorted copy
        Process currentProcess = null;
        int lastExecutionStartTime = 0; // To track when the current process started its current continuous execution block

        while (true) {
            // Add arriving processes to ready queue
            while (processIndex < processesCopy.size() && processesCopy.get(processIndex).arrivalTime <= currentTime) {
                Process p = processesCopy.get(processIndex++);
                p.remainingBurstTime = p.burstTime; // Ensure remainingBurstTime is set for the copy
                readyQueue.add(p);
            }

            // Determine the next process to run
            Process nextProcess = null;
            if (currentProcess != null) {
                nextProcess = currentProcess; // Assume current process continues
            }
            if (!readyQueue.isEmpty()) {
                if (nextProcess == null || readyQueue.peek().remainingBurstTime < nextProcess.remainingBurstTime) {
                    nextProcess = readyQueue.poll();
                    if (currentProcess != null && currentProcess != nextProcess) {
                        // Current process is preempted, add it back to ready queue
                        readyQueue.add(currentProcess);
                    }
                }
            }

            // Record Gantt entry for the previous block if process switched or finished
            if (currentProcess != null && currentProcess != nextProcess) {
                // Process switched or finished, record the block
                for (int i = lastExecutionStartTime; i < currentTime; i++) {
                    ganttChart.add(new GanttEntry(currentProcess.id, i, i + 1));
                }
            }

            currentProcess = nextProcess;

            if (currentProcess == null) {
                // CPU is idle
                if (processIndex == processesCopy.size() && readyQueue.isEmpty()) {
                    // All processes arrived and completed
                    break;
                }
                // Record idle tick
                ganttChart.add(new GanttEntry(-1, currentTime, currentTime + 1));
                currentTime++;
                lastExecutionStartTime = currentTime; // Reset for next process
            } else {
                // Process is running
                if (!currentProcess.isResponded) {
                    currentProcess.responseTime = currentTime - currentProcess.arrivalTime;
                    currentProcess.isResponded = true;
                }

                // Record current tick
                ganttChart.add(new GanttEntry(currentProcess.id, currentTime, currentTime + 1));
                currentTime++;
                currentProcess.remainingBurstTime--;

                if (currentProcess.remainingBurstTime == 0) {
                    // Process completed
                    currentProcess.completionTime = currentTime;
                    currentProcess.turnaroundTime = currentTime - currentProcess.arrivalTime;
                    currentProcess = null; // No current process
                    lastExecutionStartTime = currentTime; // Reset for next process
                }
            }
        }

        printMetrics();
        printAverages();
    }
}
