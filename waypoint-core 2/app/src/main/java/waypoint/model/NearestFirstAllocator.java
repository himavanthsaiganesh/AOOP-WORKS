package waypoint.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/** Lowest distanceFromGate wins among fitting free spots. */
public final class NearestFirstAllocator implements SpotAllocator {
    private final List<Spot> spots;

    public NearestFirstAllocator(List<Spot> spots) {
        this.spots = new ArrayList<>(spots);
    }

    public NearestFirstAllocator(Spot... spots) {
        this.spots = new ArrayList<>(Arrays.asList(spots));
    }

    public NearestFirstAllocator(Collection<Spot> spots) {
        this.spots = new ArrayList<>(spots);
    }

    @Override
    public synchronized Spot allocate(Vehicle vehicle) throws NoSpotAvailable {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle must not be null");
        }
        return spots.stream()
                .filter(Spot::isFree)
                .filter(spot -> SpotType.fits(vehicle.size(), spot.type()))
                .min(Comparator.comparingInt(Spot::distanceFromGate)
                        .thenComparingInt(Spot::level))
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