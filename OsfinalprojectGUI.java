/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.osfinalproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream; // Import for output capture
import java.io.PrintStream;           // Import for output capture

public class OsfinalprojectGUI extends JFrame {
    // Input Components
    private JRadioButton manualInputRadio, autoGenerateRadio;
    private ButtonGroup inputMethodGroup;
    private JTextField numProcessesField;
    private JPanel processInputPanel;
    private JComboBox<String> algorithmComboBox;
    private JTextField quantumField; // For RR quantum
    private JTextField timeSlicesField, allotmentTimesField; // For MLFQ
    private JLabel quantumLabel, timeSlicesLabel, allotmentTimesLabel;
    private JButton runSimulationButton;

    // Output Components
    private JTextArea metricsTextArea;
    private JLabel avgTurnaroundLabel, avgResponseLabel;
    private GanttChartPanel ganttChartPanel;

    private List<Process> processes;

    public OsfinalprojectGUI() {
        super("OS Scheduling Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(1000, 700);
        setLocationRelativeTo(null);

        processes = new ArrayList<>();

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Setup"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Input Method Selection
        manualInputRadio = new JRadioButton("Manual Input");
        autoGenerateRadio = new JRadioButton("Auto-generate");
        inputMethodGroup = new ButtonGroup();
        inputMethodGroup.add(manualInputRadio);
        inputMethodGroup.add(autoGenerateRadio);
        autoGenerateRadio.setSelected(true);

        JPanel radioPanel = new JPanel();
        radioPanel.add(manualInputRadio);
        radioPanel.add(autoGenerateRadio);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        inputPanel.add(radioPanel, gbc);

        // Number of Processes
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        inputPanel.add(new JLabel("Number of Processes:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        numProcessesField = new JTextField("5", 5);
        inputPanel.add(numProcessesField, gbc);

        // Manual Process Input Panel
        processInputPanel = new JPanel();
        processInputPanel.setLayout(new BoxLayout(processInputPanel, BoxLayout.Y_AXIS));
        processInputPanel.setBorder(BorderFactory.createTitledBorder("Process Details (PID, Arrival, Burst)"));
        JScrollPane scrollPane = new JScrollPane(processInputPanel);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.weighty = 0.5;
        inputPanel.add(scrollPane, gbc);
        gbc.weighty = 0;

        // Algorithm Selection
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        inputPanel.add(new JLabel("Select Algorithm:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        String[] algorithms = {"FIFO", "SJF", "SRTF", "Round Robin", "MLFQ"};
        algorithmComboBox = new JComboBox<>(algorithms);
        inputPanel.add(algorithmComboBox, gbc);

        // RR Quantum
        gbc.gridx = 0; gbc.gridy = 4;
        quantumLabel = new JLabel("RR Quantum:");
        inputPanel.add(quantumLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        quantumField = new JTextField("2", 5);
        inputPanel.add(quantumField, gbc);

        // MLFQ Time Slices
        gbc.gridx = 0; gbc.gridy = 5;
        timeSlicesLabel = new JLabel("MLFQ Time Slices (Q0-Q3):");
        inputPanel.add(timeSlicesLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        timeSlicesField = new JTextField("4,8,16,INF", 15);
        inputPanel.add(timeSlicesField, gbc);

        // MLFQ Allotment Times
        gbc.gridx = 0; gbc.gridy = 6;
        allotmentTimesLabel = new JLabel("MLFQ Allotment Times (A0-A3):");
        inputPanel.add(allotmentTimesLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        allotmentTimesField = new JTextField("5,10,20,INF", 15);
        inputPanel.add(allotmentTimesField, gbc);

        // Run Simulation Button
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        runSimulationButton = new JButton("Run Simulation");
        inputPanel.add(runSimulationButton, gbc);

        add(inputPanel, BorderLayout.WEST);

        // --- Output Panel ---
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Results"));

        metricsTextArea = new JTextArea(10, 40);
        metricsTextArea.setEditable(false);
        metricsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputPanel.add(new JScrollPane(metricsTextArea), BorderLayout.NORTH);

        JPanel avgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        avgTurnaroundLabel = new JLabel("Average Turnaround Time: N/A");
        avgResponseLabel = new JLabel("Average Response Time: N/A");
        avgPanel.add(avgTurnaroundLabel);
        avgPanel.add(Box.createHorizontalStrut(20));
        avgPanel.add(avgResponseLabel);
        outputPanel.add(avgPanel, BorderLayout.CENTER);

        ganttChartPanel = new GanttChartPanel();
        ganttChartPanel.setPreferredSize(new Dimension(600, 200));
        ganttChartPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        outputPanel.add(ganttChartPanel, BorderLayout.SOUTH);

        add(outputPanel, BorderLayout.CENTER);

        // Event Listeners
        manualInputRadio.addActionListener(e -> toggleInputMethod(true));
        autoGenerateRadio.addActionListener(e -> toggleInputMethod(false));
        algorithmComboBox.addActionListener(e -> updateInputVisibility());
        runSimulationButton.addActionListener(new RunSimulationListener());

        // Initial state
        toggleInputMethod(false);
        updateInputVisibility();
    }

    private void toggleInputMethod(boolean isManual) {
        processInputPanel.removeAll();
        if (isManual) {
            int numProc;
            try {
                numProc = Integer.parseInt(numProcessesField.getText());
            } catch (NumberFormatException ex) {
                numProc = 0;
            }
            for (int i = 0; i < numProc; i++) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                row.add(new JLabel("P" + i + ": AT="));
                row.add(new JTextField("0", 3));
                row.add(new JLabel("BT="));
                row.add(new JTextField("5", 3));
                processInputPanel.add(row);
            }
            processInputPanel.revalidate();
            processInputPanel.repaint();
        }
    }

    private void updateInputVisibility() {
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        
        boolean showRRQuantum = selectedAlgorithm.equals("Round Robin");
        boolean showMLFQParams = selectedAlgorithm.equals("MLFQ");
        
        quantumLabel.setVisible(showRRQuantum);
        quantumField.setVisible(showRRQuantum);
        
        timeSlicesLabel.setVisible(showMLFQParams);
        timeSlicesField.setVisible(showMLFQParams);
        allotmentTimesLabel.setVisible(showMLFQParams);
        allotmentTimesField.setVisible(showMLFQParams);
    }

    private class RunSimulationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            processes.clear();
            metricsTextArea.setText("");
            avgTurnaroundLabel.setText("Average Turnaround Time: N/A");
            avgResponseLabel.setText("Average Response Time: N/A");
            ganttChartPanel.setGanttChart(new ArrayList<>());

            int numProc;
            try {
                numProc = Integer.parseInt(numProcessesField.getText());
                if (numProc <= 0) {
                    JOptionPane.showMessageDialog(OsfinalprojectGUI.this, "Number of processes must be positive.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(OsfinalprojectGUI.this, "Invalid number of processes.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (manualInputRadio.isSelected()) {
                for (int i = 0; i < numProc; i++) {
                    JPanel row = (JPanel) processInputPanel.getComponent(i);
                    JTextField atField = (JTextField) row.getComponent(1); // Arrival Time
                    JTextField btField = (JTextField) row.getComponent(3); // Burst Time
                    try {
                        int arrivalTime = Integer.parseInt(atField.getText());
                        int burstTime = Integer.parseInt(btField.getText());
                        processes.add(new Process(i, arrivalTime, burstTime)); // Add the process to the list
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(OsfinalprojectGUI.this, "Invalid Arrival or Burst Time for Process " + i, "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            } else { // Auto-generate
                Random random = new Random();
                for (int i = 0; i < numProc; i++) {
                    int arrivalTime = random.nextInt(10); // 0-9
                    int burstTime = random.nextInt(10) + 1; // 1-10
                    processes.add(new Process(i, arrivalTime, burstTime));
                }
            }

            // Create a deep copy of processes for each simulation run
            // This is crucial because scheduling algorithms modify process states
            List<Process> processesForSimulation = processes.stream()
                .map(p -> new Process(p.id, p.arrivalTime, p.burstTime))
                .collect(Collectors.toList());

            Scheduler scheduler = null;
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();

            try {
                switch (selectedAlgorithm) {
                    case "FIFO":
                        scheduler = new FIFO(processesForSimulation);
                        break;
                    case "SJF":
                        scheduler = new SJF(processesForSimulation);
                        break;
                    case "SRTF":
                        scheduler = new SRTF(processesForSimulation);
                        break;
                    case "Round Robin":
                        int rrQuantum = parseQuantum(quantumField.getText());
                        scheduler = new RoundRobin(processesForSimulation, rrQuantum);
                        break;
                    case "MLFQ":
                        int[] timeSlices = parseCommaSeparatedInts(timeSlicesField.getText(), "Time Slices");
                        int[] allotmentTimes = parseCommaSeparatedInts(allotmentTimesField.getText(), "Allotment Times");
                        scheduler = new MLFQ(processesForSimulation, timeSlices, allotmentTimes);
                        break;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(OsfinalprojectGUI.this, 
                    "Invalid input: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (IllegalArgumentException ex) { // Catch specific exceptions from scheduler constructors
                JOptionPane.showMessageDialog(OsfinalprojectGUI.this, 
                    "Configuration Error: " + ex.getMessage(), "Setup Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            if (scheduler != null) {
                // Redirect System.out to capture print statements
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                PrintStream old = System.out; // Store original System.out
                System.setOut(ps); // Redirect System.out to our stream

                try {
                    scheduler.schedule(); // Run the simulation
                } finally {
                    System.out.flush(); // Ensure all buffered output is written
                    System.setOut(old); // Restore original System.out
                }

                metricsTextArea.setText(baos.toString()); // Set the captured output to the text area

                // Update averages from the scheduler's calculated values
                double totalTurnaround = 0;
                double totalResponse = 0;
                int completedProcessesCount = 0;
                for (Process p : processesForSimulation) { // Use the processes from the simulation
                    if (p.completionTime > 0) { // Only count completed processes for averages
                        totalTurnaround += p.turnaroundTime;
                        totalResponse += p.responseTime;
                        completedProcessesCount++;
                    }
                }
                
                if (completedProcessesCount > 0) {
                    avgTurnaroundLabel.setText(String.format("Average Turnaround Time: %.2f", totalTurnaround / completedProcessesCount));
                    avgResponseLabel.setText(String.format("Average Response Time: %.2f", totalResponse / completedProcessesCount));
                } else {
                    avgTurnaroundLabel.setText("Average Turnaround Time: N/A");
                    avgResponseLabel.setText("Average Response Time: N/A");
                }

                // Update Gantt Chart
                ganttChartPanel.setGanttChart(scheduler.ganttChart);
            }
        }

        private int parseQuantum(String input) throws NumberFormatException {
            if (input.equalsIgnoreCase("INF")) {
                return Integer.MAX_VALUE;
            }
            int value = Integer.parseInt(input);
            if (value <= 0) {
                throw new NumberFormatException("Value must be positive");
            }
            return value;
        }

        private int[] parseCommaSeparatedInts(String input, String paramName) throws NumberFormatException {
            String[] parts = input.split(",");
            if (parts.length != 4) {
                throw new NumberFormatException(paramName + " requires exactly 4 comma-separated values");
            }
            int[] result = new int[4];
            for (int i = 0; i < 4; i++) {
                result[i] = parseQuantum(parts[i].trim());
            }
            return result;
        }
    }
}
