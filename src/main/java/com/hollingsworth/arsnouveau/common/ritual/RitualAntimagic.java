package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.ritual.RangeRitual;
import com.hollingsworth.arsnouveau.api.ritual.RitualEventQueue;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class RitualAntimagic extends RangeRitual {

    public int getRange() {
        return 60;
    }

    @Override
    public String getLangName() {
        return "Antimagic";
    }

    @Override
    public String getLangDescription() {
        return "Denies spellcasting and spell resolution in a " + getRange() + " block radius. Costs source at minute if spellcasting is detected.";
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix(RitualLib.ANTIMAGIC);
    }

    public boolean cancelSpellcasting(SpellCastEvent event) {
        if (getPos() != null && event.getEntity().blockPosition().closerThan(getPos(), getRange())) {
            event.setCanceled(true);
            return true;
        }
        return false;
    }

    public boolean cancelResolve(SpellResolveEvent.Pre event) {
        if (getPos() != null && event.rayTraceResult.getLocation().closerThan(getPos().getCenter(), getRange())) {
            event.setCanceled(true);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void cancelSpellcastingListener(SpellCastEvent event) {
        if (event.getWorld() instanceof Level world && !world.isClientSide())
            RitualEventQueue.getRitual(world, RitualAntimagic.class, ritualAntimagic -> ritualAntimagic.cancelSpellcasting(event));
    }

    @SubscribeEvent
    public static void cancelSpellResolveListener(SpellResolveEvent.Pre event) {
        if (event.world instanceof Level world && !world.isClientSide())
            RitualEventQueue.getRitual(world, RitualAntimagic.class, ritualAntimagic -> ritualAntimagic.cancelResolve(event));
    }
}
