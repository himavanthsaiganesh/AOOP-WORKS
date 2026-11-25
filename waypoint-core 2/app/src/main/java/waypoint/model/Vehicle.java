package waypoint.model;

public record Vehicle(VehicleSize size) {
    public Vehicle {
        if (size == null) {
            throw new IllegalArgumentException("size must not be null");
        }
    }
}
