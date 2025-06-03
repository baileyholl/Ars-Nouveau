package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.advancement.ANCriteriaTriggers;
import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.entity.PartEntity;


public class RitualMobCapture extends AbstractRitual {
    public ModConfigSpec.IntValue JAR_RANGE;
    public ModConfigSpec.IntValue ENTITY_RANGE;
    @Override
    protected void tick() {
        Level world = getWorld();
        if (world.isClientSide) {
            BlockPos pos = getPos();
            ParticleUtil.spawnRitualAreaEffect(getPos(), getWorld(), rand, getCenterColor(), getJarRange());
        }
        if (!getWorld().isClientSide && world.getGameTime() % 60 == 0) {
            boolean didWorkOnce = false;
            Level level = getWorld();
            BlockPos pos = getPos();
            //Get nearby source jars
            int jarRange = getJarRange();
            for(BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-jarRange, -jarRange, -jarRange), pos.offset(jarRange, jarRange, jarRange))){
                if (level.getBlockState(blockPos).getBlock() instanceof MobJar) {
                    MobJarTile tile = (MobJarTile) level.getBlockEntity(blockPos);
                    if(tile == null || tile.getEntity() != null){
                        continue;
                    }
                    for(Entity e : level.getEntities((Entity)null, new AABB(tile.getBlockPos()).inflate(getEntityRange()), this::canJar)){
                        for (var passenger : e.getPassengers()) {
                            passenger.stopRiding();
                        }
                        if(e instanceof Mob mob && ((Mob) e).isLeashed() && e.shouldBeSaved()){
                            if(mob.isLeashed()){
                                mob.dropLeash(true, true);
                            }
                        }
                        if (e instanceof Raider raider && raider.hasActiveRaid()) {
                            raider.getCurrentRaid().removeFromRaid(raider, false);
                        }
                        if (e instanceof Villager villager) {
                            villager.releasePoi(MemoryModuleType.HOME);
                            villager.releasePoi(MemoryModuleType.JOB_SITE);
                            villager.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
                            villager.releasePoi(MemoryModuleType.MEETING_POINT);
                        }
                        if(tile.setEntityData(e)){
                            e.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
                            EntityFlyingItem followProjectile = new EntityFlyingItem(level, e.position, Vec3.atCenterOf(tile.getBlockPos()), 100, 50, 100);
                            level.addFreshEntity(followProjectile);
                            ParticleUtil.spawnPoof((ServerLevel) level, e.getOnPos().above());
                            didWorkOnce = true;
                            if(e instanceof Starbuncle starbuncle){
                                ANCriteriaTriggers.rewardNearbyPlayers(ANCriteriaTriggers.SHRUNK_STARBY.get(), (ServerLevel) level, starbuncle.blockPosition(), 10);
                            }
                            if(e instanceof LightningBolt bolt){
                                ANCriteriaTriggers.rewardNearbyPlayers(ANCriteriaTriggers.CAUGHT_LIGHTNING.get(), (ServerLevel) level, bolt.blockPosition(), 10);
                            }
                            if(e instanceof ItemEntity item && item.getItem().getItem() == Items.CLOCK){
                                ANCriteriaTriggers.rewardNearbyPlayers(ANCriteriaTriggers.TIME_IN_BOTTLE.get(), (ServerLevel) level, item.blockPosition(), 10);
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
        if(e instanceof PartEntity) {
            return false;
        }
        return e instanceof LivingEntity livingEntity && !(e instanceof Player) && !((LivingEntity) e).isDeadOrDying();
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        JAR_RANGE = builder
                .comment("The range in blocks around the ritual to search for containment jars")
                .defineInRange("jar_range", 3, 1, 20);
        ENTITY_RANGE = builder
                .comment("The range in blocks around each jar to search for entities to capture")
                .defineInRange("entity_range", 5, 1, 20);
    }

    private int getJarRange() {
        return JAR_RANGE.get();
    }
    
    private int getEntityRange() {
        return ENTITY_RANGE.get();
    }

    @Override
    public int getDefaultSourceCost() {
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
        return ArsNouveau.prefix( RitualLib.CONTAINMENT);
    }
}
