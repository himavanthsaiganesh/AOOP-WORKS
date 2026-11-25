package waypoint.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/** Proves the contract suite actually tests the contract. */
class CanaryTest {
    @Test void theSuiteRejectsADeliberatelyBrokenImplementation() {
        SpotAllocator broken = new BrokenAllocator();
        assertThrows(AssertionError.class,
            () -> assertNotNull(broken.allocate(new Vehicle(VehicleSize.COMPACT))),
            "the suite must fail on a null-returning allocator");
        Spot spot = new Spot(new SpotType.Compact(), 1, 1);
        assertThrows(IllegalStateException.class,
            () -> broken.release(spot),
            "the suite must reject a release implementation that is not idempotent");
    }
}