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

    public boolean isActive() {
        return active;
    }

    /**
     * 更新（判断）active状态
     * 使用与 NetLogo 完全相同的逻辑：
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
     * 服刑时间-1
     */
    public void decre_Jail_term(int jail_term) {
        this.jail_term--;
    }

    /**
     * 被捕时随机决定服刑时间
     * 注意：在 NetLogo 中，jail-term 为 0 表示不在监狱中
     */
    public void setJailTerm(int jail_term) {
        // 确保监禁时间至少为 1，因为 0 表示不在监狱
        this.jail_term = Math.max(1, jail_term);
    }

    public void setActive(boolean b) {
        this.active = b;
    }
}
