package dev.bibbythe.fargsclient.traversal;

import dev.bibbythe.fargsclient.types.Waypoint;
import dev.bibbythe.fargsclient.types.Point;
import net.minecraft.util.math.ChunkPos;

/**
 * Generates waypoints along a 2D Hilbert curve for space-filling area scanning and traversal.
 */
public class HilbertCurve {
    /**
     * The order (recursion depth) of the Hilbert curve.
     */
    private final int order;
    /**
     * The starting chunk coordinate for the traversal region.
     */
    final ChunkPos startChunk;
    /**
     * The region width (in chunks) representing the width of the scan area.
     */
    final int regionWidth;
    /**
     * The effective scan region width computed from the scan radius.
     */
    private final int scanWidth;
    /**
     * The index of the current point along the Hilbert curve (-1 before traversal starts).
     */
    public int CurrentPoint = -1;
    /**
     * The total number of points in the Hilbert curve grid.
     */
    private final int MaxPoints;

    /**
     * Constructs a Hilbert curve traversal with a specified initial point index.
     *
     * @param regionWidth  the width of the region to scan in chunks (must be a power of 2)
     * @param scanRadius   the scan radius in chunks used to determine the total scan width
     * @param startChunk   the starting chunk coordinate
     * @param currentPoint the 1-based index of the current point to resume traversal from
     * @throws IllegalArgumentException if {@code regionWidth} is not a power of 2 or exceeds the scan region width
     */
    public HilbertCurve(int regionWidth, int scanRadius, ChunkPos startChunk, int currentPoint) throws IllegalArgumentException {
        this.startChunk = startChunk;
        this.regionWidth = regionWidth;
        this.scanWidth = highestPowerof2(scanRadius * 2);
        if (!((regionWidth > 0) && ((regionWidth & (regionWidth - 1)) == 0))) {
            throw new IllegalArgumentException("region size must be a power of 2");
        }
        if (regionWidth < scanWidth) {
            throw new IllegalArgumentException("region size must be larger must be greater than search area size");
        }
        MaxPoints = (regionWidth /scanWidth) * (regionWidth /scanWidth);
        order = 31 - Integer.numberOfLeadingZeros(regionWidth /scanWidth);
        this.CurrentPoint = currentPoint - 1;
    }

    int highestPowerof2(int N)
    {

        // if N is a power of two simply return it
        if ((N & (N - 1)) == 0)
            return N;

        // else set only the most significant bit
        return (1 << (Integer.toBinaryString(N).length() - 1));
    }

    /**
     * Constructs a Hilbert curve traversal starting from the beginning.
     *
     * @param regionWidth  the width of the region to scan in chunks (must be a power of 2)
     * @param scanRadius  the scan radius in chunks used to determine the total scan width
     * @param startChunk  the starting chunk coordinate
     * @throws IllegalArgumentException if {@code regionWidth} is not a power of 2 or exceeds the scan region width
     */
    public HilbertCurve(int regionWidth, int scanRadius, ChunkPos startChunk) throws IllegalArgumentException {
        this.startChunk = startChunk;
        this.regionWidth = regionWidth;
        this.scanWidth = highestPowerof2(scanRadius * 2);
        if (!((regionWidth > 0) && ((regionWidth & (regionWidth - 1)) == 0))) {
            throw new IllegalArgumentException("region size must be a power of 2");
        }
        if (regionWidth < scanWidth) {
            throw new IllegalArgumentException("region size must be greater than search area size");
        }
        MaxPoints = (regionWidth / scanWidth) * (regionWidth / scanWidth);
        order = 31 - Integer.numberOfLeadingZeros(regionWidth / scanWidth );
    }

    /**
     * Advances to the next point on the Hilbert curve and calculates its world coordinate waypoint.
     *
     * @return the next {@link Waypoint}, or {@code null} if all points have been visited
     */
    public Waypoint getNextWaypoint(){
        CurrentPoint++;
        if (CurrentPoint >= MaxPoints) {
            return null;
        }
        Point point = getHilbertPoint(CurrentPoint);
        // multiply by search width to set distance between waypoints
        point.mul(scanWidth);
        // apply start chunk offset to ensure waypoints are within search area
        point.add(startChunk.x, startChunk.z);
        // apply search radius offset to center waypoint in search area in chunk coords
        point.add(scanWidth / 2);
        // multiply by 16 to convert from chunk coords to block coords
        point.mul(16);
        // apply block offset of 15 to finish centering in search radius as search width will be even for math reasons
        point.add(15);
        return point.toWaypoint(CurrentPoint);
    }

    /**
     * Computes the 2D grid coordinates of the point at index {@code i} on the Hilbert curve.
     *
     * @param i the 0-based curve index
     * @return a {@link Point} representing the 2D coordinates on the unit curve grid
     */
    public Point getHilbertPoint(int i) {
        Point[] points = {
                new Point(0, 0),
                new Point(0, 1),
                new Point(1, 1),
                new Point(1, 0)
        };

        int index = i & 3;
        Point v = points[index];

        for (int j = 1; j < order; j++) {
            i = i >>> 2;
            index = Math.toIntExact(i & 3);
            int len = 1 << j;
            v = switch (index) {
                case 0 -> new Point(v.z, v.x);
                case 1 -> new Point(v.x, v.z + len);
                case 2 -> new Point(v.x + len, v.z + len);
                case 3 -> new Point((len - 1 - v.z) + len, len - 1 - v.x);
                default -> v;
            };
        }
        return v;
    }
}
