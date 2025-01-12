package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RitualHarvest extends AbstractRitual {


    @Override
    protected void tick() {
        Level world = getWorld();
        BlockPos pos = getPos();
        if (world != null && world.isClientSide) {
            ParticleUtil.spawnRitualAreaEffect(getPos(), getWorld(), rand, getCenterColor(), 4);
            return;
        }
        if (world == null || pos == null || world.getGameTime() % 200 != 0)
            return;
        int range = 4;
        boolean hasPlayedSound = false;
        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(range, -1, range), pos.offset(-range, 1, -range))) {
            BlockState state = world.getBlockState(blockpos);

            if (state.getBlock() instanceof FarmBlock || world.getBlockState(blockpos.above()).getBlock() instanceof CropBlock || world.getBlockState(blockpos.above()).getBlock() instanceof NetherWartBlock || world.getBlockState(blockpos.above()).is(BlockTagProvider.HARVEST_STEMS)) {
                blockpos = blockpos.above();
                state = world.getBlockState(blockpos);
            }
            if (state.getBlock() instanceof NetherWartBlock) {
                if (harvestNetherwart(blockpos, state, world) && !hasPlayedSound) {
                    world.playSound(null, getPos(), SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1, 1);
                    hasPlayedSound = true;
                }
                continue;
            }

            if (state.getBlock() instanceof CocoaBlock) {
                if (harvestPods(blockpos, state, world) && !hasPlayedSound) {
                    world.playSound(null, getPos(), SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1, 1);
                    hasPlayedSound = true;
                }
                continue;
            }

            if (state.is(BlockTagProvider.HARVEST_STEMS) && state.getBlock() == world.getBlockState(blockpos.below()).getBlock()) {
                processAndSpawnDrops(blockpos, state, world, false);
                BlockUtil.destroyBlockSafely(world, blockpos, false, null);
                continue;
            }

            if (!(state.getBlock() instanceof CropBlock))
                continue;
            CropBlock cropsBlock = (CropBlock) world.getBlockState(blockpos).getBlock();

            if (!cropsBlock.isMaxAge(state) || !(world instanceof ServerLevel))
                continue;

            if (processAndSpawnDrops(blockpos, state, world, true) && !hasPlayedSound) {
                world.playSound(null, getPos(), SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1, 1);
                hasPlayedSound = true;
            }
            world.setBlockAndUpdate(blockpos, cropsBlock.getStateForAge(1));
        }
    }

    public boolean harvestNetherwart(BlockPos pos, BlockState state, Level world) {
        if (state.getValue(NetherWartBlock.AGE) != 3)
            return false;
        processAndSpawnDrops(pos, state, world, true);
        world.setBlockAndUpdate(pos, state.setValue(NetherWartBlock.AGE, 0));
        setNeedsSource(true);
        return true;
    }

    public boolean harvestPods(BlockPos pos, BlockState state, Level world) {
        if (state.getValue(CocoaBlock.AGE) != 2)
            return false;
        processAndSpawnDrops(pos, state, world, true);
        world.setBlockAndUpdate(pos, state.setValue(CocoaBlock.AGE, 0));
        setNeedsSource(true);
        return true;
    }

    public boolean processAndSpawnDrops(BlockPos pos, BlockState state, Level world, boolean takeSeedToReplant) {
        List<ItemStack> cropDrops = Block.getDrops(state, (ServerLevel) world, pos, world.getBlockEntity(pos));

        if (takeSeedToReplant)
            for (ItemStack i : cropDrops) {
                if (i.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == state.getBlock()) {
                    i.shrink(1);
                    break;
                }
            }
        InventoryManager manager = tile.getInventoryManager();
        cropDrops.forEach(d -> {
            if (d.isEmpty() || d.getItem() == BlockRegistry.MAGE_BLOOM_CROP.asItem()) {
                return;
            }
            d = manager.insertStack(d);
            if (!d.isEmpty()) {
                world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), d));
            }
        });
        setNeedsSource(true);
        return true;
    }

    @Override
    public String getLangDescription() {
        return "Casts the Harvest effect on nearby crops. Consumes source each time a set of crops are harvested. If an inventory is adjacent to the brazier, the items will be deposited in them before dropping on the ground.";
    }

    @Override
    public String getLangName() {
        return "Harvest";
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( RitualLib.HARVEST);
    }

    @Override
    public int getSourceCost() {
        return 100;
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(50, 180, 50);
    }
}
