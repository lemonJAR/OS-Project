# OS-Project
**Project Overview:**

This project is a **CPU Scheduling Visualizer** built in Java. It lets users simulate different CPU scheduling algorithms like **FCFS, SJF, SRTF, Round Robin**, and **MLFQ**. Users can enter processes manually or generate them randomly. The results are shown as an Gantt chart with key process metrics such as completion time, turnaround time, and response time.

**HOW TO RUN THE SIMULATION:**
- When you execute the program, the simulator should appear

- When opening the simulator, you are given a choice at the top left of the window a choice to either manually input the processes or randomly generate the processes under that is a text box to type the amount of processes you wish to input.

- When choosing the Manual Input option, below is a box entitled "Process Details (PID, Arrival, Burst). In this box you can Input the Arrival Time and Burst Time of the processes individually.

- Then at the bottom left of the simulator you can select the algorithms you wish to run or simulate.
  *Note - Round Robin requires you to input 1 time quantum. While MLFQ requries you to input 4 quantum slices seperated by a comma (1,2,3,4)

- Then you can press the Run Simulation button to visualize the processes.

**Description of each scheduling algorithm:**
- FCFS (First-Come, First-Served) / FIFO (First in, First Out) Processes are executed in the order they arrive, without preemption.

- SJF (Shortest Job First) Executes the process with the shortest burst time first, non-preemptively.

- SRTF (Shortest Remaining Time First) Runs the process with the least remaining burst time and preempts if a shorter one arrives.

- RR (Round Robin) Gives each process a fixed time slice and cycles through them fairly.

- MLFQ (Multi-Level Feedback Queue) Uses multiple priority queues to adaptively schedule processes based on their behavior and CPU usage.



 <img width="1218" height="847" alt="Screenshot 2025-07-18 003932" src="https://github.com/user-attachments/assets/331995e2-181a-4ef2-bdd0-2396b50ae5c0" />

 
**Sample input and expected output:**
<img width="1227" height="857" alt="image" src="https://github.com/user-attachments/assets/c25d8df2-14e6-4947-87e8-3643e09e38a9" />

<img width="1225" height="857" alt="image" src="https://github.com/user-attachments/assets/95871a50-da9b-4862-a383-347edc7f6255" />


**Known bugs, limitations or incomplete features:**
- As the processes increase the gantt chart size decreases.
- The Process details wont be locked when moving from manual to Auto generate

**Member Roles And Contributions:**
- Edcel Christian Repollo was tasked to finish the ui, gantt chartt, srtf, round robin, and mlfq

- John Anthony Romeo was taskes to finsih the process, scheduler, FIFO, SJF
