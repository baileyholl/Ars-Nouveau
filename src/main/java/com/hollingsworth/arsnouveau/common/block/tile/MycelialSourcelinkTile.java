package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.concurrent.atomic.AtomicReference;

public class MycelialSourcelinkTile extends SourcelinkTile {
    public MycelialSourcelinkTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public MycelialSourcelinkTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.MYCELIAL_TILE, pos, state);
    }


    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide)
            return;
        if (level.getGameTime() % 40 == 0 && this.canAcceptSource()) {
            for (ItemEntity i : level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition).inflate(1.0))) {
                if (i.getItem().getItem().isEdible()) {
                    int source = getSourceValue(i.getItem());
                    this.addSource(source);
                    ItemStack containerItem = i.getItem().getCraftingRemainingItem();
                    i.getItem().shrink(1);
                    if (!containerItem.isEmpty()) {
                        level.addFreshEntity(new ItemEntity(level, i.getX(), i.getY(), i.getZ(), containerItem));
                    }
                    Networking.sendToNearby(level, getBlockPos(),
                            new PacketANEffect(PacketANEffect.EffectType.BURST, i.blockPosition(), new ParticleColor.IntWrapper(255, 255, 255)));
                }
            }
            for (ArcanePedestalTile i : getSurroundingPedestals()) {
                int sourceValue = getSourceValue(i.getItem(0));
                if (sourceValue > 0) {
                    this.addSource(sourceValue);
                    ItemStack containerItem = i.getItem(0).getCraftingRemainingItem();
                    i.removeItem(0, 1);
                    i.setItem(0, containerItem);
                    Networking.sendToNearby(level, getBlockPos(),
                            new PacketANEffect(PacketANEffect.EffectType.BURST, i.getBlockPos().above(), new ParticleColor.IntWrapper(255, 255, 255)));
                }
            }
        }
    }

    public int getSourceValue(ItemStack i) {
        if (i.getItem().isEdible()) {
            int mana = 0;
            FoodProperties food = i.getItem().getFoodProperties(i, null);
            if(food == null)
                return 0;
            mana += 11 * food.getNutrition();
            mana += 30 * food.getSaturationModifier();
            progress += 1;

            if (i.is(ItemTagProvider.MAGIC_FOOD) || (i.getItem() instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().is(BlockTagProvider.MAGIC_PLANTS))) {
                progress += 4;
                mana += 10;
                mana *= 2;
            }
            return mana;
        }
        return 0;
    }

    @Override
    public void doRandomAction() {
        super.doRandomAction();
        if (level.isClientSide)
            return;

        if (progress > 10) {
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
        if (dirtPos != null && progress >= 25) {
            level.setBlockAndUpdate(dirtPos, Blocks.MYCELIUM.defaultBlockState());
            progress -= 25;
        }

    }

    public BlockPos getBlockInArea(Block block, int range) {
        AtomicReference<BlockPos> posFound = new AtomicReference<>();
        BlockPos.betweenClosedStream(worldPosition.offset(range, -1, range), worldPosition.offset(-range, -1, -range)).forEach(blockPos -> {
            blockPos = blockPos.immutable();
            if (posFound.get() == null && level.getBlockState(blockPos).getBlock() == block)
                posFound.set(blockPos);
        });

        return posFound.get();

    }
}
