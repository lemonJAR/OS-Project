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
    public int responseTime;
    public int level = -1; // For MLFQ
    public boolean isResponded = false;

    public Process(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingBurstTime = burstTime;
        this.responseTime = -1; // Initialize to -1 (not responded)
    }
}
