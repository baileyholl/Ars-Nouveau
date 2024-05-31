package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import java.util.List;

public class RitualPillagerRaid extends AbstractRitual {
    @Override
    protected void tick() {
        ParticleUtil.spawnRitualSkyEffect(this, tile, rand, getCenterColor().toWrapper());
        if (getWorld().getGameTime() % 20 == 0 && !getWorld().isClientSide) {
            incrementProgress();
            if (getProgress() >= 18) {
                ServerLevel world = (ServerLevel) getWorld();
                List<ServerPlayer> players = world.getEntitiesOfClass(ServerPlayer.class, new AABB(getPos()).inflate(5.0));
                if (players.size() > 0) {
                    Raid raid = world.getRaids().createOrExtendRaid(players.get(0));
                    if (raid != null) {
                        this.setFinished();
                        if (didConsumeItem(Items.EMERALD))
                            raid.numGroups = 7;
                    }
                }
            }
        }
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return getWorld().getDifficulty() != Difficulty.HARD && getContext().consumedItems.isEmpty() && stack.is(Tags.Items.GEMS_EMERALD);
    }

    @Override
    public ParticleColor getCenterColor() {
        return ParticleColor.makeRandomColor(20, 250, 20, rand);
    }

    @Override
    public void onItemConsumed(ItemStack stack) {
        super.onItemConsumed(stack);
    }


    @Override
    public String getLangName() {
        return "Challenge";
    }

    @Override
    public String getLangDescription() {
        return "Summons an illager raid when used inside a village. An Emerald may be used to increase the difficulty of the raid to the maximum amount, making Totems of the Undying accessible on easier difficulties. Augmenting has no effect on Hard difficulty.";
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsNouveau.MODID, RitualLib.CHALLENGE);
    }
}
