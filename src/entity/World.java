package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import static entity.AppConfig.*;

//Initialize grid and turtles
public class World {
    private Patch[][] grid;
    private List<Agent> agents;
    private List<Cop> cops;
    private Random random;
    private double k;
    private double threshold;
    private double government_legitimacy;

    //Parameters are written in main

    // Initialize grid, agents, cops, and build neighborhoods
    public World(int width, int height) {
        this.grid = new Patch[width][height];
        this.agents = new ArrayList<>();
        this.cops = new ArrayList<>();
        this.random = new Random();
        
        // Initialize grid
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = new Patch(new Location(i, j));
            }
        }
    }
    // Create agents/cops and place them randomly in empty patches
    public void setup(double agentDensity, double copDensity, double k, double threshold, double government_legitimacy) {
        this.k = k;
        this.threshold = threshold;
        this.government_legitimacy = government_legitimacy;

        int totalPatches = grid.length * grid[0].length;
        int numAgents = (int) (agentDensity * totalPatches);
        int numCops = (int) (copDensity * totalPatches);

        // Create cops
        for (int i = 0; i < numCops; i++) {
            Location randomLoc = getRandomEmptyLocation();
            if (randomLoc != null) {
                cops.add(new Cop(randomLoc));
            }
        }

        // Create agents
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

    // Move, update status, enforce law, reduce jail terms
    public void tick() {
        // Follow NetLogo's order: move first, then determine behavior, finally enforce law
        // 1. Move all agents and cops
        for (Agent agent : agents) {
            // Only move if MOVEMENT is true or agent is not in jail
            if (!agent.isJailed() && MOVEMENT) {
                agent.moveTo(getValidMoveLocation(agent.getLocation()));
            }
        }
        // Cops always move
        for (Cop cop : cops) {
            cop.moveTo(getValidMoveLocation(cop.getLocation()));
        }

        // 2. Update all agents' status
        for (Agent agent : agents) {
            if (!agent.isJailed()) {
                agent.beingActive(threshold, government_legitimacy, this);
            }
        }

        // 3. Cops enforce law
        for (Cop cop : cops) {
            Optional<Agent> suspect = getRandomActiveAgentInNeighborhood(cop.getLocation());
            if (suspect.isPresent()) {
                Agent agent = suspect.get();
                cop.moveTo(agent.getLocation());
                agent.setActive(false);
                agent.setJailTerm(random.nextInt(MAX_JAIL_TERM)); // Use same random range as NetLogo: 0 to MAX_JAIL_TERM-1
            }
        }

        // 4. Reduce jail terms
        for (Agent agent : agents) {
            if (agent.isJailed()) {
                agent.decre_Jail_term(agent.getJail_term());
            }
        }
    }

    private Location getValidMoveLocation(Location center) {
        // Create list of neighbors based on circular radius
        List<Location> validLocations = new ArrayList<>();
        
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Location loc = new Location(i, j);
                // Check if within vision
                if (isInVision(center, loc)) {
                    // Check if valid move location
                    if (isValidMoveLocation(loc)) {
                        validLocations.add(loc);
                    }
                }
            }
        }
        
        // If no valid locations, stay in place
        if (validLocations.isEmpty()) {
            return center;
        }
        
        // Randomly select a valid location
        return validLocations.get(random.nextInt(validLocations.size()));
    }

    private boolean isValidMoveLocation(Location loc) {
        // Check if there are cops
        boolean hasCops = cops.stream().anyMatch(c -> c.getLocation().equals(loc));
        if (hasCops) return false;

        // Check if there are non-jailed agents
        boolean hasNonJailedAgents = agents.stream()
            .anyMatch(a -> a.getLocation().equals(loc) && !a.isJailed());
        if (hasNonJailedAgents) return false;

        return true;
    }

    public double calculateArrestProbability(Location location) {
        int copsCount = copsOnNeighborhood(location);
        int activeAgentsCount = countActiveAgentsInNeighborhood(location);
        // if (activeAgentsCount == 0) return 0;
        
        // Use NetLogo's formula: 1 - exp(-k * floor(c/a))
        return 1 - Math.exp(-k * Math.floor((double) copsCount / (activeAgentsCount + 1)));
    }

    //Count cops in neighborhood
    public int copsOnNeighborhood(Location location){
        return (int) cops.stream()
            .filter(cop -> isInVision(location, cop.getLocation()))
            .count();
    }

    //Count active agents in neighborhood, excluding self
    public int countActiveAgentsInNeighborhood(Location t) {
        return (int) agents.stream()
            .filter(a -> !a.isJailed() && a.isActive() && isInVision(t, a.getLocation()) && !a.getLocation().equals(t))
            .count();
    }

    //Find rebels within vision, return null if none
    public Optional<Agent> getRandomActiveAgentInNeighborhood(Location location) {
        List<Agent> activeAgents = agents.stream()
            .filter(a -> !a.isJailed() && a.isActive() && isInVision(location, a.getLocation()))
            .toList();
        return activeAgents.isEmpty() ? Optional.empty() : Optional.of(activeAgents.get(random.nextInt(activeAgents.size())));
    }

    private boolean isInVision(Location loc1, Location loc2) {
        int dx = Math.abs(loc1.getX() - loc2.getX());
        int dy = Math.abs(loc1.getY() - loc2.getY());
        
        // Handle toroidal world boundaries
        dx = Math.min(dx, grid.length - dx);
        dy = Math.min(dy, grid[0].length - dy);
        
        // Use Euclidean distance to simulate NetLogo's in-radius
        return Math.sqrt(dx * dx + dy * dy) <= VISION;
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
