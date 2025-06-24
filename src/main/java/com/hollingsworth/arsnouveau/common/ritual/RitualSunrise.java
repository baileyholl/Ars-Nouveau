package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;

public class RitualSunrise extends AbstractRitual {
    @Override
    protected void tick() {
        ParticleUtil.spawnRitualSkyEffect(this, tile, rand, new ParticleColor.IntWrapper(255 + rand.nextInt(1), 255 + rand.nextInt(1), 25 + rand.nextInt(1)));
        if (getWorld() instanceof ServerLevel world) {
            // credits to Elucent for this trick
            if (world.getDayTime() % 24000 < 1000 || world.getDayTime() % 24000 >= 12000) {
                world.setDayTime(world.getDayTime() + 100);
                for (ServerPlayer player : world.players()) {
                    player.connection.send(new ClientboundSetTimePacket(world.getGameTime(), world.getDayTime(), world.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
                }
            } else {
                //speed up since the target is reached
                incrementProgress();
            }
            if (world.getGameTime() % 20 == 0) {
                incrementProgress();
                if (getProgress() >= 18) {
                    world.setDayTime(MathUtil.getNextDaysTime(world, MathUtil.DAY_TIME));
                    for (ServerPlayer player : world.players()) {
                        player.connection.send(new ClientboundSetTimePacket(world.getGameTime(), world.getDayTime(), world.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
                    }
                    setFinished();
                }
            }
        }

    }

    @Override
    public String getLangName() {
        return "Sunrise";
    }

    @Override
    public String getLangDescription() {
        return "Sets the time to day.";
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(
                rand.nextInt(255),
                rand.nextInt(255),
                rand.nextInt(25));
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix(RitualLib.SUNRISE);
    }

}

