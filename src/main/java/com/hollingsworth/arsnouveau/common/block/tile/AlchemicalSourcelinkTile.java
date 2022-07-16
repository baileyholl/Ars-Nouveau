package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class AlchemicalSourcelinkTile extends SourcelinkTile{
    public AlchemicalSourcelinkTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public AlchemicalSourcelinkTile(){
        super(BlockRegistry.ALCHEMICAL_TILE);
    }

    @Override
    public int getMaxMana() {
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
                Set<Effect> effectTypes = new HashSet<>();
                for(EffectInstance e : tile.getFullEffects()){
                    mana += (e.getDuration() / 50);
                    mana += e.getAmplifier() * 250;
                    mana += 150;
                    effectTypes.add(e.getEffect());
                }
                if(effectTypes.size() > 1)
                    mana *= (1.5 * (effectTypes.size() - 1));
                addMana(mana);
                tile.addAmount(-100);
            }
        }
    }

    public static @Nullable BlockPos findNearbyPotion(World level, BlockPos worldPosition){
        for(BlockPos p : BlockPos.withinManhattan(worldPosition.below(1), 1, 1,1)){
            if(level.getBlockEntity(p) instanceof PotionJarTile) {
                PotionJarTile tile = (PotionJarTile) level.getBlockEntity(p);
                if (tile.getCurrentFill() >= 100 && tile.getPotion() != Potions.EMPTY && tile.getPotion() != Potions.WATER) {
                    return p;
                }
            }
        }
        return null;
    }
}
