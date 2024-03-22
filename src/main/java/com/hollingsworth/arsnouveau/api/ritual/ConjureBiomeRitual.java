package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class ConjureBiomeRitual extends AbstractRitual {
    public int radius = 7;
    public int blocksPlaced;
    public int blocksBeforeSourceNeeded = 5;
    public ResourceKey<Biome> biome;
    public ManhattenTracker tracker;

    public ConjureBiomeRitual(ResourceKey<Biome> biome){
        super();
        this.biome = biome;
    }

    @Override
    public void onStart(@Nullable Player player) {
        super.onStart(player);
        if(getWorld().isClientSide){
            return;
        }
        for(ItemStack i : getConsumedItems()){
            if(i.is(ItemTagProvider.SOURCE_GEM_TAG)) {
                radius += i.getCount();
            }
        }
        tracker = new ManhattenTracker(getPos().below(3), radius, 2, radius);
    }

    @Override
    protected void tick() {
        if(getWorld().isClientSide){
            return;
        }
        for(int i = 0; i < radius; i++) {
            BlockPos pos = getPos();
            BlockPos nextPos = tracker.computeNext();
            if (nextPos == null) {
                setFinished();
                return;
            }
            double x = nextPos.getX() + 0.5;
            double y = nextPos.getY() + 0.5;
            double z = nextPos.getZ() + 0.5;
            double dist = BlockUtil.distanceFrom(new Vec3(x, y, z), new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
            if (dist <= radius && getWorld().getBlockState(nextPos).canBeReplaced()) {
                BlockState state = stateForPos(nextPos);
                setState(nextPos, state);
                RitualUtil.changeBiome(getWorld(), nextPos, biome);
                getWorld().playSound(null, nextPos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
                blocksPlaced++;
                if (blocksPlaced >= blocksBeforeSourceNeeded){
                    blocksPlaced = 0;
                    setNeedsSource(true);
                }
                return;
            }
        }
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return stack.is(ItemTagProvider.SOURCE_GEM_TAG);
    }

    @Override
    public int getSourceCost() {
        return 50;
    }

    public abstract BlockState stateForPos(BlockPos placePos);

    public void setState(BlockPos pos, BlockState state){
        getWorld().setBlock(pos, state, 2);
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);
        if(tag.contains("tracker")){
            tracker = new ManhattenTracker(tag.getCompound("tracker"));
        }
        radius = tag.getInt("radius");
    }

    @Override
    public void write(CompoundTag tag) {
        super.write(tag);
        if(tracker != null){
            tag.put("tracker", tracker.serialize(new CompoundTag()));
        }
        tag.putInt("radius", radius);
    }
}
