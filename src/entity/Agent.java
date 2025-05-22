package entity;

import static entity.AppConfig.*;

public class Agent extends Turtle {
    double risk_aversion = 0.5; //R, fixed for the agent's lifetime, ranging from 0-1 (inclusive)
    double perceived_hardship = 0.5;   //H, also ranging from 0-1 (inclusive)
    private boolean active = false;
    private int jail_term = 0;
    private boolean isLeader = false;  // 新增：是否为leader
    private static final double LEADER_RISK_AVERSION_MULTIPLIER = 0.5;  // leader的风险厌恶系数乘数
    private static final double LEADER_THRESHOLD_MULTIPLIER = 0.5;  // leader的阈值乘数
    private static final int LEADER_MAX_JAIL_TERM = 15;  // leader的最大监禁时间

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

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
        if (leader) {
            // 当成为leader时，降低风险厌恶系数
            this.risk_aversion *= LEADER_RISK_AVERSION_MULTIPLIER;
        }
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
        
        // 如果是leader，使用更低的阈值
        double effectiveThreshold = isLeader ? threshold * LEADER_THRESHOLD_MULTIPLIER : threshold;
        
        // 检查是否在leader周围，如果是则获得阈值折扣
        if (!isLeader && w.isNearLeader(location)) {
            effectiveThreshold *= 0.8;  // 在leader周围获得20%的阈值折扣
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
     * 服刑时间-1
     */
    public void decre_Jail_term(int jail_term) {
        this.jail_term--;
    }

    /**
     * random jail term
     */
    public void setJailTerm(int jail_term) {
        // 如果是leader，使用更短的监禁时间
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
