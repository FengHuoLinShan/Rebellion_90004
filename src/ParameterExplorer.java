import entity.World;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ParameterExplorer {
    // Predefined parameter sets
    private static final class ParameterSet {
        final double copDensity;
        final double agentDensity;
        final double legitimacy;
        final int jailTerm;
        final int vision;
        final String description;

        ParameterSet(double copDensity, double agentDensity, double legitimacy, 
                    int jailTerm, int vision, String description) {
            this.copDensity = copDensity;
            this.agentDensity = agentDensity;
            this.legitimacy = legitimacy;
            this.jailTerm = jailTerm;
            this.vision = vision;
            this.description = description;
        }
    }

    // Define representative parameter sets
    private static final ParameterSet[] PARAMETER_SETS = {
        // Baseline parameter set (original parameters)
        new ParameterSet(0.04, 0.7, 0.82, 30, 3, "Baseline"),
        
        // High cop density
        new ParameterSet(0.08, 0.7, 0.82, 30, 3, "High Cop Density"),
        
        // Low government legitimacy
        new ParameterSet(0.04, 0.7, 0.7, 30, 3, "Low Government Legitimacy"),
        
        // High agent density
        new ParameterSet(0.04, 0.8, 0.82, 30, 3, "High Agent Density"),
        
        // Long jail term
        new ParameterSet(0.04, 0.7, 0.82, 40, 3, "Long Jail Term"),
        
        // Large vision
        new ParameterSet(0.04, 0.7, 0.82, 30, 4, "Large Vision")
    };

    private static final int WORLD_SIZE = 40;
    private static final int SIMULATION_STEPS = 100;
    private static final int REPETITIONS = 1; // Set to 1 as requested

    public static void main(String[] args) {
        // Create results directory
        try {
            Files.createDirectories(Paths.get("parameter_exploration_results"));
        } catch (IOException e) {
            System.err.println("Error creating results directory: " + e.getMessage());
            return;
        }

        // Create summary report file
        try (PrintWriter summaryWriter = new PrintWriter(new FileWriter("parameter_exploration_results/summary.csv"))) {
            summaryWriter.println("ExperimentID,Description,CopDensity,AgentDensity,Legitimacy,JailTerm,Vision,AvgActive,AvgJailed,AvgQuiet,MaxActive,MaxJailed,MaxQuiet,MinActive,MinJailed,MinQuiet");
            
            // Run each parameter set
            for (int i = 0; i < PARAMETER_SETS.length; i++) {
                ParameterSet params = PARAMETER_SETS[i];
                runExperiment(i + 1, params, summaryWriter);
            }
        } catch (IOException e) {
            System.err.println("Error writing summary file: " + e.getMessage());
        }
    }

    private static void runExperiment(int experimentId, ParameterSet params, PrintWriter summaryWriter) {
        System.out.printf("Running experiment %d (%s): cop=%.2f, agent=%.2f, leg=%.2f, jail=%d, vision=%d%n",
                experimentId, params.description, params.copDensity, params.agentDensity, 
                params.legitimacy, params.jailTerm, params.vision);

        List<Integer> totalActive = new ArrayList<>();
        List<Integer> totalJailed = new ArrayList<>();
        List<Integer> totalQuiet = new ArrayList<>();

        // Repeat experiment
        for (int rep = 0; rep < REPETITIONS; rep++) {
            World world = new World(WORLD_SIZE, WORLD_SIZE);
            world.setup(params.agentDensity, params.copDensity, 2.3, 0.1, params.legitimacy);
            
            // Run simulation
            for (int step = 0; step < SIMULATION_STEPS; step++) {
                world.tick();
                
                // Record results for each step
                totalActive.add(world.getActiveAgentCount());
                totalJailed.add(world.getJailedAgentCount());
                totalQuiet.add(world.getQuietAgentCount());
            }
        }

        // Calculate statistics
        double avgActive = totalActive.stream().mapToInt(Integer::intValue).average().orElse(0);
        double avgJailed = totalJailed.stream().mapToInt(Integer::intValue).average().orElse(0);
        double avgQuiet = totalQuiet.stream().mapToInt(Integer::intValue).average().orElse(0);
        
        int maxActive = totalActive.stream().mapToInt(Integer::intValue).max().orElse(0);
        int maxJailed = totalJailed.stream().mapToInt(Integer::intValue).max().orElse(0);
        int maxQuiet = totalQuiet.stream().mapToInt(Integer::intValue).max().orElse(0);
        
        int minActive = totalActive.stream().mapToInt(Integer::intValue).min().orElse(0);
        int minJailed = totalJailed.stream().mapToInt(Integer::intValue).min().orElse(0);
        int minQuiet = totalQuiet.stream().mapToInt(Integer::intValue).min().orElse(0);

        // Write to summary report
        summaryWriter.printf("%d,%s,%.2f,%.2f,%.2f,%d,%d,%.2f,%.2f,%.2f,%d,%d,%d,%d,%d,%d%n",
                experimentId, params.description, params.copDensity, params.agentDensity,
                params.legitimacy, params.jailTerm, params.vision,
                avgActive, avgJailed, avgQuiet,
                maxActive, maxJailed, maxQuiet,
                minActive, minJailed, minQuiet);
        summaryWriter.flush();
    }
} 