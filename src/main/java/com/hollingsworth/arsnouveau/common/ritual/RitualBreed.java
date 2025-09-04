package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class RitualBreed extends AbstractRitual {
    long lastTick = -1;
    boolean tooManyAnimals = false;

    @Override
    protected void tick() {
        if (getWorld().isClientSide) {
            ParticleUtil.spawnRitualAreaEffect(getPos(), getWorld(), rand, getCenterColor(), 5);
        } else {
            if (getWorld().getGameTime() % 200 == 0) {
                List<Animal> animals = getWorld().getEntitiesOfClass(Animal.class, new AABB(getPos()).inflate(5));
                if (animals.size() >= 20) {
                    return;
                }
                boolean didWorkOnce = false;
                for (Animal a : animals) {
                    if (a.getAge() == 0 && a.canFallInLove()) {
                        didWorkOnce = true;
                        a.setInLove(null);
                    }
                }
                if (didWorkOnce)
                    setNeedsSource(true);
            }
        }
    }

    @Override
    public void modifyTooltips(List<Component> tooltips) {
        var lastCheck = getWorld().getGameTime();
        if (lastCheck != lastTick && lastCheck % 20 == 0) {
            List<Animal> animals = getWorld().getEntitiesOfClass(Animal.class, new AABB(getPos()).inflate(5));
            tooManyAnimals = animals.size() >= 20;
            lastTick = lastCheck;
        }
        if (tooManyAnimals) {
            tooltips.add(Component.translatable("ars_nouveau.tooltip.too_many_animals"));
        }
    }

    @Override
    public String getLangDescription() {
        return "Periodically causes nearby animals to breed if possible. This ritual requires source to operate, and will have no effect if there are twenty or more animals nearby.";
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(100, 255, 100);
    }

    @Override
    public String getLangName() {
        return "Fertility";
    }

    @Override
    public int getSourceCost() {
        return 500;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix(RitualLib.FERTILITY);
    }
}
