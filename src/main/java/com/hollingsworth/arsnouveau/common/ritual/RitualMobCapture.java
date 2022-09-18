package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RitualMobCapture extends AbstractRitual {
    @Override
    protected void tick() {
        Level world = getWorld();
        int radius = 3;
        if (world.isClientSide) {
            BlockPos pos = getPos();
            ParticleUtil.spawnRitualAreaEffect(getPos(), getWorld(), rand, getCenterColor(), radius);
        }
        if (!getWorld().isClientSide && world.getGameTime() % 60 == 0) {
            boolean didWorkOnce = false;
            Level level = getWorld();
            BlockPos pos = getPos();
            //Get nearby source jars
            for(BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius))){
                if (level.getBlockState(blockPos).getBlock() == BlockRegistry.MOB_JAR) {
                    MobJarTile tile = (MobJarTile) level.getBlockEntity(blockPos);
                    if(tile.entityTag != null){
                        continue;
                    }
                    for(Entity e : level.getEntities((Entity)null, new AABB(tile.getBlockPos()).inflate(5), (e) -> e instanceof LivingEntity &&
                    !(e instanceof Player))){
                        if(tile.setEntityData(e)){
                            e.remove(Entity.RemovalReason.DISCARDED);
                            EntityFlyingItem followProjectile = new EntityFlyingItem(level, e.position, Vec3.atCenterOf(tile.getBlockPos()), 100, 50, 100);
                            level.addFreshEntity(followProjectile);
                            didWorkOnce = true;
                            break;
                        }
                    }
                }
            }
            if(didWorkOnce){
                this.setNeedsSource(true);
            }
        }
    }

    @Override
    public int getSourceCost() {
        return 500;
    }

    @Override
    public String getLangDescription() {
        return "Captures a nearby entity and places it into any nearby placed Containment Jars. After the first capture, this ritual requires additional source to continue. Mobs and jars must be within 3 blocks of the brazier.";
    }

    @Override
    public String getLangName() {
        return "Containment";
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsNouveau.MODID, RitualLib.CONTAINMENT);
    }
}
