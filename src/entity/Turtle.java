package entity;

//Abstract parent class for Turtle
public abstract class Turtle {
    protected Location location;
    protected String color;

    public Location getLocation() {
        return location;
    }

    /**
     * Overrides: When controlling agent movement, first randomly get randomLoc within VISION, then check isEmptyOrJailedOnly(). Cops only move when there is active within VISION
     */
    public void moveTo(Location randomLoc){
        location = randomLoc;
    }
}
