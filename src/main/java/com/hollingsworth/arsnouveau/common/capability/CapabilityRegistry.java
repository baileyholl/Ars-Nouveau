package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class CapabilityRegistry {

    public static final Capability<IManaCap> MANA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});


    public static final Direction DEFAULT_FACING = null;


    /**
     * Get the {@link IManaCap} from the specified entity.
     *
     * @param entity The entity
     * @return A lazy optional containing the IMana, if any
     */
    public static LazyOptional<IManaCap> getMana(final LivingEntity entity){
        return entity.getCapability(MANA_CAPABILITY, DEFAULT_FACING);
    }

    public static ICapabilityProvider createProvider(final IManaCap mana) {
        return new SerializableCapabilityProvider<>(MANA_CAPABILITY, DEFAULT_FACING, mana);
    }

    /**
     * Event handler for the {@link IManaCap} capability.
     */
    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
    private static class EventHandler {

        /**
         * Attach the {@link IManaCap} capability to all living entities.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
            ManaCapAttacher.attach(event);
        }
        @SubscribeEvent
        public static void attachCapabilities(final RegisterCapabilitiesEvent event) {
            event.register(IManaCap.class);
        }
        /**
         * Copy the player's mana when they respawn after dying or returning from the end.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void playerClone(final PlayerEvent.Clone event) {
            getMana(event.getOriginal()).ifPresent(oldMaxMana -> getMana(event.getPlayer()).ifPresent(newMaxMana -> {
                newMaxMana.setMaxMana(oldMaxMana.getMaxMana());
                newMaxMana.setMana(oldMaxMana.getCurrentMana());
                newMaxMana.setBookTier(oldMaxMana.getBookTier());
                newMaxMana.setGlyphBonus(oldMaxMana.getGlyphBonus());
            }));
        }
    }
}
