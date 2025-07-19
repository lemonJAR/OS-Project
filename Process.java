/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.osfinalproject;

public class Process {
    public int id;
    public int arrivalTime;
    public int burstTime;
    public int remainingBurstTime;
    public int completionTime;
    public int turnaroundTime;
    public int waitingTime;
    public int responseTime;
    public boolean isResponded;
    
    // MLFQ-specific fields
    public int currentQueue;        // Tracks which queue the process is in (0=highest priority)
    public int timeUsedInQueue;     // Tracks how much time process has used in current queue
    public int timeUsedInSlice;     // Tracks how much time used in current time slice
    public int lastRunTime;         // For aging calculations (though not fully implemented in MLFQ yet)

    public Process(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingBurstTime = burstTime;
        this.isResponded = false;
        this.currentQueue = 0;
        this.timeUsedInQueue = 0;
        this.timeUsedInSlice = 0;
        this.lastRunTime = -1; // Initialize to -1 or 0
    }

    @Override
    public String toString() {
        return String.format(
            "P%d [AT:%d, BT:%d, CT:%d, TAT:%d, WT:%d, RT:%d, Q:%d]",
            id, arrivalTime, burstTime, completionTime, 
            turnaroundTime, waitingTime, responseTime, currentQueue
        );
    }
}
