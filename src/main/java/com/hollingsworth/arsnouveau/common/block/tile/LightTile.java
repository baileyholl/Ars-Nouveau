package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class LightTile extends ModdedTile {

    public int red = 255;
    public int green = 125;
    public int blue = 255;

    public LightTile(BlockPos pos, BlockState state) {
        this(BlockRegistry.LIGHT_TILE, pos, state);
    }

    public LightTile(BlockEntityType<?> lightTile, BlockPos pos, BlockState state) {
        super(lightTile, pos, state);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.red = nbt.getInt("red");
        this.red = red > 0 ? red : 255;
        this.green = nbt.getInt("green");
        green = this.green > 0 ? green : 125;
        this.blue = nbt.getInt("blue");
        blue = this.blue > 0 ? blue : 255;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putInt("red", red);
        tag.putInt("green", green);
        tag.putInt("blue", blue);
    }
}
