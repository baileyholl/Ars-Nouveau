package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.datagen.Recipes;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.atomic.AtomicReference;

public class MycelialSourcelinkTile extends SourcelinkTile{
    public MycelialSourcelinkTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public MycelialSourcelinkTile(){
        super(BlockRegistry.MYCELIAL_TILE);
    }


    @Override
    public void tick() {
        super.tick();
        if(level.isClientSide)
            return;
        if(level.getGameTime() % 40 == 0 && this.canAcceptMana()){
            for(ItemEntity i : level.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(worldPosition).inflate(1.0))){
                if(i.getItem().getItem().isEdible()){
                    int mana = 0;
                    Food food = i.getItem().getItem().getFoodProperties();
                    mana += 11 * food.getNutrition();
                    mana += 30 * food.getSaturationModifier();
                    progress += 1;
                    if(i.getItem().getItem().is(Recipes.MAGIC_FOOD) || (i.getItem().getItem() instanceof BlockItem && Recipes.MAGIC_PLANTS.contains(((BlockItem) i.getItem().getItem()).getBlock()))){
                        progress += 4;
                        mana += 10;
                        mana *= 2;
                    }
                    this.addMana(mana);
                    ItemStack containerItem = i.getItem().getContainerItem();
                    i.getItem().shrink(1);
                    if(!containerItem.isEmpty()){
                        level.addFreshEntity(new ItemEntity(level, i.getX(), i.getY(), i.getZ(), containerItem));
                    }
                    Networking.sendToNearby(level, getBlockPos(),
                            new PacketANEffect(PacketANEffect.EffectType.BURST, i.blockPosition(), new ParticleColor.IntWrapper(255, 0, 0)));
                }
            }
        }
    }

    @Override
    public void doRandomAction() {
        super.doRandomAction();
        if(level.isClientSide)
            return;

        if(progress > 10) {
            for (BlockPos p : BlockPos.withinManhattan(worldPosition, 1, 0, 1)) {
                if (level.getBlockState(p).isAir() && level.getBlockState(p.below()).getBlock() == Blocks.MYCELIUM) {
                    level.setBlockAndUpdate(p, level.getRandom().nextFloat() > 0.5 ? Blocks.BROWN_MUSHROOM.defaultBlockState() : Blocks.RED_MUSHROOM.defaultBlockState());
                    progress -= 10;
                    break;
                }
            }
        }

        BlockPos dirtPos = getBlockInArea(Blocks.DIRT, 1);
        dirtPos = dirtPos == null ? getBlockInArea(Blocks.GRASS_BLOCK, 1) : dirtPos;
        if(dirtPos != null && progress >= 25){
            level.setBlockAndUpdate(dirtPos, Blocks.MYCELIUM.defaultBlockState());
            progress -= 25;
            return;
        }

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
}
