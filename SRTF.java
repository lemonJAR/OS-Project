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

        // PriorityQueue for ready processes, ordered by remaining burst time, then arrival time (for tie-breaking)
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
            Comparator.comparingInt((Process p) -> p.remainingBurstTime)
                    .thenComparingInt(p -> p.arrivalTime)
                    .thenComparingInt(p -> p.id) // Tie-break by ID for consistency
        );

        int currentTime = 0;
        int processIndex = 0; // To track processes from the sorted copy
        Process currentProcess = null; // The process currently running on the CPU
        int lastExecutionStartTime = 0; // The time when the current continuous execution block started

        // Loop until all processes have arrived and all processes in the ready queue are completed
        while (processIndex < processesCopy.size() || !readyQueue.isEmpty() || currentProcess != null) {

            // Add newly arriving processes to the ready queue
            while (processIndex < processesCopy.size() && processesCopy.get(processIndex).arrivalTime <= currentTime) {
                Process p = processesCopy.get(processIndex++);
                p.remainingBurstTime = p.burstTime; // Ensure remainingBurstTime is set for the copy
                readyQueue.add(p);
            }

            // Determine the next process to run
            Process nextProcessCandidate = null;
            if (!readyQueue.isEmpty()) {
                nextProcessCandidate = readyQueue.peek(); // Get the process with shortest remaining time
            }

            // Check for preemption or if a new process should start
            if (currentProcess == null || (nextProcessCandidate != null && nextProcessCandidate.remainingBurstTime < currentProcess.remainingBurstTime)) {
                // Preemption or new process to run
                if (currentProcess != null) {
                    // If a process was running, record its completed block in Gantt chart
                    // This handles the case where a process is preempted
                    for (int i = lastExecutionStartTime; i < currentTime; i++) {
                        ganttChart.add(new GanttEntry(currentProcess.id, i, i + 1));
                    }
                    // Add the preempted process back to the ready queue
                    readyQueue.add(currentProcess);
                }
                currentProcess = readyQueue.poll(); // Get the new process to run
                lastExecutionStartTime = currentTime; // Reset start time for the new block
            }

            // If no process is ready and no process is currently running, CPU is idle
            if (currentProcess == null) {
                // Record idle tick
                ganttChart.add(new GanttEntry(-1, currentTime, currentTime + 1));
                currentTime++;
                lastExecutionStartTime = currentTime; // Reset for next process
                continue; // Continue to next time unit
            }

            // Process is running
            if (!currentProcess.isResponded) {
                currentProcess.responseTime = currentTime - currentProcess.arrivalTime;
                currentProcess.isResponded = true;
            }

            // Execute for one time unit
            currentProcess.remainingBurstTime--;
            currentTime++;

            // Check if the current process completed its execution
            if (currentProcess.remainingBurstTime == 0) {
                // Record the completed block in Gantt chart
                for (int i = lastExecutionStartTime; i < currentTime; i++) {
                    ganttChart.add(new GanttEntry(currentProcess.id, i, i + 1));
                }
                currentProcess.completionTime = currentTime - 1;
                currentProcess.turnaroundTime = (currentTime - currentProcess.arrivalTime) - 1;
                currentProcess = null; // No process running
                lastExecutionStartTime = currentTime; // Reset for next process
            }
        }

        // After the loop, ensure all processes have their final metrics calculated
        // (This is mostly for processes that might have been the last one running)
        // The current logic should handle this, but it's a good sanity check.
        // The metrics are calculated when remainingBurstTime == 0.

        printMetrics();
        printAverages();
    }
}
