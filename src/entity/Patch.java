package entity;

import java.util.List;

public class Patch {
    private Location location;
    Turtle turtle;

    public void addTurtle(Turtle t){
        this.turtle = t;
    }

    public void removeTurtle(){
        this.turtle = null;
    }

    //判断此地块是否为空（或者仅关押）
    public boolean isEmptyOrJailedOnly(){
        boolean empty = (turtle == null);
        boolean JailedOnly = false;
        if (turtle instanceof Agent){
            JailedOnly = ((Agent) turtle).isJailed();
        }
        return empty||JailedOnly;
    }



}
