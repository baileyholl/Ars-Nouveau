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
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.Tags;
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
        if(level.isClientSide)
            return;
        if(level.getGameTime() % 20 == 0 && this.canAcceptMana()){
            int numSource = (int) BlockPos.betweenClosedStream(this.getBlockPos().below().offset(1, 0, 1), this.getBlockPos().below().offset(-1, 0, -1))
                    .filter(b -> level.getFluidState(b).getType() instanceof LavaFluid).map(b -> level.getFluidState(b))
                    .filter(FluidState::isSource).count();

            for(ItemEntity i : level.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(worldPosition).inflate(1.0))){
                if(i.getItem().getItem() == BlockRegistry.BLAZING_LOG.asItem()){
                    int mana = 100;
                    this.addMana(mana);
                    this.progress += 5;
                    i.getItem().shrink(1);
                    Networking.sendToNearby(level, getBlockPos(),
                            new PacketANEffect(PacketANEffect.EffectType.BURST, i.blockPosition(), new ParticleColor.IntWrapper(255, 0, 0)));
                    break;
                }

            }

            if(numSource > 0){
                this.addMana(numSource*2);
                progress += 1 + numSource/2;
            }
        }

        if(level.getGameTime() % 100 == 0 && getCurrentMana() > 0){
            BlockPos jarPos = ManaUtil.canGiveManaClosest(worldPosition, level, 5);
            if(jarPos != null){
                transferMana(this, (IManaTile) level.getBlockEntity(jarPos));
                ParticleUtil.spawnFollowProjectile(level, this.worldPosition, jarPos);
            }
        }
    }

    public void doRandomAction(){
        if(level.isClientSide)
            return;
        AtomicBoolean set = new AtomicBoolean(false);
        BlockPos.withinManhattanStream(worldPosition, 1, 0,1).forEach(p ->{
            if(!set.get() && level.getBlockState(p).isAir()){
                level.setBlockAndUpdate(p, BlockRegistry.LAVA_LILY.getState(level, p));
                set.set(true);
            }
        });


        BlockPos magmaPos = getBlockInArea(Blocks.MAGMA_BLOCK, 1);
        if(magmaPos != null && progress >= 500){
            level.setBlockAndUpdate(magmaPos, Blocks.LAVA.defaultBlockState());
            progress -= 500;
            return;
        }

        BlockPos stonePos = getBlockInArea(Blocks.STONE, 1);
        if(stonePos != null && progress >= 300){
            level.setBlockAndUpdate(stonePos, Blocks.MAGMA_BLOCK.defaultBlockState());
            progress -= 300;
            return;
        }

        magmaPos = getBlockInArea(Blocks.MAGMA_BLOCK, 1);
        if(magmaPos != null && progress >= 500){
            level.setBlockAndUpdate(magmaPos, Blocks.LAVA.defaultBlockState());
            progress -= 500;
            return;
        }

        stonePos = getTagInArea(Tags.Blocks.STONE, 1);
        if(stonePos != null && progress >= 300){
            level.setBlockAndUpdate(stonePos, Blocks.MAGMA_BLOCK.defaultBlockState());
            progress -= 300;
            return;
        }
    }

    public BlockPos getTagInArea(ITag<Block> block, int range){
        AtomicReference<BlockPos> posFound = new AtomicReference<>();
        BlockPos.betweenClosedStream(worldPosition.offset(range, -1, range), worldPosition.offset(-range, -1, -range)).forEach(blockPos -> {
            blockPos = blockPos.immutable();
            if(posFound.get() == null && level.getBlockState(blockPos).getBlock().is(block))
                posFound.set(blockPos);
        });

        return posFound.get();

    }

    public BlockPos getBlockInArea(Block block, int range){
        AtomicReference<BlockPos> posFound = new AtomicReference<>();
        BlockPos.betweenClosedStream(worldPosition.offset(range, -1, range), worldPosition.offset(-range, -1, -range)).forEach(blockPos -> {
            blockPos = blockPos.immutable();
            if(posFound.get() == null && level.getBlockState(blockPos).getBlock() == block)
                posFound.set(blockPos);
        });

        return posFound.get();

    }

    @Override
    public int getMaxMana() {
        return 1000;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        progress = tag.getInt("progress");
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putInt("progress", progress);
        return super.save(tag);
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
