package entity;

import static entity.AppConfig.*;

public class Agent extends Turtle {
    double risk_aversion = 0.5; //R, fixed for the agent's lifetime, ranging from 0-1 (inclusive)
    double perceived_hardship = 0.5;   //H, also ranging from 0-1 (inclusive)
    private boolean active = false;
    private int jail_term = 0;

    //创建时每个agent有不同的参数（color是固定的）
    public Agent(Location location, double risk_aversion, double perceived_hardship) {
        this.color = AGENT_COLOR;
        this.location = location;
        this.risk_aversion = risk_aversion;
        this.perceived_hardship = perceived_hardship;
    }

    public boolean isJailed() {
        return jail_term > 0;
    }

    /**
     * 更新（判断）active状态
     */
    public void beingActive(double threshold, double government_legitimacy, World w){
        active = (getGrievance(government_legitimacy) - risk_aversion * estimateArrestProbability(w)
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

    /**
     * 服刑时间-1
     */
    public void decre_Jail_term(int jail_term) {
        this.jail_term--;
    }

    /**
     * 被捕时随机决定服刑时间
     */
    public void setJailTerm(int jail_term) {
        this.jail_term = jail_term;
    }

    public void setActive(boolean b) {
        this.active = b;
    }
}
