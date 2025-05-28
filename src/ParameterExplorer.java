import entity.World;
import entity.Patch;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import javax.swing.*;
import java.awt.*;

public class ParameterExplorer {
    // Predefined parameter sets
    private static final class ParameterSet {
        final double copDensity;
        final double agentDensity;
        final double legitimacy;
        final int jailTerm;
        final int vision;
        final String description;
        final double maxLocalRelated;  // New parameter for cop-times influence

        ParameterSet(double copDensity, double agentDensity, double legitimacy, 
                    int jailTerm, int vision, String description) {
            this(copDensity, agentDensity, legitimacy, jailTerm, vision, description, 10.0);
        }

        ParameterSet(double copDensity, double agentDensity, double legitimacy, 
                    int jailTerm, int vision, String description, double maxLocalRelated) {
            this.copDensity = copDensity;
            this.agentDensity = agentDensity;
            this.legitimacy = legitimacy;
            this.jailTerm = jailTerm;
            this.vision = vision;
            this.description = description;
            this.maxLocalRelated = maxLocalRelated;
        }
    }

    // Define representative parameter sets
    private static final ParameterSet[] PARAMETER_SETS = {
        // Baseline parameter set (original parameters)
        new ParameterSet(0.04, 0.7, 0.82, 30, 7, "Baseline"),
        
        // Single parameter variations
        new ParameterSet(0.08, 0.7, 0.82, 30, 7, "High Cop Density"),
        new ParameterSet(0.02, 0.7, 0.82, 30, 7, "Low Cop Density"),
        new ParameterSet(0.04, 0.8, 0.82, 30, 7, "High Agent Density"),
        new ParameterSet(0.04, 0.6, 0.82, 30, 7, "Low Agent Density"),
        new ParameterSet(0.04, 0.7, 0.9, 30, 7, "High Legitimacy"),
        new ParameterSet(0.04, 0.7, 0.7, 30, 7, "Low Legitimacy"),
        new ParameterSet(0.04, 0.7, 0.82, 40, 7, "Long Jail Term"),
        new ParameterSet(0.04, 0.7, 0.82, 20, 7, "Short Jail Term"),
        new ParameterSet(0.04, 0.7, 0.82, 30, 9, "Large Vision"),
        new ParameterSet(0.04, 0.7, 0.82, 30, 5, "Small Vision"),
        
        // Combined parameter variations - High repression scenario
        new ParameterSet(0.08, 0.7, 0.9, 40, 7, "High Repression"),
        
        // Combined parameter variations - Low control scenario
        new ParameterSet(0.02, 0.8, 0.7, 20, 5, "Low Control"),
        
        // Combined parameter variations - High tension scenario
        new ParameterSet(0.04, 0.8, 0.7, 30, 9, "High Tension"),
        
        // Combined parameter variations - Stable society scenario
        new ParameterSet(0.06, 0.6, 0.9, 30, 7, "Stable Society"),
        
        // Extreme scenarios
        new ParameterSet(0.09, 0.9, 0.6, 50, 9, "Extreme Unrest"),
        new ParameterSet(0.09, 0.5, 0.95, 50, 5, "Extreme Control"),

        // New parameter sets for local legitimacy testing
        new ParameterSet(0.04, 0.7, 0.82, 30, 7, "Local Legitimacy - High Cop Activity", 5.0),  // Stronger cop-times influence
        new ParameterSet(0.04, 0.7, 0.82, 30, 7, "Local Legitimacy - Low Cop Activity", 15.0) // Weaker cop-times influence
    };

    private static final int WORLD_SIZE = 40;
    private static final int SIMULATION_STEPS = 100;
    private static final int REPETITIONS = 1;

    // Add new statistics tracking
    private static class RebellionStats {
        int outbreakCount = 0;  // Number of times rebellion outbreak occurred
        int maxRebellionSize = 0;  // Maximum number of active agents
        int totalRebellionSteps = 0;  // Total steps with active agents
        List<Integer> rebellionSizes = new ArrayList<>();  // Track rebellion sizes over time
        List<Integer> activeCounts = new ArrayList<>();  // Track active counts over time
        List<Integer> jailedCounts = new ArrayList<>();  // Track jailed counts over time
        List<Integer> quietCounts = new ArrayList<>();  // Track quiet counts over time
    }

