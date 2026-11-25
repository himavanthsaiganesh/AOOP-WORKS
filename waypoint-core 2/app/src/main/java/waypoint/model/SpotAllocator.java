package waypoint.model;

/**
 * Allocates and releases parking spots.
 *
 * <pre>
 * PRE  allocate(vehicle): vehicle is non-null; the allocator contains the spots.
 * POST allocate(vehicle): returns a non-null, fitting, previously-free spot and marks it occupied.
 * INV  The set of represented spots does not change; every returned spot is unique until released.
 *
 * Timing       allocate/release complete synchronously.
 * Side effects allocate changes exactly one matching spot from free to occupied;
 *              release changes its target spot to free.
 * Idempotency  release has no effect after the first call for an already-free spot.
 * Threading    Operations are serialized so concurrent callers cannot occupy the same spot.
 * Ordering     Each implementation applies its documented preference among all fitting free spots.
 * </pre>
 *
 * @throws NoSpotAvailable if no fitting free spot exists
 */
public interface SpotAllocator {
    Spot allocate(Vehicle vehicle) throws NoSpotAvailable;
    void release(Spot spot);
}