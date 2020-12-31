package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.api.mana.IManaTile;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VolcanicTile extends AbstractManaTile implements IAnimatable {
    public VolcanicTile() {
        super(BlockRegistry.VOLCANIC_TILE);
    }
    AnimationFactory manager = new AnimationFactory(this);

    int progress;


    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "rotate_controller", 0, this::idlePredicate));
        animationData.addAnimationController(new AnimationController(this, "hover_controller", 0, this::hover));
        animationData.addAnimationController(new AnimationController(this, "gem_controller", 0, this::gem));
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    @Override
    public int getTransferRate() {
        return 1000;
    }

    @Override
    public void tick() {
        if(world.isRemote)
            return;
        if(world.getGameTime() % 40 == 0 && this.canAcceptMana()){
            int numSource = (int) BlockPos.getAllInBox(this.getPos().down().add(1, 0, 1), this.getPos().down().add(-1, 0, -1))
                    .filter(b -> world.getFluidState(b).getFluid() instanceof LavaFluid).map(b -> world.getFluidState(b))
                    .filter(FluidState::isSource).count();

            for(ItemEntity i : world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos).grow(1.0))){
                if(i.getItem().getItem() == BlockRegistry.BLAZING_LOG.asItem()){
                    int mana = 100;
                    this.addMana(mana);
                    this.progress += 5;
                    i.getItem().shrink(1);
                    Networking.sendToNearby(world, getPos(),
                            new PacketANEffect(PacketANEffect.EffectType.BURST, i.getPosition(), new ParticleColor.IntWrapper(255, 0, 0)));
                    break;
                }

            }

            if(numSource > 0){
                this.addMana(numSource);
                progress += 1 + numSource/2;
            }
        }

        if(world.getGameTime() % 100 == 0 && getCurrentMana() > 0){
            BlockPos jarPos = ManaUtil.canGiveManaClosest(pos, world, 5);
            if(jarPos != null){
                transferMana(this, (IManaTile) world.getTileEntity(jarPos));
                ParticleUtil.spawnFollowProjectile(world, this.pos, jarPos);
            }
        }
    }

    public void doRandomAction(){
        if(world.isRemote)
            return;
        AtomicBoolean set = new AtomicBoolean(false);
        BlockPos.getProximitySortedBoxPositions(pos, 1, 0,1).forEach(p ->{
            if(!set.get() && world.getBlockState(p).isAir()){
                world.setBlockState(p, BlockRegistry.LAVA_LILY.getState(world, p));
                set.set(true);
            }
        });


        BlockPos magmaPos = getBlockInArea(Blocks.MAGMA_BLOCK, 1);
        if(magmaPos != null && progress >= 500){
            world.setBlockState(magmaPos, Blocks.LAVA.getDefaultState());
            progress -= 500;
            return;
        }

        BlockPos stonePos = getBlockInArea(Blocks.STONE, 1);
        if(stonePos != null && progress >= 300){
            world.setBlockState(stonePos, Blocks.MAGMA_BLOCK.getDefaultState());
            progress -= 300;
            return;
        }

        magmaPos = getBlockInArea(Blocks.MAGMA_BLOCK, 1);
        if(magmaPos != null && progress >= 500){
            world.setBlockState(magmaPos, Blocks.LAVA.getDefaultState());
            progress -= 500;
            return;
        }

        stonePos = getBlockInArea(Blocks.STONE, 1);
        if(stonePos != null && progress >= 300){
            world.setBlockState(stonePos, Blocks.MAGMA_BLOCK.getDefaultState());
            progress -= 300;
            return;
        }
    }

    public BlockPos getBlockInArea(Block block, int range){
        AtomicReference<BlockPos> posFound = new AtomicReference<>();
        BlockPos.getAllInBox(pos.add(range, -1, range), pos.add(-range, -1, -range)).forEach(blockPos -> {
            blockPos = blockPos.toImmutable();
            if(posFound.get() == null && world.getBlockState(blockPos).getBlock() == block)
                posFound.set(blockPos);
        });

        return posFound.get();

    }

    @Override
    public int getMaxMana() {
        return 1000;
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        progress = tag.getInt("progress");
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putInt("progress", progress);
        return super.write(tag);
    }

    private <E extends TileEntity  & IAnimatable > PlayState idlePredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("core_rotation", true));
        return PlayState.CONTINUE;
    }
    private <E extends TileEntity  & IAnimatable > PlayState hover(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("gem_hover", true));
        return PlayState.CONTINUE;
    }
    private <E extends TileEntity  & IAnimatable > PlayState gem(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("gem_rotation", true));
        return PlayState.CONTINUE;
    }

}
