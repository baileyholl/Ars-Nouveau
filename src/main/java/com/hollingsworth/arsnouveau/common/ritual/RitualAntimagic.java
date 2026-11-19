package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.ritual.RangeRitual;
import com.hollingsworth.arsnouveau.api.ritual.RitualEventQueue;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class RitualAntimagic extends RangeRitual {

    int radius = 0;
    UUID owner;

    public int getRange() {
        return 20 + radius * 4;
    }

    @Override
    public void onStart(@Nullable Player player) {
        super.onStart(player);
        if (getWorld().isClientSide) {
            return;
        }
        for (ItemStack i : getConsumedItems()) {
            if (i.is(ItemTagProvider.SOURCE_GEM_TAG)) {
                radius += i.getCount();
            }
        }
        if (player != null) {
            owner = player.getUUID();
        }
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
        if (event.getEntity() instanceof Player p && p.getUUID().equals(owner)) return false;
        if (getPos() != null && event.getEntity().blockPosition().closerThan(getPos(), getRange())) {
            event.setCanceled(true);
            return true;
        }
        return false;
    }

    public boolean cancelResolve(SpellResolveEvent.Pre event) {
        if (event.shooter instanceof Player p && p.getUUID().equals(owner)) return false;
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

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag tag) {
        super.read(provider, tag);
        radius = tag.getInt("radius");
        if (tag.contains("owner"))
            playerUUID = tag.getUUID("owner");
    }

    @Override
    public void write(HolderLookup.Provider provider, CompoundTag tag) {
        super.write(provider, tag);
        tag.putInt("radius", radius);
        if (owner != null) {
            tag.putUUID("owner", owner);
        }
    }

}
