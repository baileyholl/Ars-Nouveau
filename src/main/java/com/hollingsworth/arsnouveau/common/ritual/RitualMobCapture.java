package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
                    if(tile == null || tile.getEntity() != null){
                        continue;
                    }
                    for(Entity e : level.getEntities((Entity)null, new AABB(tile.getBlockPos()).inflate(5), this::canJar)){
                        if(tile.setEntityData(e)){
                            e.remove(Entity.RemovalReason.DISCARDED);
                            EntityFlyingItem followProjectile = new EntityFlyingItem(level, e.position, Vec3.atCenterOf(tile.getBlockPos()), 100, 50, 100);
                            level.addFreshEntity(followProjectile);
                            ParticleUtil.spawnPoof((ServerLevel) level, e.getOnPos().above());
                            didWorkOnce = true;
                            if(e instanceof Starbuncle starbuncle){
                                ANCriteriaTriggers.rewardNearbyPlayers(ANCriteriaTriggers.SHRUNK_STARBY, (ServerLevel) level, starbuncle.blockPosition(), 10);
                            }
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

    public boolean canJar(Entity e){
        if(e.getType().is(EntityTags.JAR_WHITELIST))
            return true;
        if(e.getType().is(EntityTags.JAR_BLACKLIST)){
            return false;
        }
        return e instanceof LivingEntity livingEntity && !(e instanceof Player) && !((LivingEntity) e).isDeadOrDying();
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
