package entity;

//Turtle抽象父类
public abstract class Turtle {
    protected Location location;
    protected String color;

    public Location getLocation() {
        return location;
    }

    /**
     * Overrides:在控制agent移动时，先随机获取VISION内的randomLoc，再检测isEmptyOrJailedOnly()
     */
    public void moveTo(Location randomLoc){
        location = randomLoc;
    }
}
