package waypoint.model;

/**
 * THE CANARY. Deliberately violates the contract.
 * CanaryTest asserts that the shared suite REJECTS this class.
 * If it ever passes, the contract test has stopped testing the contract.
 */
public final class BrokenAllocator implements SpotAllocator {
    public Spot allocate(Vehicle v) { return null; }              // weakened postcondition
    public void release(Spot s)     { throw new IllegalStateException(); }  // not idempotent
}