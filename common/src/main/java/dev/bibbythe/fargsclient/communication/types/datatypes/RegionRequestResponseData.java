package dev.bibbythe.fargsclient.communication.types.datatypes;
public class RegionRequestResponseData {
    public final int x;
    public final int z;
    public final int width;
    public final int index;
    public final boolean isResume;

    public RegionRequestResponseData(int x, int z, int width, int index, boolean isResume) {
        this.x = x;
        this.z = z;
        this.width = width;
        this.index = index;
        this.isResume = isResume;
    }
}
