/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.osfinalproject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GanttChartPanel extends JPanel {

    private List<Scheduler.GanttEntry> ganttEntries;
    private int maxTime = 0;
    private int maxProcessId = 0;

    public GanttChartPanel() {
        this.ganttEntries = new ArrayList<>();
        setBackground(Color.WHITE);
    }

    public void setGanttEntries(List<Scheduler.GanttEntry> ganttEntries) {
        this.ganttEntries = ganttEntries;
        maxTime = 0;
        maxProcessId = 0;
        if (!ganttEntries.isEmpty()) {
            maxTime = ganttEntries.get(ganttEntries.size() - 1).endTime;
            for (Scheduler.GanttEntry entry : ganttEntries) {
                if (entry.processId != -1) { // Exclude idle
                    maxProcessId = Math.max(maxProcessId, entry.processId);
                }
            }
        }
        repaint(); // Redraw the panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (ganttEntries.isEmpty()) {
            g.setColor(Color.BLACK);
            //g.drawString("Gantt Chart will appear here.", 10, 20);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int topMargin = 30;
        int bottomMargin = 30;
        int leftMargin = 40;
        int rightMargin = 10;

        int chartWidth = panelWidth - leftMargin - rightMargin;
        int chartHeight = panelHeight - topMargin - bottomMargin;

        // Calculate scale
        double timeScale = (double) chartWidth / maxTime;
        int barHeight = 30; // Height of each process bar
        int yOffset = topMargin + 10; // Starting Y position for bars

        // Draw time axis
        g2d.setColor(Color.BLACK);
        g2d.drawLine(leftMargin, yOffset + barHeight, leftMargin + chartWidth, yOffset + barHeight);

        // Draw time labels and ticks
        int tickInterval = Math.max(1, maxTime / 10); // Adjust tick frequency
        for (int i = 0; i <= maxTime; i += tickInterval) {
            int x = leftMargin + (int) (i * timeScale);
            g2d.drawLine(x, yOffset + barHeight, x, yOffset + barHeight + 5); // Tick mark
            g2d.drawString(String.valueOf(i), x - (g.getFontMetrics().stringWidth(String.valueOf(i)) / 2), yOffset + barHeight + 20); // Label
        }

        // Draw Gantt bars
        for (Scheduler.GanttEntry entry : ganttEntries) {
            int xStart = leftMargin + (int) (entry.startTime * timeScale);
            int xEnd = leftMargin + (int) (entry.endTime * timeScale);
            int width = xEnd - xStart;

            if (entry.processId == -1) {
                g2d.setColor(Color.LIGHT_GRAY); // Idle time
                g2d.fillRect(xStart, yOffset, width, barHeight);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(xStart, yOffset, width, barHeight);
                g2d.drawString("Idle", xStart + 5, yOffset + barHeight / 2 + 5);
            } else {
                // Use a color based on process ID for distinction
                Color processColor = new Color(
                    (entry.processId * 50 % 255),
                    (entry.processId * 100 % 255),
                    (entry.processId * 150 % 255)
                ).brighter();
                g2d.setColor(processColor);
                g2d.fillRect(xStart, yOffset, width, barHeight);
                g2d.setColor(Color.WHITE);
                g2d.drawRect(xStart, yOffset, width, barHeight);
                g2d.drawString("P" + entry.processId, xStart + 5, yOffset + barHeight / 2 + 5);
            }
        }
    }
}
