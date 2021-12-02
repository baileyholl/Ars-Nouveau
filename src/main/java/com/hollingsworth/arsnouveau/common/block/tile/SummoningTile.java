package com.hollingsworth.arsnouveau.common.block.tile;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.Nullable;

public class SummoningTile extends BlockEntity implements TickableBlockEntity {
    public int tickCounter; // just for animation, not saved
    public boolean converted;

    public static final BooleanProperty CONVERTED = BooleanProperty.create("converted");

    public SummoningTile(BlockEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    @Override
    public void tick() {
        if(level.isClientSide)
            return;

        if (!converted) {
            convertedEffect();
            return;
        }
    }

    public void convertedEffect() {
        tickCounter++;
    }

    @Override
    public void load(BlockState state, CompoundTag compound) {
        super.load(state, compound);
        this.converted = compound.getBoolean("converted");
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putBoolean("converted", converted);
        return super.save(compound);
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(level.getBlockState(worldPosition),pkt.getTag());
    }
}
