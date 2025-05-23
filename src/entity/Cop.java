package entity;
import java.util.Optional;
import java.util.Random;

import static entity.AppConfig.*;

public class Cop extends Turtle {
    private double arrest_probability = 0.6;
    private Random random_list = new Random();

    public Cop(Location location) {
        this.color = COPS_COLOR;
        this.location = location;
    }

    public void moveTo(Location newLocation) {
        this.location = newLocation;
    }

    public void arrest(World world) {
        Optional<Agent> suspect = world.getRandomActiveAgentInNeighborhood(location);
        if(random_list.nextInt(1) < arrest_probability){
            suspect.ifPresent(agent -> {
            this.moveTo(agent.getLocation());
            agent.setActive(false);
            agent.setJailTerm(new Random().nextInt(MAX_JAIL_TERM) + 1);
            });
        }
    }
}
