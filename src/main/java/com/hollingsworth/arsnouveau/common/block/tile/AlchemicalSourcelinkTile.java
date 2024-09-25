package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class AlchemicalSourcelinkTile extends SourcelinkTile {

    public AlchemicalSourcelinkTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ALCHEMICAL_TILE.get(), pos, state);
    }

    @Override
    public int getMaxSource() {
        return 20000;
    }

    @Override
    public int getTransferRate() {
        return 10000;
    }

    @Override
    public void tick() {
        super.tick();
        if (level instanceof ServerLevel && level.getGameTime() % 20 == 0 && this.canAcceptSource()) {
            BlockPos potionPos = findNearbyPotion(level, worldPosition);
            if (potionPos != null && level.getBlockEntity(potionPos) instanceof PotionJarTile tile) {
                int source = 75;
                Set<MobEffect> effectTypes = new HashSet<>();
                for (MobEffectInstance e : tile.getData().getAllEffects()) {
                    source += (e.getDuration() / 50);
                    source += e.getAmplifier() * 250;
                    source += 150;
                    effectTypes.add(e.getEffect().value());
                }
                if (effectTypes.size() > 1) {
                    source *= Math.pow(2.1, effectTypes.size());
                }
                if (source > 0 && canAcceptSource(source) || this.getSource() <= 0) {
                    addSource(source);
                    tile.remove(100);
                }
            }
        }
    }

    public static @Nullable BlockPos findNearbyPotion(Level level, BlockPos worldPosition) {
        for (BlockPos p : BlockPos.withinManhattan(worldPosition.below(1), 1, 1, 1)) {
            if (level.getBlockEntity(p) instanceof PotionJarTile tile) {
                if (tile.getAmount() >= 100) {
                    return p;
                }
            }
        }
        return null;
    }
}
