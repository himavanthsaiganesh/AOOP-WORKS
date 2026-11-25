package waypoint.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.List;
import java.util.stream.Stream;

/** ONE suite. Every implementation must pass it, unchanged.
 *  RULE: no branch anywhere in this file on which implementation is running. */
class AllocatorContractTest {
    static List<Spot> threeFreeSpots() {
        return List.of(
                new Spot(new SpotType.Compact(), 1, 30),
                new Spot(new SpotType.Compact(), 2, 20),
                new Spot(new SpotType.Large(), 1, 10)
        );
    }

    static Vehicle car() {
        return new Vehicle(VehicleSize.COMPACT);
    }

    static Vehicle motorcycle() {
        return new Vehicle(VehicleSize.MOTORCYCLE);
    }

    static void drainAllSpots(SpotAllocator a) {
        while (true) {
            try {
                a.allocate(car());
            } catch (NoSpotAvailable expected) {
                return;
            }
        }
    }

    static Stream<SpotAllocator> implementations() {
        return Stream.of(new NearestFirstAllocator(threeFreeSpots()),
                         new LevelBalancingAllocator(threeFreeSpots()),
                         new SizeFitAllocator(threeFreeSpots()));
    }

    @ParameterizedTest @MethodSource("implementations")
    void neverReturnsNull(SpotAllocator a) {
        assertNotNull(a.allocate(car()));
    }

    @ParameterizedTest @MethodSource("implementations")
    void allocatedSpotIsNoLongerFree(SpotAllocator a) {
        assertFalse(a.allocate(car()).isFree());
    }

    @ParameterizedTest @MethodSource("implementations")
    void acceptsEveryVehicleSize(SpotAllocator a) {
        assertDoesNotThrow(() -> a.allocate(motorcycle()));   // the contract accepts all sizes
    }

    @ParameterizedTest @MethodSource("implementations")
    void throwsWhenNothingFits(SpotAllocator a) {
        drainAllSpots(a);
        assertThrows(NoSpotAvailable.class, () -> a.allocate(car()));
    }

    @ParameterizedTest @MethodSource("implementations")
    void releaseIsIdempotent(SpotAllocator a) {
        Spot s = a.allocate(car());
        a.release(s);
        assertDoesNotThrow(() -> a.release(s));    // twice must be harmless
        assertTrue(s.isFree());
    }

    @ParameterizedTest @MethodSource("implementations")
    void neverAllocatesTheSameSpotTwice(SpotAllocator a) {
        Spot first = a.allocate(car());
        for (int i = 0; i < 2; i++) {
            try { assertNotEquals(first, a.allocate(car())); }
            catch (NoSpotAvailable expected) { return; }
        }
    }
}