package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.List;
import java.util.Optional;

public class RitualHealing extends AbstractRitual {
    @Override
    protected void tick() {
        if (getWorld().isClientSide) {
            ParticleUtil.spawnRitualAreaEffect(getPos(), getWorld(), rand, getCenterColor(), 5);
        } else {
            if (getWorld().getGameTime() % 100 == 0) {
                List<LivingEntity> entities = getWorld().getEntitiesOfClass(LivingEntity.class, new AABB(getPos()).inflate(5));
                Optional<LivingEntity> player = entities.stream().filter(e -> e instanceof Player).findFirst();

                boolean didWorkOnce = false;
                for (LivingEntity a : entities) {
                    if (a instanceof ZombieVillager zv) {
                        zv.startConverting(player.map(Entity::getUUID).orElse(null), 0);
                        didWorkOnce = true;
                        continue;
                    }

                    if (a.getHealth() < a.getMaxHealth() || a.isInvertedHealAndHarm()) {
                        if (a.isInvertedHealAndHarm()) {
                            FakePlayer player1 = ANFakePlayer.getPlayer((ServerLevel) getWorld());
                            a.hurt(getWorld().damageSources().playerAttack(player1), 10.0f);
                        } else {
                            a.heal(10.0f);
                        }
                        didWorkOnce = true;
                    }
                }
                if (didWorkOnce)
                    setNeedsSource(true);
            }
        }
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix(RitualLib.RESTORATION);
    }

    @Override
    public ParticleColor getCenterColor() {
        return ParticleColor.makeRandomColor(20, 240, 240, rand);
    }

    @Override
    public String getLangName() {
        return "Restoration";
    }

    @Override
    public String getLangDescription() {
        return "Heals nearby entities or harms undead over time. Additionally, Zombie Villagers will be instantly cured, and the resulting villager will offer discounts if a player was nearby. This ritual requires source to operate.";
    }

    @Override
    public int getSourceCost() {
        return 200;
    }
}
