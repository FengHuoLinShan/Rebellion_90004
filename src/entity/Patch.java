package entity;

import java.util.List;

public class Patch {
    private Location location;
    private Turtle turtle;
    private double localLegitimacy;
    private int copTimes;
    private static double MAX_LOCAL_RELATED = 10.0; // Make this configurable

    public Patch(Location location) {
        this.location = location;
        this.localLegitimacy = 0.0;
        this.copTimes = 0;
    }

    public static void setMaxLocalRelated(double value) {
        MAX_LOCAL_RELATED = value;
    }

    public static double getMaxLocalRelated() {
        return MAX_LOCAL_RELATED;
    }

    public Location getLocation() {
        return location;
    }

    public void addTurtle(Turtle t){
        this.turtle = t;
    }

    public void removeTurtle(){
        this.turtle = null;
    }

    public double getLocalLegitimacy() {
        return localLegitimacy;
    }

    public void updateLocalLegitimacy(double governmentLegitimacy) {
        // Update local legitimacy based on cop times
        this.localLegitimacy = governmentLegitimacy - (1.0 / (copTimes + MAX_LOCAL_RELATED));
    }

    public void incrementCopTimes() {
        this.copTimes++;
    }

    public void decrementCopTimes() {
        if (this.copTimes > 1) {
            this.copTimes--;
        } else {
            this.copTimes = 0;
        }
    }

    public int getCopTimes() {
        return copTimes;
    }

    /**
     * Check if this patch is empty (or only contains jailed agents)
     */
    public boolean isEmptyOrJailedOnly(){
        boolean empty = (turtle == null);
        boolean JailedOnly = false;
        if (turtle instanceof Agent){
            JailedOnly = ((Agent) turtle).isJailed();
        }
        return empty||JailedOnly;
    }
}
