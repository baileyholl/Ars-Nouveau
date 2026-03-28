package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class TempLightTile extends LightTile {

    int age;
    public double lengthModifier;

    public TempLightTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.T_LIGHT_TILE.get(), pos, state);
    }

    @Override
    public void loadAdditional(@NotNull ValueInput nbt) {
        super.loadAdditional(nbt);
        this.age = nbt.getIntOr("age", 0);
        this.lengthModifier = nbt.getDoubleOr("modifier", 0.0);
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putDouble("modifier", lengthModifier);
        tag.putInt("age", age);
    }

    @Override
    public void tick(Level level, BlockState state, BlockPos pos) {
        super.tick(level, state, pos);
        if (!level.isClientSide()) {
            age++;
            //15 seconds
            if (age > (20 * 15 + 20 * 5 * lengthModifier)) {
                level.destroyBlock(this.getBlockPos(), false);
                level.removeBlockEntity(this.getBlockPos());
            }
        }
    }
}
