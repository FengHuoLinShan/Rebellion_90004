package entity;

/**
 * Fixed parameter record class
 * K = 2.3
 * THRESHOLD = 0.1
 * VISION = 3
 * MAX_JAIL_TERM = 30
 * GOVERNMENT_LEGITIMACY = 0.82
 * INITIAL_COP_DENSITY = 4.0%
 * INITIAL_AGENT_DENSITY = 70.0%
 * AGENT_COLOR = "GREEN"
 * COPS_COLOR = "RED"
 */
public final class AppConfig {
    public static final double K = 2.3;
    public static final double THRESHOLD = 0.1;

    //netlogo slider variables
    public static final int VISION = 7;
    public static final int MAX_JAIL_TERM = 30;
    public static final double GOVERNMENT_LEGITIMACY = 0.82;
    public static final double INITIAL_COP_DENSITY = 0.04;
    public static final double INITIAL_AGENT_DENSITY = 0.7;
    
    // Additional parameters in NetLogo
    public static final boolean MOVEMENT = true; // Whether to allow agent movement

    public static final String AGENT_COLOR = "GREEN";
    public static final String COPS_COLOR = "RED";
    private AppConfig() {
        // Prevent instantiation
    }
}
