package entity;

import java.util.List;

public class Patch {
    private Location location;
    
    Turtle turtle;

    public Patch(Location location) {
        this.location = location;
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
