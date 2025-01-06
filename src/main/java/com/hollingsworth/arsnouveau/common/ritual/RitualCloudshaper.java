package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RitualCloudshaper extends AbstractRitual {
    public static final IntProvider RAIN_DELAY = UniformInt.of(12000, 180000);
    public static final IntProvider RAIN_DURATION = UniformInt.of(12000, 24000);
    public static final IntProvider THUNDER_DURATION = UniformInt.of(3600, 15600);
    @Override
    protected void tick() {
        ParticleUtil.spawnRitualSkyEffect(this, tile, rand, getCenterColor().toWrapper());
        if (getWorld().getGameTime() % 20 == 0 && !getWorld().isClientSide) {
            incrementProgress();
            if (getProgress() >= 18) {
                ServerLevel world = (ServerLevel) getWorld();
                if (!isStorm() && !isRain()) {
                    world.setWeatherParameters(RAIN_DELAY.sample(world.getRandom()), 0, false, false);
                    setFinished();
                }
                if (isStorm()) {
                    world.setWeatherParameters(0, THUNDER_DURATION.sample(world.getRandom()), true, true);
                    setFinished();
                }

                if (isRain()) {
                    world.setWeatherParameters(0, RAIN_DURATION.sample(world.getRandom()), true, false);
                    setFinished();
                }

            }
        }

    }

    public boolean isStorm() {
        return didConsumeItem(Items.LAPIS_BLOCK);
    }

    public boolean isRain() {
        return didConsumeItem(Items.GUNPOWDER);
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return getConsumedItems().isEmpty() && (stack.getItem() == Items.LAPIS_BLOCK || stack.getItem() == Items.GUNPOWDER);
    }

    @Override
    public String getLangName() {
        return "Cloudshaping";
    }

    @Override
    public String getLangDescription() {
        return "This ritual can change the weather at a moments notice. By default, this ritual will set the weather to clear. Augmenting with Gunpowder will cause it to rain, while a Lapis Block will cause it to storm.";
    }


    @Override
    public ParticleColor getCenterColor() {
        return !isRain() && !isStorm() ? new ParticleColor(
                rand.nextInt(255),
                rand.nextInt(255),
                rand.nextInt(255))
                : new ParticleColor(
                rand.nextInt(100),
                rand.nextInt(100),
                rand.nextInt(255));
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( RitualLib.CLOUDSHAPER);
    }
}