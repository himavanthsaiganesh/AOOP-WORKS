package waypoint.model;

import java.util.Objects;

public final class Spot {
    private final SpotType type;
    private final int level;
    private final int distanceFromGate;
    private boolean free;

    public Spot(SpotType type, int level, int distanceFromGate) {
        this(type, level, distanceFromGate, true);
    }

    public Spot(SpotType type, int level, int distanceFromGate, boolean free) {
        this.type = Objects.requireNonNull(type, "type");
        if (level < 0) {
            throw new IllegalArgumentException("level must be non-negative");
        }
        if (distanceFromGate < 0) {
            throw new IllegalArgumentException("distanceFromGate must be non-negative");
        }
        this.level = level;
        this.distanceFromGate = distanceFromGate;
        this.free = free;
    }

    public SpotType type() {
        return type;
    }

    public SpotType spotType() {
        return type;
    }

    public int level() {
        return level;
    }

    public int getLevel() {
        return level;
    }

    public int distanceFromGate() {
        return distanceFromGate;
    }

    public int getDistanceFromGate() {
        return distanceFromGate;
    }

    public boolean isFree() {
        return free;
    }

    public void allocate() {
        free = false;
    }

    public void release() {
        free = true;
    }

    @Override
    public String toString() {
        return "Spot[type=" + type + ", level=" + level
                + ", distanceFromGate=" + distanceFromGate + ", free=" + free + "]";
    }
}
