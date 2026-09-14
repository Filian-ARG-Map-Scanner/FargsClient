package dev.bibbythe.fargsclient.types;

import net.minecraft.util.math.ChunkPos;

/**
 * Represents an indexed waypoint extending {@link Point} with an associated traversal sequence index.
 */
public class Waypoint extends Point {
    /**
     * The sequence index of this waypoint.
     */
    public int index;

    /**
     * Converts this waypoint to a base {@link Point} instance.
     *
     * @return a new {@link Point} with this waypoint's coordinates
     */
    public Point toPoint() {
        return new Point(x, z);
    }

    /**
     * Constructs a waypoint with the specified coordinates and index.
     *
     * @param x     the X coordinate
     * @param y     the Z coordinate (mapped to superclass z)
     * @param index the waypoint index
     */
    public Waypoint (int x, int y, int index) {
        super(x, y);
        this.index = index;
    }

    /**
     * Constructs a waypoint from an existing {@link Point} and index.
     *
     * @param point the base point
     * @param index the waypoint index
     */
    public Waypoint (Point point, int index) {
        super(point.x, point.z);
        this.index = index;
    }

    /**
     * Constructs a waypoint from a {@link ChunkPos} and index.
     *
     * @param chunkPos the chunk position
     * @param index    the waypoint index
     */
    public Waypoint (ChunkPos chunkPos, int index) {
        super(chunkPos);
        this.index = index;
    }
}
