package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.List;
import java.util.Optional;

public class RitualHealing extends AbstractRitual {
    @Override
    protected void tick() {
        if(getWorld().isClientSide){
            ParticleUtil.spawnRitualAreaEffect(tile, rand, getCenterColor(), 5);
        }else{
            if(getWorld().getGameTime() % 100 == 0){
                List<LivingEntity> entities = getWorld().getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(getPos()).inflate(5));
                Optional<LivingEntity> player = entities.stream().filter(e -> e instanceof PlayerEntity).findFirst();

                boolean didWorkOnce = false;
                for(LivingEntity a : entities){
                    if(a instanceof ZombieVillagerEntity){

                        ((ZombieVillagerEntity) a).startConverting(player.isPresent() ? player.get().getUUID() : null, 0);
                        didWorkOnce = true;
                        continue;
                    }

                    if(a.getHealth() < a.getMaxHealth() || a.isInvertedHealAndHarm()) {
                        if(a.isInvertedHealAndHarm()){
                            FakePlayer player1 = FakePlayerFactory.getMinecraft((ServerWorld) getWorld());
                            a.hurt(DamageSource.playerAttack(player1).setMagic(), 10.0f);
                        }else{
                            a.heal(10.0f);
                        }
                        didWorkOnce = true;
                    }
                }
                if(didWorkOnce)
                    setNeedsMana(true);
            }
        }
    }

    @Override
    public String getID() {
        return RitualLib.RESTORATION;
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
        return "Heals nearby entities or harms undead over time. Additionally, Zombie Villagers will be instantly cured, and the resulting villager will offer discounts if a player was nearby. This ritual requires mana to operate.";
    }

    @Override
    public int getManaCost() {
        return 200;
    }
}
