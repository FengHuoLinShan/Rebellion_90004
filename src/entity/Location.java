package entity;

//记录每个turtle和地块的横纵坐标
public class Location {
    private int x,y;

    public Location getLocation() {
        return this;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
