package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class RitualMobCapture extends AbstractRitual {
    @Override
    protected void tick() {
        if (!getWorld().isClientSide) {
            Level level = getWorld();
            BlockPos pos = getPos();
            //Get nearby source jars
            int radius = 3;
            for(BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius))){
                if (level.getBlockState(blockPos).getBlock() == BlockRegistry.MOB_JAR) {
                    MobJarTile tile = (MobJarTile) level.getBlockEntity(blockPos);
                    if(tile.entityTag != null){
                        continue;
                    }
                    for(Entity e : level.getEntities((Entity)null, new AABB(tile.getBlockPos()).inflate(5), (e) -> !(e instanceof Player))){
                        if(tile.setEntityData(e)){
                            e.remove(Entity.RemovalReason.DISCARDED);
                            break;
                        }
                    }
                }
            }
        }
        setFinished();
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsNouveau.MODID, "mob_capture");
    }
}
