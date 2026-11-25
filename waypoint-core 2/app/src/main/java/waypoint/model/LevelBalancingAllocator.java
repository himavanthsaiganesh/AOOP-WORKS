package waypoint.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Prefers the least-occupied level, then nearest within it. */
public final class LevelBalancingAllocator implements SpotAllocator {
    private final List<Spot> spots;

    public LevelBalancingAllocator(List<Spot> spots) {
        this.spots = new ArrayList<>(spots);
    }

    public LevelBalancingAllocator(Spot... spots) {
        this.spots = new ArrayList<>(Arrays.asList(spots));
    }

    public LevelBalancingAllocator(Collection<Spot> spots) {
        this.spots = new ArrayList<>(spots);
    }

    @Override
    public synchronized Spot allocate(Vehicle vehicle) throws NoSpotAvailable {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle must not be null");
        }
        Map<Integer, Long> occupiedByLevel = spots.stream()
                .collect(Collectors.groupingBy(Spot::level,
                        Collectors.summingLong(spot -> spot.isFree() ? 0L : 1L)));

        Comparator<Spot> preference = Comparator
                .comparingInt((Spot spot) -> occupiedByLevel.getOrDefault(spot.level(), 0L).intValue())
                .thenComparingInt(Spot::distanceFromGate)
                .thenComparingInt(Spot::level);

        return spots.stream()
                .filter(Spot::isFree)
                .filter(spot -> SpotType.fits(vehicle.size(), spot.type()))
                .min(preference)
                .map(spot -> {
                    spot.allocate();
                    return spot;
                })
                .orElseThrow(NoSpotAvailable::new);
    }

    @Override
    public synchronized void release(Spot spot) {
        if (spot != null) {
            spot.release();
        }
    }
}