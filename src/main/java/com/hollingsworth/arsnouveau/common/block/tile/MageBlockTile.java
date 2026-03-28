package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.util.IWololoable;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class MageBlockTile extends ModdedTile implements ITickable, IDispellable, IWololoable {

    int age;
    public boolean isPermanent;
    public double lengthModifier;
    public ParticleColor color = ParticleColor.defaultParticleColor();

    public MageBlockTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.MAGE_BLOCK_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if (isPermanent)
            return;
        if (!level.isClientSide()) {
            age++;
            //15 seconds
            if (age > (20 * 15 + 20 * 5 * lengthModifier)) {
                level.destroyBlock(this.getBlockPos(), false);
                level.removeBlockEntity(this.getBlockPos());
            }
        }
    }


    @Override
    public void handleUpdateTag(ValueInput tag) {
        super.handleUpdateTag(tag);
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 8);
    }

    @Override
    protected void loadAdditional(ValueInput compound) {
        super.loadAdditional(compound);
        this.age = compound.getIntOr("age", 0);
        this.color = compound.read("lightColor", ParticleColor.CODEC.codec()).orElseGet(ParticleColor::defaultParticleColor);
        this.isPermanent = compound.getBooleanOr("permanent", false);
        this.lengthModifier = compound.getDoubleOr("modifier", 0.0);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putInt("age", age);
        tag.store("lightColor", ParticleColor.CODEC.codec(), color);
        tag.putBoolean("permanent", isPermanent);
        tag.putDouble("modifier", lengthModifier);
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        level.destroyBlock(this.getBlockPos(), false);
        level.removeBlockEntity(this.getBlockPos());
        return true;
    }

    @Override
    public void setColor(ParticleColor color) {
        this.color = color;
        updateBlock();
    }

    @Override
    public ParticleColor getColor() {
        return color;
    }
}
