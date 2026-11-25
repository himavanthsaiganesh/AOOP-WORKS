package waypoint.model;

/** The kinds of parking bay supported by the allocator. */
public sealed interface SpotType permits SpotType.Compact, SpotType.Large, SpotType.Ev {
    record Compact() implements SpotType {}
    record Large() implements SpotType {}
    record Ev(int kw) implements SpotType {}

    static boolean fits(VehicleSize size, SpotType spot) {
        return switch (size) {
            case MOTORCYCLE -> true;
            case COMPACT, CAR -> switch (spot) {
                case Compact ignored -> true;
                case Large ignored -> true;
                case Ev ignored -> false;
            };
            case LARGE -> spot instanceof Large;
            case EV, ELECTRIC -> switch (spot) {
                case Compact ignored -> false;
                case Large ignored -> false;
                case Ev ignored -> true;
            };
        };
    }
}