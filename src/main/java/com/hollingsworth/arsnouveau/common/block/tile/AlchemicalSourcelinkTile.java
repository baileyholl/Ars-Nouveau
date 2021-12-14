package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class AlchemicalSourcelinkTile extends SourcelinkTile{

    public AlchemicalSourcelinkTile(BlockPos pos, BlockState state){
        super(BlockRegistry.ALCHEMICAL_TILE, pos, state);
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
        if(!level.isClientSide && level.getGameTime() % 20 == 0){
            BlockPos potionPos = findNearbyPotion(level, worldPosition);
            if(potionPos != null){
                PotionJarTile tile = (PotionJarTile) level.getBlockEntity(potionPos);
                int mana = 75;
                Set<MobEffect> effectTypes = new HashSet<>();
                for(MobEffectInstance e : tile.getFullEffects()){
                    mana += (e.getDuration() / 50);
                    mana += e.getAmplifier() * 250;
                    mana += 150;
                    effectTypes.add(e.getEffect());
                }
                if(effectTypes.size() > 1)
                    mana *= (1.5 * (effectTypes.size() - 1));
                addSource(mana);
                tile.addAmount(-100);
            }
        }
    }

    public static @Nullable BlockPos findNearbyPotion(Level level, BlockPos worldPosition){
        for(BlockPos p : BlockPos.withinManhattan(worldPosition.below(1), 1, 1,1)){
            if(level.getBlockEntity(p) instanceof PotionJarTile tile) {
                if (tile.getCurrentFill() >= 100) {
                    return p;
                }
            }
        }
        return null;
    }
}
