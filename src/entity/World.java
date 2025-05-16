package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import static entity.AppConfig.*;

//初始化各地块和各turtle
public class World {
    private Patch[][] grid;
    private List<Agent> agents;
    private List<Cop> cops;
    private Random random;
    private double k;
    private double threshold;
    private double government_legitimacy;

    //参数写到main

    // 初始化 grid、agents、cops，构建邻域
    public World(int width, int height) {
        this.grid = new Patch[width][height];
        this.agents = new ArrayList<>();
        this.cops = new ArrayList<>();
        this.random = new Random();
        
        // 初始化网格
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = new Patch(new Location(i, j));
            }
        }
    }
    // 创建 agent/cop，随机放置到空地块
    public void setup(double agentDensity, double copDensity, double k, double threshold, double government_legitimacy) {
        this.k = k;
        this.threshold = threshold;
        this.government_legitimacy = government_legitimacy;

        int totalPatches = grid.length * grid[0].length;
        int numAgents = (int) (agentDensity * totalPatches);
        int numCops = (int) (copDensity * totalPatches);

        // 创建警察
        for (int i = 0; i < numCops; i++) {
            Location randomLoc = getRandomEmptyLocation();
            if (randomLoc != null) {
                cops.add(new Cop(randomLoc));
            }
        }

        // 创建代理
        for (int i = 0; i < numAgents; i++) {
            Location randomLoc = getRandomEmptyLocation();
            if (randomLoc != null) {
                double riskAversion = random.nextDouble();
                double perceivedHardship = random.nextDouble();
                agents.add(new Agent(randomLoc, riskAversion, perceivedHardship));
            }
        }
    }

    private Location getRandomEmptyLocation() {
        List<Location> emptyLocations = new ArrayList<>();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (isLocationEmpty(new Location(i, j))) {
                    emptyLocations.add(new Location(i, j));
                }
            }
        }
        return emptyLocations.isEmpty() ? null : emptyLocations.get(random.nextInt(emptyLocations.size()));
    }

    private boolean isLocationEmpty(Location loc) {
        return !agents.stream().anyMatch(a -> a.getLocation().equals(loc)) &&
               !cops.stream().anyMatch(c -> c.getLocation().equals(loc));
    }

    // 移动、更新状态、执法、坐牢减刑
    public void tick() {
        // 移动代理和警察
        for (Agent agent : agents) {
            if (!agent.isJailed()) {
                agent.moveTo(getRandomEmptyLocationInVision(agent.getLocation()));
            }
        }
        for (Cop cop : cops) {
            cop.moveTo(getRandomEmptyLocationInVision(cop.getLocation()));
        }

        // 更新代理状态
        for (Agent agent : agents) {
            if (!agent.isJailed()) {
                agent.beingActive(threshold, government_legitimacy, this);
            }
        }

        // 警察执法
        for (Cop cop : cops) {
            Optional<Agent> suspect = getRandomActiveAgentInNeighborhood(cop.getLocation());
            if (suspect.isPresent()) {
                Agent agent = suspect.get();
                cop.moveTo(agent.getLocation());
                agent.setActive(false);
                agent.setJailTerm(random.nextInt(MAX_JAIL_TERM) + 1);
            }
        }

        // 减少监禁时间
        for (Agent agent : agents) {
            if (agent.isJailed()) {
                agent.decre_Jail_term(agent.getJail_term());
            }
        }
    }

    private Location getRandomEmptyLocationInVision(Location center) {
        List<Location> validLocations = new ArrayList<>();
        for (int i = -VISION; i <= VISION; i++) {
            for (int j = -VISION; j <= VISION; j++) {
                Location newLoc = new Location(
                    (center.getX() + i + grid.length) % grid.length,
                    (center.getY() + j + grid[0].length) % grid[0].length
                );
                if (isLocationEmpty(newLoc)) {
                    validLocations.add(newLoc);
                }
            }
        }
        return validLocations.isEmpty() ? center : validLocations.get(random.nextInt(validLocations.size()));
    }

    //计算neighbor上的cops数
    //todo: 定义neighbor计算方式
    public int copsOnNeighborhood(Location location){
        int c = 0;
        for (Cop cop : cops) {
            if (isInVision(location, cop.getLocation())) {
                c++;
            }
        }
        return c;
    }

    //计算neighbor上的ActiveAgents数
    public int countActiveAgentsInNeighborhood(Location t) {
        return (int) agents.stream()
            .filter(a -> !a.isJailed() && a.isActive() && isInVision(t, a.getLocation()))
            .count();
    }

    //寻找vision内叛乱分子，没有则返回null
    public Optional<Agent> getRandomActiveAgentInNeighborhood(Location location) {
        List<Agent> activeAgents = agents.stream()
            .filter(a -> !a.isJailed() && a.isActive() && isInVision(location, a.getLocation()))
            .toList();
        return activeAgents.isEmpty() ? Optional.empty() : Optional.of(activeAgents.get(random.nextInt(activeAgents.size())));
    }

    private boolean isInVision(Location loc1, Location loc2) {
        int dx = Math.abs(loc1.getX() - loc2.getX());
        int dy = Math.abs(loc1.getY() - loc2.getY());
        dx = Math.min(dx, grid.length - dx);
        dy = Math.min(dy, grid[0].length - dy);
        return dx <= VISION && dy <= VISION;
    }

    // Add statistics methods
    public int getActiveAgentCount() {
        return (int) agents.stream()
            .filter(a -> !a.isJailed() && a.isActive())
            .count();
    }

    public int getJailedAgentCount() {
        return (int) agents.stream()
            .filter(Agent::isJailed)
            .count();
    }

    public int getQuietAgentCount() {
        return (int) agents.stream()
            .filter(a -> !a.isJailed() && !a.isActive())
            .count();
    }

    public void printStatus() {
        System.out.println("=== Simulation Status ===");
        System.out.println("Active Agents: " + getActiveAgentCount());
        System.out.println("Jailed Agents: " + getJailedAgentCount());
        System.out.println("Quiet Agents: " + getQuietAgentCount());
        System.out.println("Number of Cops: " + cops.size());
        System.out.println("================\n");
    }

    public int getCopsCount() {
        return cops.size();
    }

    // ...
}
