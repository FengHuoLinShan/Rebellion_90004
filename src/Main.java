/**
 * Main class for running the rebellion simulation model.
 * This class provides a simple GUI interface for running the simulation
 * and displaying real-time results.
 */
import entity.World;
import static entity.AppConfig.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    /**
     * Main method that initializes and runs the simulation.
     * Creates a GUI window to display the simulation results in real-time.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Create world
        World world = new World(40, 40);

        // Initialize world
        world.setup(
            INITIAL_AGENT_DENSITY,
            INITIAL_COP_DENSITY,
            K,
            THRESHOLD,
            GOVERNMENT_LEGITIMACY,
            false  // Default to not using local legitimacy
        );

        // Lists to store statistics
        List<Integer> quietList = new ArrayList<>();
        List<Integer> jailedList = new ArrayList<>();
        List<Integer> activeList = new ArrayList<>();

        // Create and show chart window
        SimpleLineChart chartPanel = new SimpleLineChart(quietList, jailedList, activeList);
        JFrame frame = new JFrame("Simulation Result");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Simulation parameters
        int steps = 200; // Total number of simulation steps
        int delay = 0;   // Delay between steps in milliseconds

        // Create CSV file and write header
        try {
            Files.write(Paths.get("simulation_results.csv"), 
                "TimeStep,ActiveAgents,JailedAgents,QuietAgents,TotalCops\n".getBytes());
        } catch (IOException ex) {
            System.err.println("Error creating CSV file: " + ex.getMessage());
            return;
        }

        // Timer for dynamic update
        Timer timer = new Timer(delay, e -> {
            if (quietList.size() < steps) {
                world.tick();
                int activeCount = world.getActiveAgentCount();
                int jailedCount = world.getJailedAgentCount();
                int quietCount = world.getQuietAgentCount();
                int copsCount = world.getCopsCount();

                // Add to lists
                quietList.add(quietCount);
                jailedList.add(jailedCount);
                activeList.add(activeCount);

                // Print to console
                System.out.println("Time Step: " + (quietList.size() - 1));
                world.printStatus();

                // Write to CSV
                try {
                    String line = String.format("%d,%d,%d,%d,%d\n",
                        quietList.size() - 1,
                        activeCount,
                        jailedCount,
                        quietCount,
                        copsCount
                    );
                    Files.write(Paths.get("simulation_results.csv"), 
                        line.getBytes(), 
                        java.nio.file.StandardOpenOption.APPEND);
                } catch (IOException ex) {
                    System.err.println("Error writing to CSV: " + ex.getMessage());
                }

                chartPanel.repaint();
            } else {
                ((Timer) e.getSource()).stop();
                System.out.println("Simulation finished.");
                
                // Calculate and write final statistics
                try {
                    int maxActive = activeList.stream().mapToInt(Integer::intValue).max().orElse(0);
                    int minActive = activeList.stream().mapToInt(Integer::intValue).min().orElse(0);
                    double avgActive = activeList.stream().mapToInt(Integer::intValue).average().orElse(0);
                    
                    int maxJailed = jailedList.stream().mapToInt(Integer::intValue).max().orElse(0);
                    int minJailed = jailedList.stream().mapToInt(Integer::intValue).min().orElse(0);
                    double avgJailed = jailedList.stream().mapToInt(Integer::intValue).average().orElse(0);
                    
                    int maxQuiet = quietList.stream().mapToInt(Integer::intValue).max().orElse(0);
                    int minQuiet = quietList.stream().mapToInt(Integer::intValue).min().orElse(0);
                    double avgQuiet = quietList.stream().mapToInt(Integer::intValue).average().orElse(0);
                    
                    // Write statistics to CSV
                    String statsHeader = "\nStatistics\n";
                    String statsContent = String.format(
                        "Category,Max,Min,Average\n" +
                        "Active,%d,%d,%.2f\n" +
                        "Jailed,%d,%d,%.2f\n" +
                        "Quiet,%d,%d,%.2f\n",
                        maxActive, minActive, avgActive,
                        maxJailed, minJailed, avgJailed,
                        maxQuiet, minQuiet, avgQuiet
                    );
                    
                    Files.write(Paths.get("simulation_results.csv"), 
                        (statsHeader + statsContent).getBytes(), 
                        java.nio.file.StandardOpenOption.APPEND);
                    
                    System.out.println("Statistics have been added to simulation_results.csv");
                } catch (IOException ex) {
                    System.err.println("Error writing statistics to CSV: " + ex.getMessage());
                }
            }
        });
        timer.start();
    }
}

/**
 * Custom chart panel for displaying simulation results in real-time.
 * Shows the number of active, jailed, and quiet agents over time.
 */
class SimpleLineChart extends JPanel {
    private final List<Integer> quiet, jailed, active;

    /**
     * Constructor for SimpleLineChart.
     * @param quiet List of quiet agent counts
     * @param jailed List of jailed agent counts
     * @param active List of active agent counts
     */
    public SimpleLineChart(List<Integer> quiet, List<Integer> jailed, List<Integer> active) {
        this.quiet = quiet;
        this.jailed = jailed;
        this.active = active;
        setPreferredSize(new Dimension(600, 400));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        
        // Find maximum value for scaling
        int max = Math.max(Math.max(
            quiet.stream().max(Integer::compareTo).orElse(1),
            jailed.stream().max(Integer::compareTo).orElse(1)),
            active.stream().max(Integer::compareTo).orElse(1));

        // Draw axes
        g.setColor(Color.DARK_GRAY);
        g.drawLine(40, h-40, w-20, h-40); // x-axis
        g.drawLine(40, h-40, 40, 20);     // y-axis

        // Draw axis labels
        g.setColor(Color.BLACK);
        g.drawString("0", 25, h-35);
        g.drawString(String.valueOf(max), 5, 30);
        g.drawString("time", w/2, h-10);
        g.drawString("agents", 5, h/2);

        // Draw data lines
        drawLine(g, quiet, Color.GREEN, w, h, max);
        drawLine(g, jailed, Color.BLACK, w, h, max);
        drawLine(g, active, Color.RED, w, h, max);

        // Draw legend
        g.setColor(Color.GREEN); g.drawString("quiet", w-80, 30);
        g.setColor(Color.BLACK); g.drawString("jailed", w-80, 45);
        g.setColor(Color.RED);   g.drawString("active", w-80, 60);
    }

    /**
     * Draws a line representing a data series.
     * @param g Graphics context
     * @param data List of data points
     * @param color Color to use for the line
     * @param w Width of the panel
     * @param h Height of the panel
     * @param max Maximum value for scaling
     */
    private void drawLine(Graphics g, List<Integer> data, Color color, int w, int h, int max) {
        g.setColor(color);
        int n = data.size();
        for (int i = 1; i < n; i++) {
            int x1 = 40 + (w-60) * (i-1) / (n-1);
            int y1 = h-40 - (h-60) * data.get(i-1) / max;
            int x2 = 40 + (w-60) * i / (n-1);
            int y2 = h-40 - (h-60) * data.get(i) / max;
            g.drawLine(x1, y1, x2, y2);
        }
    }
}