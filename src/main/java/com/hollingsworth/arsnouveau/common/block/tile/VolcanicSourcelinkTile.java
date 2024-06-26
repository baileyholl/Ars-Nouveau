package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import software.bernie.geckolib.animatable.GeoAnimatable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VolcanicSourcelinkTile extends SourcelinkTile implements GeoAnimatable {

    public VolcanicSourcelinkTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.VOLCANIC_TILE.get(), pos, state);
    }

    @Override
    public int getTransferRate() {
        return 1000;
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide)
            return;
        if (level.getGameTime() % 20 == 0 && this.canAcceptSource()) {
            for (ItemEntity i : level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition).inflate(1.0))) {
                int source = getSourceValue(i.getItem());
                if (source > 0 && (canConsumeEntireItem(i.getItem()) || this.getSource() <= 0)) {
                    this.addSource(source);
                    ItemStack containerItem = i.getItem().getCraftingRemainingItem();
                    i.getItem().shrink(1);
                    if (!containerItem.isEmpty()) {
                        level.addFreshEntity(new ItemEntity(level, i.getX(), i.getY(), i.getZ(), containerItem));
                    }
                    Networking.sendToNearbyClient(level, getBlockPos(),
                            new PacketANEffect(PacketANEffect.EffectType.BURST, i.blockPosition(), new ParticleColor(255, 0, 0)));
                    return;
                }
            }
            for (ArcanePedestalTile i : getSurroundingPedestals()) {
                int sourceValue = getSourceValue(i.getItem(0));
                if (sourceValue > 0 && (canConsumeEntireItem(i.getItem(0)) || this.getSource() <= 0)) {
                    this.addSource(sourceValue);
                    ItemStack containerItem = i.getItem(0).getCraftingRemainingItem();
                    i.removeItem(0, 1);
                    i.setItem(0, containerItem);
                    Networking.sendToNearbyClient(level, getBlockPos(),
                            new PacketANEffect(PacketANEffect.EffectType.BURST, i.getBlockPos().above(), new ParticleColor(255, 0, 0)));
                }
            }
        }
    }

    public boolean canConsumeEntireItem(ItemStack i){
        int sourceValue = getSourceValue(i);
        return this.getSource() + sourceValue <= this.getMaxSource();
    }

    public int getSourceValue(ItemStack i) {
        int source = 0;
        int progress = 0;
        int burnTime = i.getBurnTime(RecipeType.SMELTING);
        if (burnTime > 0) {
            source = burnTime / 12;
            progress = 1;
        }
        if (i.getItem() == BlockRegistry.BLAZING_LOG.asItem()) {
            source += 100;
            progress += 5;
        } else if (i.is(ItemTagProvider.ARCHWOOD_LOG_TAG)) {
            source += 50;
            progress += 3;
        }
        if (i.getItem() == ItemsRegistry.FIRE_ESSENCE.get()) {
            source = 2000;
        }
        this.progress += progress;
        setChanged();
        return source;
    }

    public void doRandomAction() {
        if (level.isClientSide)
            return;
        AtomicBoolean set = new AtomicBoolean(false);

        BlockPos magmaPos = getBlockInArea(Blocks.MAGMA_BLOCK, 1);
        if (magmaPos != null && progress >= 200) {
            level.setBlockAndUpdate(magmaPos, Blocks.LAVA.defaultBlockState());
            progress -= 200;
            return;
        }

        BlockPos stonePos = getBlockInArea(Blocks.STONE, 1);
        if (stonePos != null && progress >= 150) {
            level.setBlockAndUpdate(stonePos, Blocks.MAGMA_BLOCK.defaultBlockState());
            progress -= 150;
            return;
        }

        stonePos = getTagInArea(Tags.Blocks.STONES, 1);
        if (stonePos != null && progress >= 150) {
            level.setBlockAndUpdate(stonePos, Blocks.MAGMA_BLOCK.defaultBlockState());
            progress -= 150;
        }
    }

    public BlockPos getTagInArea(TagKey<Block> block, int range) {
        AtomicReference<BlockPos> posFound = new AtomicReference<>();
        BlockPos.betweenClosedStream(worldPosition.offset(range, -1, range), worldPosition.offset(-range, -1, -range)).forEach(blockPos -> {
            blockPos = blockPos.immutable();
            if (posFound.get() == null && level.getBlockState(blockPos).is(block))
                posFound.set(blockPos);
        });

        return posFound.get();

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

    @Override
    public int getMaxSource() {
        return 5000;
    }

}