    public static void main(String[] args) {
        // Create Swing frame for parameter selection
        JFrame frame = new JFrame("Parameter Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JLabel label = new JLabel("Select parameter sets to run:");
        panel.add(label);

        // Create checkboxes for each parameter set
        JCheckBox[] checkboxes = new JCheckBox[PARAMETER_SETS.length];
        for (int i = 0; i < PARAMETER_SETS.length; i++) {
            checkboxes[i] = new JCheckBox(PARAMETER_SETS[i].description);
            panel.add(checkboxes[i]);
        }

        // Add run button
        JButton runButton = new JButton("Run Selected");
        panel.add(runButton);

        runButton.addActionListener(e -> {
            // Get selected parameter sets
            List<ParameterSet> selectedSets = new ArrayList<>();
            for (int i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i].isSelected()) {
                    selectedSets.add(PARAMETER_SETS[i]);
                }
            }

            if (selectedSets.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please select at least one parameter set.");
                return;
            }

            // Create results directory with timestamp
            String timestamp = String.valueOf(System.currentTimeMillis());
            String resultsDir = "parameter_exploration_results_" + timestamp;
            try {
                Files.createDirectories(Paths.get(resultsDir));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error creating results directory: " + ex.getMessage());
                return;
            }

            // Run selected parameter sets
            try (PrintWriter summaryWriter = new PrintWriter(new FileWriter(resultsDir + "/summary.csv"))) {
                summaryWriter.println("ExperimentID,Description,CopDensity,AgentDensity,Legitimacy,JailTerm,Vision," +
                        "AvgActive,AvgJailed,AvgQuiet,MaxActive,MaxJailed,MaxQuiet,MinActive,MinJailed,MinQuiet," +
                        "OutbreakCount,RebellionFrequency,AvgRebellionSize,MaxRebellionSize,TotalRebellionSteps," +
                        "StabilityIndex,RecoveryTime");

                // Store data for parameter interaction analysis
                List<Double> copDensities = new ArrayList<>();
                List<Double> legitimacies = new ArrayList<>();
                List<Double> stabilityIndices = new ArrayList<>();

                // Run each selected parameter set
                for (int i = 0; i < selectedSets.size(); i++) {
                    ParameterSet params = selectedSets.get(i);
                    RebellionStats stats = runExperiment(i + 1, params, summaryWriter, resultsDir);
                    
                    // Generate charts for this experiment
                    ChartGenerator.generateTimeSeriesChart(
                        stats.activeCounts,
                        stats.jailedCounts,
                        stats.quietCounts,
                        String.valueOf(i + 1),
                        resultsDir
                    );
                    
                    ChartGenerator.generateRebellionSizeChart(
                        stats.rebellionSizes,
                        String.valueOf(i + 1),
                        resultsDir
                    );

                    // Store data for parameter interaction analysis
                    if (!copDensities.contains(params.copDensity)) {
                        copDensities.add(params.copDensity);
                    }
                    if (!legitimacies.contains(params.legitimacy)) {
                        legitimacies.add(params.legitimacy);
                    }
                    stabilityIndices.add(calculateStabilityIndex(stats.activeCounts));
                }

                // Generate parameter interaction chart
                ChartGenerator.generateParameterInteractionChart(
                    copDensities.stream().sorted().collect(Collectors.toList()),
                    legitimacies.stream().sorted().collect(Collectors.toList()),
                    stabilityIndices,
                    resultsDir
                );

                JOptionPane.showMessageDialog(frame, "Simulation completed. Results saved in: " + resultsDir);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error writing summary file: " + ex.getMessage());
            }
        });

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static RebellionStats runExperiment(int experimentId, ParameterSet params, PrintWriter summaryWriter, String resultsDir) {
        System.out.printf("Running experiment %d (%s): cop=%.2f, agent=%.2f, leg=%.2f, jail=%d, vision=%d, maxLocal=%.1f%n",
                experimentId, params.description, params.copDensity, params.agentDensity, 
                params.legitimacy, params.jailTerm, params.vision, params.maxLocalRelated);

        RebellionStats stats = new RebellionStats();
        boolean inRebellion = false;
        int rebellionStartStep = 0;

        // Run simulation
        World world = new World(WORLD_SIZE, WORLD_SIZE);
        // Enable local legitimacy for the new parameter sets
        boolean useLocalLegitimacy = params.description.contains("Local Legitimacy");
        if (useLocalLegitimacy) {
            Patch.setMaxLocalRelated(params.maxLocalRelated);
        }
        world.setup(params.agentDensity, params.copDensity, 2.3, 0.1, params.legitimacy, useLocalLegitimacy);
        
        for (int step = 0; step < SIMULATION_STEPS; step++) {
            world.tick();
            
            int activeCount = world.getActiveAgentCount();
            int jailedCount = world.getJailedAgentCount();
            int quietCount = world.getQuietAgentCount();

            // Track rebellion statistics
            stats.activeCounts.add(activeCount);
            stats.jailedCounts.add(jailedCount);
            stats.quietCounts.add(quietCount);
            stats.rebellionSizes.add(activeCount);

            // Detect rebellion outbreaks
            if (activeCount > 0 && !inRebellion) {
                inRebellion = true;
                rebellionStartStep = step;
                stats.outbreakCount++;
            } else if (activeCount == 0 && inRebellion) {
                inRebellion = false;
                stats.totalRebellionSteps += (step - rebellionStartStep);
            }

            // Update maximum rebellion size
            if (activeCount > stats.maxRebellionSize) {
                stats.maxRebellionSize = activeCount;
            }
        }

        // Calculate additional statistics
        double rebellionFrequency = (double) stats.outbreakCount / SIMULATION_STEPS;
        double avgRebellionSize = stats.rebellionSizes.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        // Calculate stability index (lower means more stable)
        double stabilityIndex = calculateStabilityIndex(stats.activeCounts);

        // Calculate average recovery time
        double recoveryTime = stats.outbreakCount > 0 ? 
                (double) stats.totalRebellionSteps / stats.outbreakCount : 0;

        // Write detailed results to CSV
        summaryWriter.printf("%d,%s,%.2f,%.2f,%.2f,%d,%d," +
                "%.2f,%.2f,%.2f,%d,%d,%d,%d,%d,%d," +
                "%d,%.4f,%.2f,%d,%d,%.4f,%.2f%n",
                experimentId, params.description, params.copDensity, params.agentDensity,
                params.legitimacy, params.jailTerm, params.vision,
                calculateAverage(stats.activeCounts), calculateAverage(stats.jailedCounts),
                calculateAverage(stats.quietCounts),
                stats.activeCounts.stream().mapToInt(Integer::intValue).max().orElse(0),
                stats.jailedCounts.stream().mapToInt(Integer::intValue).max().orElse(0),
                stats.quietCounts.stream().mapToInt(Integer::intValue).max().orElse(0),
                stats.activeCounts.stream().mapToInt(Integer::intValue).min().orElse(0),
                stats.jailedCounts.stream().mapToInt(Integer::intValue).min().orElse(0),
                stats.quietCounts.stream().mapToInt(Integer::intValue).min().orElse(0),
                stats.outbreakCount, rebellionFrequency, avgRebellionSize,
                stats.maxRebellionSize, stats.totalRebellionSteps,
                stabilityIndex, recoveryTime);

        summaryWriter.flush();
        return stats;
    }

    private static double calculateAverage(List<Integer> values) {
        return values.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    private static double calculateStabilityIndex(List<Integer> activeCounts) {
        // Calculate the variance of active counts as a measure of stability
        double mean = calculateAverage(activeCounts);
        double variance = activeCounts.stream()
                .mapToDouble(x -> Math.pow(x - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }
} 