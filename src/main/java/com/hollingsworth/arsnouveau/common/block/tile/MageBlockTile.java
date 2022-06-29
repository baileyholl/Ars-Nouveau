package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class MageBlockTile extends AnimatedTile implements ITickable, IAnimatable {

    int age;
    public boolean isPermanent;
    public double lengthModifier;
    public ParticleColor color = ParticleColor.defaultParticleColor();

    public MageBlockTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.MAGE_BLOCK_TILE, pos, state);
    }

    @Override
    public void tick() {
        if(isPermanent)
            return;
        if(!level.isClientSide){
            age++;
            //15 seconds
            if(age > (20 * 15 + 20 * 5 * lengthModifier)){
                level.destroyBlock(this.getBlockPos(), false);
                level.removeBlockEntity(this.getBlockPos());
            }
        }
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.age = compound.getInt("age");
        this.color = ParticleColor.deserialize(compound.getCompound("lightColor"));
        this.isPermanent = compound.getBoolean("permanent");
        this.lengthModifier = compound.getDouble("modifier");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("age", IntTag.valueOf(age));
        tag.put("lightColor", color.serialize());
        tag.putBoolean("permanent", isPermanent);
        tag.putDouble("modifier", lengthModifier);
    }

    @Override
    public void registerControllers(AnimationData data) {}

    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
