/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.osfinalproject;

import java.util.*;

public class Osfinalproject {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Process> processes = new ArrayList<>();

        System.out.print("Do you want to manually input process details or auto-generate them? \n(1 for Manual, 2 for Auto-generate):");
        int inputChoice = scanner.nextInt();

        if (inputChoice == 1) {
            System.out.print("Enter number of processes:");
            int n = scanner.nextInt();
            for (int i = 0; i < n; i++) {
                System.out.print("Enter Arrival Time and Burst Time for Process " + (i + 1) + ":");
                int arrivalTime = scanner.nextInt();
                int burstTime = scanner.nextInt();
                processes.add(new Process(i, arrivalTime, burstTime));
            }
        } else if (inputChoice == 2) {
            System.out.print("Enter number of processes to auto-generate:");
            int n = scanner.nextInt();
            Random random = new Random();
            for (int i = 0; i < n; i++) {
                int arrivalTime = random.nextInt(10);
                int burstTime = random.nextInt(10) + 1;
                processes.add(new Process(i, arrivalTime, burstTime));
                System.out.printf("Generated Process %d: Arrival Time = %d, Burst Time = %d%n",
                                  i, arrivalTime, burstTime);
            }
        } else {
            System.out.print("Invalid choice. Exiting.");
            scanner.close();
            return;
        }

        System.out.println("Select Scheduling Algorithm:\n1. FIFO\n2. SJF\n3. SRTF\n4. Round Robin\n5. MLFQ \nChoice:");
        int algorithmChoice = scanner.nextInt();

        Scheduler scheduler = null;
        switch (algorithmChoice) {
            case 1:
                scheduler = new FIFO(processes);
                break;
            case 2:
                scheduler = new SJF(processes);
                break;
            case 3:
                scheduler = new SRTF(processes);
                break;
            case 4:
                System.out.println("Enter Time Quantum for Round Robin:");
                int rrQuantum = scanner.nextInt();
                scheduler = new RoundRobin(processes, rrQuantum);
                break;
            case 5:
                //mlfq
                break;
            default:
                System.out.println("Invalid choice.");
                scanner.close();
                return;
        }

        scheduler.schedule();
        scheduler.printGanttChart();
        scanner.close();
    }
}
