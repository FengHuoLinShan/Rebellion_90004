package entity;

import static entity.AppConfig.*;

public class Agent extends Turtle {
    double risk_aversion = 0.5; //R, fixed for the agent's lifetime, ranging from 0-1 (inclusive)
    double perceived_hardship = 0.5;   //H, also ranging from 0-1 (inclusive)
    private boolean active = false;
    private int jail_term = 0;
    private boolean isLeader = false;  // New: whether the agent is a leader
    private static final double LEADER_RISK_AVERSION_MULTIPLIER = 0.5;  // Risk aversion multiplier for leaders
    private static final double LEADER_THRESHOLD_MULTIPLIER = 0.5;  // Threshold multiplier for leaders
    private static final int LEADER_MAX_JAIL_TERM = 15;  // Maximum jail term for leaders

    // Each agent has different parameters when created (color is fixed)
    public Agent(Location location, double risk_aversion, double perceived_hardship) {
        this.color = AGENT_COLOR;
        this.location = location;
        this.risk_aversion = risk_aversion;
        this.perceived_hardship = perceived_hardship;
    }

    public boolean isJailed() {
        return jail_term > 0;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
        if (leader) {
            // When becoming a leader, reduce risk aversion
            this.risk_aversion *= LEADER_RISK_AVERSION_MULTIPLIER;
        }
    }

    /**
     * Update (determine) active status
     * Using the same logic as NetLogo:
     * active? = (grievance - risk-aversion * estimated-arrest-probability > threshold)
     */
    public void beingActive(double threshold, double government_legitimacy, World w) {
        double grievance = getGrievance(government_legitimacy);
        double arrestProb = w.calculateArrestProbability(location);
        double netRisk = risk_aversion * arrestProb;
        
        // If it's a leader, use a lower threshold
        double effectiveThreshold = isLeader ? threshold * LEADER_THRESHOLD_MULTIPLIER : threshold;
        
        // Check if near a leader, if so get a threshold discount
        if (!isLeader && w.isNearLeader(location)) {
            effectiveThreshold *= 0.8;  // Get 20% threshold discount when near a leader
        }
        
        active = (grievance - netRisk > effectiveThreshold);
    }

    private double getGrievance(double government_legitimacy) {
        return perceived_hardship * (1 - government_legitimacy);
    }

    public void moveTo(Location randomLoc){
        this.location = randomLoc;
    }

    public int getJail_term() {
        return jail_term;
    }

    /**
     * Decrease jail term by 1
     */
    public void decre_Jail_term(int jail_term) {
        this.jail_term--;
    }

    /**
     * Set random jail term
     */
    public void setJailTerm(int jail_term) {
        // If it's a leader, use shorter jail term
        if (isLeader) {
            this.jail_term = Math.max(1, Math.min(jail_term, LEADER_MAX_JAIL_TERM));
        } else {
            this.jail_term = Math.max(1, jail_term);
        }
    }

    public void setActive(boolean b) {
        this.active = b;
    }
}
