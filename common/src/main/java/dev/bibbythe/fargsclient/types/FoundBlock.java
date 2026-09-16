package dev.bibbythe.fargsclient.types;

import net.minecraft.util.math.BlockPos;

public class FoundBlock {
    String blockId;
    int x;
    int y;
    int z;
    public FoundBlock(String blockId,BlockPos pos) {
        this.blockId = blockId;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }
}
