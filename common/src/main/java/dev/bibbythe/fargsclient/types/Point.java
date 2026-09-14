package dev.bibbythe.fargsclient.types;

import net.minecraft.util.math.ChunkPos;

/**
 * Represents a 2D integer coordinate (x, z) on a horizontal plane with vector operations.
 */
public class Point {
    /**
     * The X coordinate.
     */
    public int x;
    /**
     * The Z coordinate.
     */
    public int z;

    /**
     * Adds the specified x and z offsets to this point.
     *
     * @param x the offset to add to X
     * @param z the offset to add to Z
     * @return this point instance for method chaining
     */
    public Point add(int x, int z) {
        this.x += x;
        this.z += z;
        return this;
    }

    /**
     * Adds a scalar value to both coordinates of this point.
     *
     * @param a the scalar value to add
     * @return this point instance for method chaining
     */
    public Point add(int a) {
        this.x += a;
        this.z += a;
        return this;
    }

    /**
     * Subtracts the specified x and z offsets from this point.
     *
     * @param x the offset to subtract from X
     * @param z the offset to subtract from Z
     * @return this point instance for method chaining
     */
    public Point sub(int x, int z) {
        this.x -= x;
        this.z -= z;
        return this;
    }

    /**
     * Subtracts a scalar value from both coordinates of this point.
     *
     * @param a the scalar value to subtract
     * @return this point instance for method chaining
     */
    public Point sub(int a) {
        this.x -= a;
        this.z -= a;
        return this;
    }

    /**
     * Multiplies the coordinates of this point component-wise by x and z.
     *
     * @param x the multiplier for X
     * @param z the multiplier for Z
     * @return this point instance for method chaining
     */
    public Point mul(int x, int z) {
        this.x *= x;
        this.z *= z;
        return this;
    }

    /**
     * Multiplies both coordinates of this point by a scalar factor.
     *
     * @param a the scalar multiplier
     * @return this point instance for method chaining
     */
    public Point mul(int a) {
        this.x *= a;
        this.z *= a;
        return this;
    }

    /**
     * Divides the coordinates of this point component-wise by x and z.
     *
     * @param x the divisor for X
     * @param z the divisor for Z
     * @return this point instance for method chaining
     */
    public Point div(int x, int z) {
        this.x /= x;
        this.z /= z;
        return this;
    }

    /**
     * Divides both coordinates of this point by a scalar divisor.
     *
     * @param a the scalar divisor
     * @return this point instance for method chaining
     */
    public Point div(int a) {
        this.x /= a;
        this.z /= a;
        return this;
    }

    /**
     * Creates a copy of this point with the same coordinates.
     *
     * @return a new {@link Point} instance with identical x and z values
     */
    public Point copy() {
        return new Point(this.x, this.z);
    }

    /**
     * Converts this point into a {@link Waypoint} with the specified index.
     *
     * @param index the waypoint index
     * @return a new {@link Waypoint} at this point's location
     */
    public Waypoint toWaypoint(int index) {
        return new Waypoint(this.x, this.z, index);
    }

    /**
     * Constructs a point with the given x and z coordinates.
     *
     * @param x the X coordinate
     * @param z the Z coordinate
     */
    public Point(int x, int z) {
        this.x = x;
        this.z = z;
    }

    /**
     * Constructs a point initialized from a {@link ChunkPos}.
     *
     * @param chunkPos the chunk position
     */
    public Point (ChunkPos chunkPos) {
        this.x = chunkPos.x;
        this.z = chunkPos.z;
    }

    /**
     * Constructs a point initialized from a {@link Waypoint}.
     *
     * @param waypoint the waypoint
     */
    public Point (Waypoint waypoint) {
        this.x = waypoint.x;
        this.z = waypoint.z;
    }
}
