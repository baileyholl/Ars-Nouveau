package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleSparkleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Random;

public class SummoningCrystalTile extends AbstractManaTile implements IAnimatable, ITickable {

    public boolean isOff;

    public SummoningCrystalTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SUMMONING_CRYSTAL_TILE, pos, state);
    }

    AnimationFactory manager = new AnimationFactory(this);
    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "rotate_controller", 0, this::idlePredicate));
    }

    private <E extends BlockEntity & IAnimatable > PlayState idlePredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("summoning_crystal", true));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    @Override
    public int getTransferRate() {
        return 0;
    }

    public ItemStack insertItem(ItemStack stack){
       return BlockUtil.insertItemAdjacent(level, worldPosition, stack);
    }

    public boolean enoughMana(int manaCost){
        return ManaUtil.hasManaNearby(worldPosition, level, 7, manaCost);
    }

    public boolean removeManaAround(int manaCost){
        return ManaUtil.takeManaNearbyWithParticles(worldPosition, level, 7, manaCost) != null;
    }

    @Override
    public void tick() {
        Random rand = level.getRandom();
        if(level.isClientSide && rand.nextInt(6) == 0){
            for(int i = 0; i < 10; i++){
                level.addParticle(ParticleSparkleData.createData(ParticleUtil.defaultParticleColor(), 0.05f, 60),
                        worldPosition.getX()  +ParticleUtil.inRange(-0.5, 0.5) +0.5 , worldPosition.getY() +ParticleUtil.inRange(-1, 1) , worldPosition.getZ() +ParticleUtil.inRange(-0.5, 0.5) +0.5,
                        ParticleUtil.inRange(-0.03, 0.03),  ParticleUtil.inRange(0.01, 0.5), ParticleUtil.inRange(-0.03, 0.03));
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        isOff = tag.getBoolean("is_off");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putBoolean("is_off", isOff);
    }
}
