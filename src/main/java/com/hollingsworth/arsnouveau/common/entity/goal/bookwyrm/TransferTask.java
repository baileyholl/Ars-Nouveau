package com.hollingsworth.arsnouveau.common.entity.goal.bookwyrm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class TransferTask {
    public Vec3 from;
    public Vec3 to;
    public ItemStack stack;
    public long gameTime;
    public BlockPos fromPos;
    public BlockPos toPos;

    public TransferTask(BlockPos from, BlockPos to, ItemStack stack, long gameTime) {
        this.from = new Vec3(from.getX() + 0.5, from.getY(), from.getZ() + 0.5);
        this.to = new Vec3(to.getX() + 0.5, to.getY(), to.getZ() + 0.5);
        this.fromPos = from;
        this.toPos = to;
        this.stack = stack;
        this.gameTime = gameTime;
    }
}
