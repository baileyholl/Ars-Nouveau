package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.api.ritual.features.IPlaceableFeature;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.*;

public abstract class FeaturePlacementRitual extends AbstractRitual {

    public Map<String, List<BlockPos>> featureMap = new HashMap<>();

    public int checkRadius = 7;

    public int featureIndex = 0;

    public int positionIndex = 0;

    public List<BlockPos> targetPositions = new ArrayList<>();

    public List<IPlaceableFeature> features = new ArrayList<>();

    public BlockPos lowerOffset = BlockPos.ZERO;
    public BlockPos upperOffset = BlockPos.ZERO;

    public abstract void addFeatures(List<IPlaceableFeature> features);

    @Override
    public void onStart(@Nullable Player player) {
        super.onStart(player);
        for (ItemStack i : getConsumedItems()) {
            if (i.is(ItemTagProvider.SOURCE_GEM_TAG)) {
                checkRadius += i.getCount();
            }
        }
        setup();
    }

    public void setup() {
        addFeatures(features);
        for (IPlaceableFeature feature : features) {
            featureMap.put(feature.getFeatureName(), new ArrayList<>());
        }
        targetPositions = getTargetPositions();
    }

    public List<BlockPos> getTargetPositions() {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos pos = getPos();
        Pair<BlockPos, BlockPos> offsets = features.get(featureIndex).getCustomOffsets();
        BlockPos lowerBound = getPos().offset(-checkRadius, 0, -checkRadius).offset(lowerOffset).offset(offsets.getA());
        BlockPos upperBound = getPos().offset(checkRadius, 0, checkRadius).offset(upperOffset).offset(offsets.getB());
        for (BlockPos nextPos : BlockPos.betweenClosed(lowerBound, upperBound)) {
            double x = nextPos.getX() + 0.5;
            double y = nextPos.getY() + 0.5;
            double z = nextPos.getZ() + 0.5;
            double dist = BlockUtil.distanceFrom(new Vec3(x, y, z), new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
            if (dist <= checkRadius) {
                positions.add(nextPos.immutable());
            }
        }
        Collections.shuffle(positions);
        return positions;
    }


    @Override
    public void tick() {
        if (getWorld().isClientSide) {
            return;
        }

        while (true) {
            if (positionIndex >= targetPositions.size()) {
                featureIndex++;
                if (featureIndex >= features.size()) {
                    setFinished();
                    return;
                }
                targetPositions = getTargetPositions();
                positionIndex = 0;
            }
            BlockPos targetPos = targetPositions.get(positionIndex);
            IPlaceableFeature feature = features.get(featureIndex);
            if (isEnoughBlocksFrom(feature.getFeatureName(), targetPos, feature.distanceFromOthers()) && feature.onPlace(getWorld(), targetPos, this, tile)) {
                featureMap.computeIfAbsent(feature.getFeatureName(), k -> new ArrayList<>()).add(targetPos.immutable());
                return;
            }
            positionIndex++;
        }
    }

    public boolean isEnoughBlocksFrom(String feature, BlockPos targetPos, double dist) {
        if (!featureMap.containsKey(feature)) {
            return true;
        }
        for (BlockPos pos : featureMap.get(feature)) {
            if (BlockUtil.distanceFrom(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), new Vec3(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5)) <= dist) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return stack.is(ItemTagProvider.SOURCE_GEM_TAG);
    }

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag tag) {
        super.read(provider, tag);
        featureIndex = tag.getInt("featureIndex");
        positionIndex = tag.getInt("positionIndex");
        checkRadius = tag.getInt("checkRadius");
        setup();
    }

    @Override
    public void write(HolderLookup.Provider provider, CompoundTag tag) {
        super.write(provider, tag);
        tag.putInt("featureIndex", featureIndex);
        tag.putInt("positionIndex", positionIndex);
        tag.putInt("checkRadius", checkRadius);
    }
}
