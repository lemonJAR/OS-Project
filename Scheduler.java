/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.osfinalproject;

import java.util.*;

public abstract class Scheduler {
    protected List<Process> processes;
    protected List<GanttEntry> ganttChart = new ArrayList<>();

    public Scheduler(List<Process> processes) {
        this.processes = processes;
    }

    public abstract void schedule();

    protected void printMetrics() {
        System.out.println("\nProcess Metrics:");
        System.out.println("PID | Arrival | Burst | Completion | Turnaround | Response");
        for (Process p : processes) {
            System.out.printf("%3d | %7d | %5d | %10d | %10d | %8d%n",
                p.id, p.arrivalTime, p.burstTime, 
                p.completionTime, p.turnaroundTime, p.responseTime);
        }
    }

    protected void printAverages() {
        double totalTurnaround = 0;
        double totalResponse = 0;
        
        for (Process p : processes) {
            totalTurnaround += p.turnaroundTime;
            totalResponse += p.responseTime;
        }
        
        System.out.printf("\nAverage Turnaround Time: %.2f%n", totalTurnaround / processes.size());
        System.out.printf("Average Response Time: %.2f%n", totalResponse / processes.size());
    }

    public void printGanttChart() {
        System.out.println("\nGantt Chart:");
        System.out.print("Time: ");
        for (GanttEntry entry : ganttChart) {
            System.out.printf("%3d ", entry.startTime);
        }
        System.out.println(ganttChart.get(ganttChart.size()-1).endTime);
        
        System.out.print("Proc: ");
        for (GanttEntry entry : ganttChart) {
            if (entry.processId == -1) {
                System.out.print("-- ");
            } else {
                System.out.printf("P%-2d", entry.processId);
            }
        }
        System.out.println();
    }

    protected static class GanttEntry {
        int processId;
        int startTime;
        int endTime;

        public GanttEntry(int processId, int startTime, int endTime) {
            this.processId = processId;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
}
