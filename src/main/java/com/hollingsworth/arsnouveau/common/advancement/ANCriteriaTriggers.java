package com.hollingsworth.arsnouveau.common.advancement;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.resources.ResourceLocation;

public class ANCriteriaTriggers {
    public static final PlayerTrigger POOF_MOB = register(new PlayerTrigger(new ResourceLocation(ArsNouveau.MODID, "poof_mob")));
    public static final PlayerTrigger FAMILIAR = register(new PlayerTrigger(new ResourceLocation(ArsNouveau.MODID, "familiar")));
    public static final PlayerTrigger CHIMERA_EXPLOSION = register(new PlayerTrigger(new ResourceLocation(ArsNouveau.MODID, "chimera_explosion")));

    public static <T extends CriterionTrigger<?>> T register(T trigger) {
        return CriteriaTriggers.register(trigger);
    }

    public static void init() {}
}
