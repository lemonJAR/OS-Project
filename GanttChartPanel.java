/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.osfinalproject;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

class GanttChartPanel extends JPanel {
    private static final int BLOCK_WIDTH = 50;
    private static final int BLOCK_HEIGHT = 30;
    private static final int PANEL_PADDING = 20;
    private static final int ANIMATION_DELAY = 300;
    
    // Predefined color palette (10 distinct colors)
    private static final Color[] COLOR_PALETTE = {
        new Color(0, 102, 204),   // Blue
        new Color(204, 0, 0),     // Red
        new Color(0, 153, 51),    // Green
        new Color(229, 102, 255), // Purple
        new Color(255, 153, 0),   // Orange
        new Color(0, 204, 204),   // Cyan
        new Color(153, 51, 0),    // Brown
        new Color(255, 102, 178), // Pink
        new Color(102, 102, 102), // Gray
        new Color(204, 204, 0)    // Yellow
    };
    
    private List<Scheduler.GanttEntry> ganttChart;
    private Timer animationTimer;
    private int currentTime = 0;
    private int maxTime = 0;
    private JLabel timeLabel;
    
    public GanttChartPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        timeLabel = new JLabel("Time: 0", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(timeLabel, BorderLayout.NORTH);
        
        animationTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentTime++;
                timeLabel.setText("Time: " + currentTime);
                
                if (currentTime >= maxTime) {
                    animationTimer.stop();
                }
                
                repaint();
            }
        });
    }

    public void setGanttChart(List<Scheduler.GanttEntry> ganttChart) {
        this.ganttChart = ganttChart;
        
        if (ganttChart != null && !ganttChart.isEmpty()) {
            maxTime = ganttChart.get(ganttChart.size() - 1).endTime;
        } else {
            maxTime = 0;
        }
        
        currentTime = 0;
        timeLabel.setText("Time: " + currentTime);
        
        animationTimer.stop();
        animationTimer.start();
        
        repaint();
    }

    // Helper method to get consistent color for each process ID
    private Color getColorForProcess(int processId) {
        if (processId == -1) return Color.LIGHT_GRAY; // IDLE time
        
        // Use modulo to cycle through the color palette
        return COLOR_PALETTE[Math.abs(processId) % COLOR_PALETTE.length];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (ganttChart == null || ganttChart.isEmpty()) {
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int x = PANEL_PADDING;
        int y = PANEL_PADDING + 30;
        
        int totalWidth = BLOCK_WIDTH * maxTime;
        int availableWidth = getWidth() - 2 * PANEL_PADDING;
        float scale = 1.0f;
        
        if (totalWidth > availableWidth) {
            scale = (float) availableWidth / totalWidth;
        }
        
        // Draw time markers
        int scaledBlockWidth = (int) (BLOCK_WIDTH * scale);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        
        for (int t = 0; t <= maxTime; t++) {
            int markerX = x + t * scaledBlockWidth;
            g2d.drawLine(markerX, y, markerX, y + 5);
            
            if (t % 2 == 0 || t == maxTime) {
                String timeStr = Integer.toString(t);
                int strWidth = g2d.getFontMetrics().stringWidth(timeStr);
                g2d.drawString(timeStr, markerX - strWidth/2, y + 15);
            }
        }
        
        y += 20;
        
        // Draw process blocks
        for (Scheduler.GanttEntry entry : ganttChart) {
            Color blockColor = getColorForProcess(entry.processId);
            int drawStartTime = entry.startTime;
            int drawEndTime = entry.endTime;
            
            for (int t = drawStartTime; t < drawEndTime; t++) {
                int blockX = x + t * scaledBlockWidth;
                
                if (t <= currentTime) {
                    g2d.setColor(blockColor);
                    g2d.fillRect(blockX, y, scaledBlockWidth, BLOCK_HEIGHT);
                    
                    if (t == currentTime) {
                        g2d.setColor(new Color(255, 255, 0, 100));
                        g2d.fillRect(blockX, y, scaledBlockWidth, BLOCK_HEIGHT);
                    }
                } else {
                    g2d.setColor(blockColor);
                    g2d.drawRect(blockX, y, scaledBlockWidth, BLOCK_HEIGHT);
                }
                
                g2d.setColor(Color.BLACK);
                String blockText = entry.processId == -1 ? "IDLE" : ("P" + entry.processId);
                
                if (t <= currentTime) {
                    int textWidth = g2d.getFontMetrics().stringWidth(blockText);
                    int textX = blockX + (scaledBlockWidth - textWidth)/2;
                    int textY = y + BLOCK_HEIGHT/2 + 5;
                    g2d.drawString(blockText, textX, textY);
                }
            }
        }
        
        // Draw legend only if we have processes
        boolean hasProcesses = ganttChart.stream().anyMatch(entry -> entry.processId != -1);
        if (hasProcesses) {
            y += BLOCK_HEIGHT + 20;
            int legendX = PANEL_PADDING;
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            
            // Find all unique process IDs (excluding -1)
            Map<Integer, Boolean> uniqueProcesses = new TreeMap<>();
            for (Scheduler.GanttEntry entry : ganttChart) {
                if (entry.processId != -1) {
                    uniqueProcesses.put(entry.processId, true);
                }
            }
            
            // Draw legend items in order
            for (Integer processId : uniqueProcesses.keySet()) {
                Color color = getColorForProcess(processId);
                g2d.setColor(color);
                g2d.fillRect(legendX, y, 15, 15);
                
                g2d.setColor(Color.BLACK);
                g2d.drawString("P" + processId, legendX + 20, y + 12);
                
                legendX += 60;
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (ganttChart == null || ganttChart.isEmpty()) {
            return new Dimension(300, 150);
        }
        
        return new Dimension(
            Math.max(BLOCK_WIDTH * maxTime + 2 * PANEL_PADDING, 500),
            250
        );
    }
}
