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

public class OsfinalprojectGUI extends JFrame {

    // Input Components
    private JRadioButton manualInputRadio, autoGenerateRadio;
    private ButtonGroup inputMethodGroup;
    private JTextField numProcessesField;
    private JPanel processInputPanel; // For manual process details
    private JComboBox<String> algorithmComboBox;
    private JTextField quantumField; // For RR and MLFQ quantums
    private JLabel quantumLabel;
    private JButton runSimulationButton;

    // Output Components
    private JTextArea metricsTextArea;
    private JLabel avgTurnaroundLabel, avgResponseLabel;
    private GanttChartPanel ganttChartPanel; // Custom panel for drawing Gantt chart

    private List<Process> processes; // List to hold processes for simulation

    public OsfinalprojectGUI() {
        super("OS Scheduling Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(1000, 700);
        setLocationRelativeTo(null); // Center the window

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
        autoGenerateRadio.setSelected(true); // Default selection

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

        // Manual Process Input Panel (initially hidden/empty)
        processInputPanel = new JPanel();
        processInputPanel.setLayout(new BoxLayout(processInputPanel, BoxLayout.Y_AXIS));
        processInputPanel.setBorder(BorderFactory.createTitledBorder("Process Details (PID, Arrival, Burst)"));
        JScrollPane scrollPane = new JScrollPane(processInputPanel);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.weighty = 0.5; // Allow vertical expansion
        inputPanel.add(scrollPane, gbc);
        gbc.weighty = 0; // Reset

        // Algorithm Selection
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        inputPanel.add(new JLabel("Select Algorithm:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        String[] algorithms = {"FIFO", "SJF", "SRTF", "Round Robin", "MLFQ"};
        algorithmComboBox = new JComboBox<>(algorithms);
        inputPanel.add(algorithmComboBox, gbc);

        // Quantum Input
        gbc.gridx = 0; gbc.gridy = 4;
        quantumLabel = new JLabel("Time Quantum:");
        inputPanel.add(quantumLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        quantumField = new JTextField("2", 5); // Default for RR
        inputPanel.add(quantumField, gbc);

        // Run Simulation Button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        runSimulationButton = new JButton("Run Simulation");
        inputPanel.add(runSimulationButton, gbc);

        add(inputPanel, BorderLayout.WEST);

        // --- Output Panel ---
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Results"));

        // Metrics Area
        metricsTextArea = new JTextArea(10, 40);
        metricsTextArea.setEditable(false);
        metricsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputPanel.add(new JScrollPane(metricsTextArea), BorderLayout.NORTH);

        // Averages
        JPanel avgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        avgTurnaroundLabel = new JLabel("Average Turnaround Time: N/A");
        avgResponseLabel = new JLabel("Average Response Time: N/A");
        avgPanel.add(avgTurnaroundLabel);
        avgPanel.add(Box.createHorizontalStrut(20)); // Spacer
        avgPanel.add(avgResponseLabel);
        outputPanel.add(avgPanel, BorderLayout.CENTER);

        // Gantt Chart Panel
        ganttChartPanel = new GanttChartPanel();
        ganttChartPanel.setPreferredSize(new Dimension(600, 200));
        ganttChartPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        outputPanel.add(ganttChartPanel, BorderLayout.SOUTH);

        add(outputPanel, BorderLayout.CENTER);

        // --- Event Listeners ---
        manualInputRadio.addActionListener(e -> toggleInputMethod(true));
        autoGenerateRadio.addActionListener(e -> toggleInputMethod(false));
        algorithmComboBox.addActionListener(e -> updateQuantumFieldVisibility());
        runSimulationButton.addActionListener(new RunSimulationListener());

        // Initial state setup
        toggleInputMethod(false); // Start with auto-generate
        updateQuantumFieldVisibility();
    }

    private void toggleInputMethod(boolean isManual) {
        processInputPanel.removeAll();
        if (isManual) {
            int numProc;
            try {
                numProc = Integer.parseInt(numProcessesField.getText());
            } catch (NumberFormatException ex) {
                numProc = 0; // Or show error
            }
            for (int i = 0; i < numProc; i++) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                row.add(new JLabel("P" + i + ": AT="));
                row.add(new JTextField(String.valueOf(0), 3)); // Arrival Time
                row.add(new JLabel("BT="));
                row.add(new JTextField(String.valueOf(5), 3)); // Burst Time
                processInputPanel.add(row);
            }
            processInputPanel.revalidate();
            processInputPanel.repaint();
        }
        // For auto-generate, processInputPanel remains empty
    }

    private void updateQuantumFieldVisibility() {
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        boolean showQuantum = selectedAlgorithm.equals("Round Robin") || selectedAlgorithm.equals("MLFQ");
        quantumLabel.setVisible(showQuantum);
        quantumField.setVisible(showQuantum);
        if (selectedAlgorithm.equals("MLFQ")) {
            quantumLabel.setText("Quantums (Q0,Q1,Q2,Q3):");
            quantumField.setText("2,4,8,16"); // Default MLFQ quantums
        } else {
            quantumLabel.setText("Time Quantum:");
            quantumField.setText("2"); // Default RR quantum
        }
    }

    private class RunSimulationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            processes.clear(); // Clear previous processes
            metricsTextArea.setText("");
            avgTurnaroundLabel.setText("Average Turnaround Time: N/A");
            avgResponseLabel.setText("Average Response Time: N/A");
            ganttChartPanel.setGanttChart(new ArrayList<>()); // Clear Gantt chart

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
                    JTextField atField = (JTextField) row.getComponent(2); // Assuming order: Label, AT_Field, Label, BT_Field
                    JTextField btField = (JTextField) row.getComponent(4);
                    try {
                        int arrivalTime = Integer.parseInt(atField.getText());
                        int burstTime = Integer.parseInt(btField.getText());
                        if (arrivalTime < 0 || burstTime <= 0) {
                             JOptionPane.showMessageDialog(OsfinalprojectGUI.this, "Arrival time cannot be negative, Burst time must be positive.", "Input Error", JOptionPane.ERROR_MESSAGE);
                             return;
                        }
                        processes.add(new Process(i, arrivalTime, burstTime));
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
                        int rrQuantum = Integer.parseInt(quantumField.getText());
                        if (rrQuantum <= 0) {
                            JOptionPane.showMessageDialog(OsfinalprojectGUI.this, "Time quantum must be positive.", "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        scheduler = new RoundRobin(processesForSimulation, rrQuantum);
                        break;
                    case "MLFQ":
                        String[] quantumStrings = quantumField.getText().split(",");
                        if (quantumStrings.length != 4) {
                            JOptionPane.showMessageDialog(OsfinalprojectGUI.this, "MLFQ requires 4 comma-separated quantums (Q0,Q1,Q2,Q3).", "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        int[] mlfqQuantums = new int[4];
                        for (int i = 0; i < 4; i++) {
                            mlfqQuantums[i] = Integer.parseInt(quantumStrings[i].trim());
                            if (mlfqQuantums[i] <= 0) {
                                JOptionPane.showMessageDialog(OsfinalprojectGUI.this, "MLFQ quantums must be positive.", "Input Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        scheduler = new MLFQ(processesForSimulation, mlfqQuantums);
                        break;
                    default:
                        JOptionPane.showMessageDialog(OsfinalprojectGUI.this, "Unknown algorithm selected.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(OsfinalprojectGUI.this, "Invalid quantum value(s). Please enter integers.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(OsfinalprojectGUI.this, "An error occurred during scheduler initialization: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
                return;
            }

            if (scheduler != null) {
                // Redirect System.out to capture print statements
                // This is a bit hacky but allows reusing existing printMetrics/printAverages
                // A better approach would be to modify Scheduler to return data structures
                // instead of printing directly.
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                java.io.PrintStream ps = new java.io.PrintStream(baos);
                java.io.PrintStream old = System.out;
                System.setOut(ps);

                scheduler.schedule();

                System.out.flush();
                System.setOut(old); // Restore System.out

                metricsTextArea.setText(baos.toString());

                // Update averages from the scheduler's calculated values
                double totalTurnaround = 0;
                double totalResponse = 0;
                for (Process p : processesForSimulation) { // Use the processes from the simulation
                    totalTurnaround += p.turnaroundTime;
                    totalResponse += p.responseTime;
                }
                avgTurnaroundLabel.setText(String.format("Average Turnaround Time: %.2f", totalTurnaround / processesForSimulation.size()));
                avgResponseLabel.setText(String.format("Average Response Time: %.2f", totalResponse / processesForSimulation.size()));

                // Update Gantt Chart
                ganttChartPanel.setGanttChart(scheduler.ganttChart);
            }
        }
    }

}

