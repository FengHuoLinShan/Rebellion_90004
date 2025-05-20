import entity.World;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ParameterExplorer {
    // 预定义的参数组合
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

    // 定义几组有代表性的参数组合
    private static final ParameterSet[] PARAMETER_SETS = {
        // 基准参数组（原始参数）
        new ParameterSet(0.04, 0.7, 0.82, 30, 3, "基准参数组"),
        
        // 高警察密度组
        new ParameterSet(0.08, 0.7, 0.82, 30, 3, "高警察密度组"),
        
        // 低政府合法性组
        new ParameterSet(0.04, 0.7, 0.7, 30, 3, "低政府合法性组"),
        
        // 高代理密度组
        new ParameterSet(0.04, 0.8, 0.82, 30, 3, "高代理密度组"),
        
        // 长监禁时间组
        new ParameterSet(0.04, 0.7, 0.82, 40, 3, "长监禁时间组"),
        
        // 大视野范围组
        new ParameterSet(0.04, 0.7, 0.82, 30, 4, "大视野范围组")
    };

    private static final int WORLD_SIZE = 40;
    private static final int SIMULATION_STEPS = 100;
    private static final int REPETITIONS = 5; // 增加重复次数以提高可靠性

    public static void main(String[] args) {
        // 创建结果目录
        try {
            Files.createDirectories(Paths.get("parameter_exploration_results"));
        } catch (IOException e) {
            System.err.println("Error creating results directory: " + e.getMessage());
            return;
        }

        // 创建汇总报告文件
        try (PrintWriter summaryWriter = new PrintWriter(new FileWriter("parameter_exploration_results/summary.csv"))) {
            summaryWriter.println("ExperimentID,Description,CopDensity,AgentDensity,Legitimacy,JailTerm,Vision,AvgActive,AvgJailed,AvgQuiet,MaxActive,MaxJailed,MaxQuiet,MinActive,MinJailed,MinQuiet");
            
            // 运行每组参数组合
            for (int i = 0; i < PARAMETER_SETS.length; i++) {
                ParameterSet params = PARAMETER_SETS[i];
                runExperiment(i + 1, params, summaryWriter);
            }
        } catch (IOException e) {
            System.err.println("Error writing summary file: " + e.getMessage());
        }
    }

    private static void runExperiment(int experimentId, ParameterSet params, PrintWriter summaryWriter) {
        System.out.printf("运行实验 %d (%s): cop=%.2f, agent=%.2f, leg=%.2f, jail=%d, vision=%d%n",
                experimentId, params.description, params.copDensity, params.agentDensity, 
                params.legitimacy, params.jailTerm, params.vision);

        List<Integer> totalActive = new ArrayList<>();
        List<Integer> totalJailed = new ArrayList<>();
        List<Integer> totalQuiet = new ArrayList<>();

        // 重复运行实验
        for (int rep = 0; rep < REPETITIONS; rep++) {
            World world = new World(WORLD_SIZE, WORLD_SIZE);
            world.setup(params.agentDensity, params.copDensity, 2.3, 0.1, params.legitimacy);
            
            // 运行模拟
            for (int step = 0; step < SIMULATION_STEPS; step++) {
                world.tick();
                
                // 记录每一步的结果，而不仅仅是最后一步
                totalActive.add(world.getActiveAgentCount());
                totalJailed.add(world.getJailedAgentCount());
                totalQuiet.add(world.getQuietAgentCount());
            }
        }

        // 计算统计数据
        double avgActive = totalActive.stream().mapToInt(Integer::intValue).average().orElse(0);
        double avgJailed = totalJailed.stream().mapToInt(Integer::intValue).average().orElse(0);
        double avgQuiet = totalQuiet.stream().mapToInt(Integer::intValue).average().orElse(0);
        
        int maxActive = totalActive.stream().mapToInt(Integer::intValue).max().orElse(0);
        int maxJailed = totalJailed.stream().mapToInt(Integer::intValue).max().orElse(0);
        int maxQuiet = totalQuiet.stream().mapToInt(Integer::intValue).max().orElse(0);
        
        int minActive = totalActive.stream().mapToInt(Integer::intValue).min().orElse(0);
        int minJailed = totalJailed.stream().mapToInt(Integer::intValue).min().orElse(0);
        int minQuiet = totalQuiet.stream().mapToInt(Integer::intValue).min().orElse(0);

        // 写入汇总报告
        summaryWriter.printf("%d,%s,%.2f,%.2f,%.2f,%d,%d,%.2f,%.2f,%.2f,%d,%d,%d,%d,%d,%d%n",
                experimentId, params.description, params.copDensity, params.agentDensity,
                params.legitimacy, params.jailTerm, params.vision,
                avgActive, avgJailed, avgQuiet,
                maxActive, maxJailed, maxQuiet,
                minActive, minJailed, minQuiet);
        summaryWriter.flush();
    }
} 