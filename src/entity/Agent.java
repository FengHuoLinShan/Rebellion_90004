package entity;

import static entity.AppConfig.*;

public class Agent extends Turtle {
    double risk_aversion = 0.5; //R, fixed for the agent's lifetime, ranging from 0-1 (inclusive)
    double perceived_hardship = 0.5;   //H, also ranging from 0-1 (inclusive)
    private boolean active = false;
    private int jail_term = 0;


    public Agent(String color, Location location, double risk_aversion, double perceived_hardship) {
        this.color = color;
        this.location = location;
        this.risk_aversion = risk_aversion;
        this.perceived_hardship = perceived_hardship;
    }

    public boolean isJailed() {
        return jail_term > 0;
    }

    public void beingActive(double estimated_arrest_probability, double threshold, double government_legitimacy){
        active = (getGrievance(government_legitimacy) - risk_aversion * estimated_arrest_probability
                > threshold);
    }

    private double getGrievance(double government_legitimacy){
        double grievance = perceived_hardship * (1 - government_legitimacy);
        return grievance;
    }

    private double estimateArrestProbability(World world){
        int c = world.copsOnNeighborhood(location);
        int a = 1 + world.countActiveAgentsInNeighborhood(location);
        return 1 - Math.exp(-K * Math.floorDiv(c, a));
    }

    public void moveTo(Location randomLoc){
        //todo:与VISION算法有关，待完善
    }

    public int getJail_term() {
        return jail_term;
    }

    public void decre_Jail_term(int jail_term) {
        this.jail_term--;
    }

    public void setJailTerm(int nextInt) {
        this.jail_term = jail_term;
    }

    public void setActive(boolean b) {
        this.active = b;
    }
}
