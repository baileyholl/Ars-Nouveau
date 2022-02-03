package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.datagen.Recipes;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import software.bernie.geckolib3.core.IAnimatable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VolcanicSourcelinkTile extends SourcelinkTile implements IAnimatable {

    public VolcanicSourcelinkTile() {
        super(BlockRegistry.VOLCANIC_TILE);
    }

    @Override
    public int getTransferRate() {
        return 1000;
    }

    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide)
            return;
        if(level.getGameTime() % 20 == 0 && this.canAcceptMana()){
            for(ItemEntity i : level.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(worldPosition).inflate(1.0))){
                int source = getSourceValue(i.getItem());
                if(source > 0) {
                    this.addMana(source);
                    ItemStack containerItem = i.getItem().getContainerItem();
                    i.getItem().shrink(1);
                    if(!containerItem.isEmpty()){
                        level.addFreshEntity(new ItemEntity(level, i.getX(), i.getY(), i.getZ(), containerItem));
                    }
                    Networking.sendToNearby(level, getBlockPos(),
                            new PacketANEffect(PacketANEffect.EffectType.BURST, i.blockPosition(), new ParticleColor.IntWrapper(255, 0, 0)));
                    return;
                }
            }
            for(ArcanePedestalTile i : getSurroundingPedestals()){
                int sourceValue = getSourceValue(i.getItem(0));
                if(sourceValue > 0){
                    this.addMana(sourceValue);
                    ItemStack containerItem = i.getItem(0).getContainerItem();
                    i.removeItem(0, 1);
                    i.setItem(0, containerItem);
                    Networking.sendToNearby(level, getBlockPos(),
                            new PacketANEffect(PacketANEffect.EffectType.BURST, i.getBlockPos().above(), new ParticleColor.IntWrapper(255, 0, 0)));
                }
            }
        }
    }

    public int getSourceValue(ItemStack i){
        int source = 0;
        int progress = 0;
        int burnTime = ForgeHooks.getBurnTime(i, null) ;
        if(burnTime > 0){
            source = burnTime / 12;
            progress = 1;
        }
        if(i.getItem().getItem() == BlockRegistry.BLAZING_LOG.asItem()){
            source += 100;
            progress += 5;
        }else if(i.getItem().getItem().is(Recipes.ARCHWOOD_LOG_TAG)){
            source += 50;
            progress += 3;
        }
        this.progress += progress;
        return source;
    }

    public void doRandomAction(){
        if(level.isClientSide)
            return;
        AtomicBoolean set = new AtomicBoolean(false);
        BlockPos.withinManhattanStream(worldPosition, 1, 0,1).forEach(p ->{
            if(!set.get() && level.getBlockState(p).isAir() && (level.getFluidState(p.below()).getType() == Fluids.LAVA || level.getFluidState(p.below()).getType() == Fluids.FLOWING_LAVA)){
                level.setBlockAndUpdate(p, BlockRegistry.LAVA_LILY.getState(level, p));
                set.set(true);
            }
        });


        BlockPos magmaPos = getBlockInArea(Blocks.MAGMA_BLOCK, 1);
        if(magmaPos != null && progress >= 200){
            level.setBlockAndUpdate(magmaPos, Blocks.LAVA.defaultBlockState());
            progress -= 200;
            return;
        }

        BlockPos stonePos = getBlockInArea(Blocks.STONE, 1);
        if(stonePos != null && progress >= 150){
            level.setBlockAndUpdate(stonePos, Blocks.MAGMA_BLOCK.defaultBlockState());
            progress -= 150;
            return;
        }

        stonePos = getTagInArea(Tags.Blocks.STONE, 1);
        if(stonePos != null && progress >= 150){
            level.setBlockAndUpdate(stonePos, Blocks.MAGMA_BLOCK.defaultBlockState());
            progress -= 150;
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
        return 5000;
    }

}
