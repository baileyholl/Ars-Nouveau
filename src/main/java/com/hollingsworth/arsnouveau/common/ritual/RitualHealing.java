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
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.util.FakePlayer;
import java.util.List;
import java.util.Optional;

public class RitualHealing extends AbstractRitual {
    public ModConfigSpec.DoubleValue RANGE;
    public ModConfigSpec.DoubleValue HEAL_AMOUNT;
    public ModConfigSpec.DoubleValue HARM_AMOUNT;
    @Override
    protected void tick() {
        if (getWorld().isClientSide) {
            ParticleUtil.spawnRitualAreaEffect(getPos(), getWorld(), rand, getCenterColor(), 5);
        } else {
            if (getWorld().getGameTime() % 100 == 0) {
                List<LivingEntity> entities = getWorld().getEntitiesOfClass(LivingEntity.class, new AABB(getPos()).inflate(getRange()));
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
                            a.hurt(getWorld().damageSources().playerAttack(player1), getHarmAmount());
                        } else {
                            a.heal(getHealAmount());
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
        return ArsNouveau.prefix( RitualLib.RESTORATION);
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
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        RANGE = builder
                .comment("The range in blocks around the ritual where entities will be affected")
                .defineInRange("range", 5.0, 1.0, 30.0);
        HEAL_AMOUNT = builder
                .comment("The amount of health to restore to living entities")
                .defineInRange("heal_amount", 10.0, 0.5, 100.0);
        HARM_AMOUNT = builder
                .comment("The amount of damage to deal to undead entities")
                .defineInRange("harm_amount", 10.0, 0.5, 100.0);
    }

    private double getRange() {
        return RANGE.get();
    }
    
    private float getHealAmount() {
        return HEAL_AMOUNT.get().floatValue();
    }
    
    private float getHarmAmount() {
        return HARM_AMOUNT.get().floatValue();
    }

    @Override
    public int getDefaultSourceCost() {
        return 200;
    }
}
