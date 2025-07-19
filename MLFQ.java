/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.osfinalproject;

import java.util.*;

public class MLFQ extends Scheduler {
    private static final int NUM_QUEUES = 4;
    private final int[] timeSlices;      // Quantum for each queue (Q0, Q1, Q2, Q3)
    private final int[] allotmentTimes;  // Max time a process can stay in each queue (A0, A1, A2, A3)

    public MLFQ(List<Process> processes, int[] timeSlices, int[] allotmentTimes) {
        super(processes);
        if (timeSlices == null || timeSlices.length != NUM_QUEUES || 
            allotmentTimes == null || allotmentTimes.length != NUM_QUEUES) {
            throw new IllegalArgumentException("MLFQ requires exactly " + NUM_QUEUES + 
                " time slices and allotment times.");
        }
        this.timeSlices = timeSlices;
        this.allotmentTimes = allotmentTimes;
    }

    @Override
    public void schedule() {
        System.out.println("\nMLFQ Scheduling (4 queues)");
        System.out.println("Time Slices: " + Arrays.toString(timeSlices));
        System.out.println("Allotment Times: " + Arrays.toString(allotmentTimes));

        List<Queue<Process>> queues = new ArrayList<>();
        for (int i = 0; i < NUM_QUEUES; i++) {
            queues.add(new LinkedList<>());
        }

        List<Process> processesCopy = new ArrayList<>(processes);
        processesCopy.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        int processIndex = 0;
        
        // Set to keep track of processes that have completed
        Set<Integer> completedProcessIds = new HashSet<>();

        while (processIndex < processesCopy.size() || !allQueuesEmpty(queues) || completedProcessIds.size() < processes.size()) {
            // Add arriving processes to queue 0
            while (processIndex < processesCopy.size() && 
                   processesCopy.get(processIndex).arrivalTime <= currentTime) {
                Process p = processesCopy.get(processIndex++);
                p.currentQueue = 0;
                p.timeUsedInQueue = 0;
                p.timeUsedInSlice = 0;
                queues.get(0).add(p);
                // First response time is recorded when it first gets CPU
                // This is handled below when currentProcess is not null
            }

            // Find highest priority process
            Process currentProcess = null;
            int currentQueue = -1;
            for (int q = 0; q < NUM_QUEUES; q++) {
                if (!queues.get(q).isEmpty()) {
                    currentQueue = q;
                    currentProcess = queues.get(q).poll();
                    break;
                }
            }

            if (currentProcess == null) {
                // If no process is ready, and there are still processes to arrive or be completed
                if (processIndex < processesCopy.size() || completedProcessIds.size() < processes.size()) {
                    ganttChart.add(new GanttEntry(-1, currentTime, currentTime + 1));
                    currentTime++;
                } else {
                    // All processes arrived and completed
                    break; 
                }
                continue;
            }

            // Record response time if not already responded
            if (!currentProcess.isResponded) {
                currentProcess.responseTime = currentTime - currentProcess.arrivalTime;
                currentProcess.isResponded = true;
            }

            // Calculate execution time (minimum of: slice remainder, burst remainder)
            int timeToExecute = Math.min(
                timeSlices[currentQueue] - currentProcess.timeUsedInSlice,
                currentProcess.remainingBurstTime
            );
            
            // Ensure timeToExecute is at least 1 if process needs to run
            if (timeToExecute <= 0 && currentProcess.remainingBurstTime > 0) {
                // This case should ideally not happen if timeSlices are positive,
                // but as a safeguard, execute for at least 1 unit if process is not done
                timeToExecute = 1; 
            }
            
            // If timeToExecute is 0 (e.g., timeUsedInSlice already equals timeSlice),
            // it means this process should be demoted or re-queued without execution.
            // This is a critical point for MLFQ logic.
            if (timeToExecute == 0 && currentProcess.remainingBurstTime > 0) {
                // This process has exhausted its time slice for this turn.
                // It should be re-queued or demoted.
                // The logic below handles this. For now, don't execute.
                // Just re-add to queue or demote.
            } else {
                // Execute the process
                ganttChart.add(new GanttEntry(currentProcess.id, currentTime, currentTime + timeToExecute));
                currentTime += timeToExecute;
                currentProcess.remainingBurstTime -= timeToExecute;
                currentProcess.timeUsedInSlice += timeToExecute;
                currentProcess.timeUsedInQueue += timeToExecute;
            }

            if (currentProcess.remainingBurstTime == 0) {
                currentProcess.completionTime = currentTime - 1;
                currentProcess.turnaroundTime = (currentTime - currentProcess.arrivalTime) -1;
                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                completedProcessIds.add(currentProcess.id); // Mark as completed
                continue; // Process finished, no need to re-queue or demote
            }

            // Check for queue demotion
            boolean demote = false;
            if (currentProcess.timeUsedInQueue >= allotmentTimes[currentQueue] && 
                currentQueue < NUM_QUEUES - 1) {
                demote = true;
            }
            // Check if time slice exhausted but still has allotment remaining
            else if (currentProcess.timeUsedInSlice >= timeSlices[currentQueue]) {
                // Time slice exhausted, but not allotment. Reset slice and re-queue in same queue.
                currentProcess.timeUsedInSlice = 0;
                queues.get(currentQueue).add(currentProcess);
                continue; // Process re-queued, move to next iteration
            }

            if (demote) {
                currentQueue++;
                currentProcess.currentQueue = currentQueue;
                currentProcess.timeUsedInQueue = 0; // Reset time used in new queue
                currentProcess.timeUsedInSlice = 0; // Reset slice for new queue
            }

            queues.get(currentQueue).add(currentProcess);
        }

        // Ensure all processes have their final metrics calculated
        // This loop is a safeguard, as they should be set when remainingBurstTime == 0
        for (Process p : processes) {
            if (p.remainingBurstTime > 0) {
                // This should ideally not happen if the loop condition is correct
                // and all processes eventually complete.
                // If it does, it means the scheduler didn't finish a process.
                System.err.println("Warning: Process P" + p.id + " did not complete.");
            }
            // Calculate waiting time if not already done (turnaround - burst)
            if (p.turnaroundTime > 0 && p.burstTime > 0 && p.waitingTime == 0) {
                 p.waitingTime = p.turnaroundTime - p.burstTime;
            }
        }

        printMetrics();
        printAverages();
    }

    private boolean allQueuesEmpty(List<Queue<Process>> queues) {
        for (Queue<Process> q : queues) {
            if (!q.isEmpty()) return false;
        }
        return true;
    }
}
