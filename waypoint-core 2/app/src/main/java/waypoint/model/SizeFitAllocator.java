package waypoint.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/** Smallest spot that fits. A compact car may take a large bay
 *  ONLY when no compact bay remains. */
public final class SizeFitAllocator implements SpotAllocator {
    private final List<Spot> spots;

    public SizeFitAllocator(List<Spot> spots) {
        this.spots = new ArrayList<>(spots);
    }

    public SizeFitAllocator(Spot... spots) {
        this.spots = new ArrayList<>(Arrays.asList(spots));
    }

    public SizeFitAllocator(Collection<Spot> spots) {
        this.spots = new ArrayList<>(spots);
    }

    @Override
    public synchronized Spot allocate(Vehicle vehicle) throws NoSpotAvailable {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle must not be null");
        }

        return switch (vehicle.size()) {
            case MOTORCYCLE -> allocateFirstFit(vehicle,
                    Comparator.comparingInt(Spot::distanceFromGate));
            case COMPACT, CAR -> allocateCompactFirst(vehicle);
            case LARGE -> allocateTypeFirst(vehicle, SpotType.Large.class);
            case EV, ELECTRIC -> allocateTypeFirst(vehicle, SpotType.Ev.class);
        };
    }

    private Spot allocateCompactFirst(Vehicle vehicle) {
        List<Spot> compact = spots.stream()
                .filter(Spot::isFree)
                .filter(spot -> spot.type() instanceof SpotType.Compact)
                .sorted(Comparator.comparingInt(Spot::distanceFromGate).thenComparingInt(Spot::level))
                .toList();
        if (!compact.isEmpty()) {
            Spot spot = compact.get(0);
            spot.allocate();
            return spot;
        }
        return allocateFirstFit(vehicle, Comparator.comparingInt(Spot::distanceFromGate));
    }

    private Spot allocateTypeFirst(Vehicle vehicle, Class<?> type) {
        return spots.stream()
                .filter(Spot::isFree)
                .filter(spot -> type.isInstance(spot.type()))
                .filter(spot -> SpotType.fits(vehicle.size(), spot.type()))
                .min(Comparator.comparingInt(Spot::distanceFromGate).thenComparingInt(Spot::level))
                .map(spot -> {
                    spot.allocate();
                    return spot;
                })
                .orElseThrow(NoSpotAvailable::new);
    }

    private Spot allocateFirstFit(Vehicle vehicle, Comparator<Spot> preference) {
        return spots.stream()
                .filter(Spot::isFree)
                .filter(spot -> SpotType.fits(vehicle.size(), spot.type()))
                .sorted(preference
                        .thenComparing(spot -> spot instanceof Spot ? spotSizeRank(spot) : 0)
                        .thenComparingInt(Spot::level))
                .findFirst()
                .map(spot -> {
                    spot.allocate();
                    return spot;
                })
                .orElseThrow(NoSpotAvailable::new);
    }

    private int spotSizeRank(Spot spot) {
        if (spot.type() instanceof SpotType.Compact) {
            return 0;
        }
        if (spot.type() instanceof SpotType.Ev) {
            return 1;
        }
        return 2;
    }

    @Override
    public synchronized void release(Spot spot) {
        if (spot != null) {
            spot.release();
        }
    }
}