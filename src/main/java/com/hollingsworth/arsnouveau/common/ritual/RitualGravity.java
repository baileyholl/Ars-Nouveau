package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.RangeRitual;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

public class RitualGravity extends RangeRitual {
    public static int Duration = 60 * 20;
    public static int RenewInterval = 20;
    public static int RenewThreshold = 10 * 20;
    public static int Range = 60;

    @Override
    public int getSourceCost() {
        return 200;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsNouveau.MODID, RitualLib.GRAVITY);
    }

    @Override
    public String getLangDescription() {
        return "Grants nearby players the Gravity effect, forcing them to the ground for a short time. If the player is nearby, this ritual will refresh their gravity debuff. Each time this ritual grants or refreshes gravity, it will expend source from nearby jars.";
    }

    @Override
    public String getLangName() {
        return "Gravity";
    }

    public boolean applyGravityIfNear(ServerPlayer player) {
        if (player.level.isClientSide || needsSourceNow() || BlockUtil.distanceFrom(getPos(), player.blockPosition()) > RitualGravity.Range) return false;

        player.addEffect(new MobEffectInstance(ModPotions.GRAVITY_EFFECT.get(), RitualGravity.Duration));
        setNeedsSource(true);
        return true;
    }
}
