package entity;

/**
 * 固定参数记录类
 * K = 2.3
 * THRESHOLD = 0.1
 * VISION = 3
 * MAX_JAIL_TERM = 30
 * GOVERNMENT_LEGITIMACY = 0.82
 * INITIAL_COP_DENSITY = 4.0%
 * INITIAL_AGENT_DENSITY = 70.0%
 */
public final class AppConfig {
    public static final double K = 2.3;
    public static final double THRESHOLD = 0.1;
    public static final int VISION = 3;
    public static final int MAX_JAIL_TERM = 30;
    public static final double GOVERNMENT_LEGITIMACY = 0.82;
    public static final double INITIAL_COP_DENSITY = 0.04;
    public static final double INITIAL_AGENT_DENSITY = 0.7;

    private AppConfig() {
        // 防止实例化
    }
}
