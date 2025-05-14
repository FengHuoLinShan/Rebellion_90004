package entity;
import java.util.Optional;
import java.util.Random;

import static entity.AppConfig.*;

public class Cop extends Turtle{
     protected Cop(Location location){
        this.color = COPS_COLOR;
        this.location = location;
    }

    public void arrest(World world) {
        Optional<Agent> suspect = world.getRandomActiveAgentInNeighborhood(location);
        suspect.ifPresent(agent -> {
            this.moveTo(agent.getLocation());
            agent.setActive(false);
            agent.setJailTerm(new Random().nextInt(MAX_JAIL_TERM));
        });
    }
}
