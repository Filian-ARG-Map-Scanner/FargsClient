package dev.bibbythe.fargsclient.communication.types.datatypes;

import dev.bibbythe.fargsclient.traversal.Autopilot;
import dev.bibbythe.fargsclient.types.FoundBlock;

import java.util.List;

public class ScanResultData {
    public int x;
    public int z;
    public int index;
    public List<FoundBlock> data;

    public ScanResultData(List<FoundBlock> data) {
        this.x = Autopilot.hilbertCurve.startChunk.x;
        this.z = Autopilot.hilbertCurve.startChunk.z;
        this.index = Autopilot.hilbertCurve.CurrentPoint;
        this.data = data;
    }

}
