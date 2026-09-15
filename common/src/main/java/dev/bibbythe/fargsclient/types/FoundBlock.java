package dev.bibbythe.fargsclient.types;

import net.minecraft.util.math.BlockPos;

public class FoundBlock {
    String blockId;
    BlockPos pos;
    public FoundBlock(String blockId,BlockPos pos) {
        this.blockId = blockId;
        this.pos = pos;
    }
}
