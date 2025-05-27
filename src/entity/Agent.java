package entity;

import static entity.AppConfig.*;

public class Agent extends Turtle {
    double risk_aversion = 0.5; //R, fixed for the agent's lifetime, ranging from 0-1 (inclusive)
    double perceived_hardship = 0.5;   //H, also ranging from 0-1 (inclusive)
    private boolean active = false;
    private int jail_term = 0;

    //Each agent has different parameters when created (color is fixed)
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

    /**
     * Update (determine) active status
     * Using the same logic as NetLogo:
     * active? = (grievance - risk-aversion * estimated-arrest-probability > threshold)
     */
    public void beingActive(double threshold, double government_legitimacy, World w) {
        double grievance = getGrievance(government_legitimacy);
        double arrestProb = w.calculateArrestProbability(location);
        double netRisk = risk_aversion * arrestProb;
        active = (grievance - netRisk > threshold);
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
     * Set random jail term when arrested
     * Note: In NetLogo, jail-term of 0 means not in jail
     */
    public void setJailTerm(int jail_term) {
        // Ensure jail term is at least 1, as 0 means not in jail
        this.jail_term = Math.max(1, jail_term);
    }

    public void setActive(boolean b) {
        this.active = b;
    }
}
