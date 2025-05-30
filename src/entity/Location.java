package entity;
import java.util.Random;

/**
 * Records the x and y coordinates for each turtle and plot
 */
public class Location {
    private int x, y;
    private boolean hideable = false;
    private double hideable_proportion = 0.1;
    private Random random_list = new Random();
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
        if (random_list.nextDouble(1) < hideable_proportion){
            this.hideable = true;
        }
    }

    public Location getLocation() {
        return this;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean getHideable(){
        return hideable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return x == location.x && y == location.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
