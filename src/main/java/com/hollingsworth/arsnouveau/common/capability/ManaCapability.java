package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

import static com.hollingsworth.arsnouveau.setup.InjectionUtil.Null;

public class ManaCapability {

    @CapabilityInject(IMana.class)
    public static final Capability<IMana> MANA_CAPABILITY = Null();


    public static final Direction DEFAULT_FACING = null;


    public static final ResourceLocation ID = new ResourceLocation(ArsNouveau.MODID, "mana");

    public static void register(){

        CapabilityManager.INSTANCE.register(IMana.class, new Capability.IStorage<IMana>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IMana> capability, IMana instance, Direction side) {
                CompoundNBT tag = new CompoundNBT();
                tag.putDouble("current", instance.getCurrentMana());
                tag.putInt("max", instance.getMaxMana());
                tag.putInt("glyph", instance.getGlyphBonus());
                tag.putInt("book_tier", instance.getBookTier());
                return tag;
            }

            @Override
            public void readNBT(Capability<IMana> capability, IMana instance, Direction side, INBT nbt) {
                if(!(nbt instanceof CompoundNBT))
                    return;
                CompoundNBT tag = (CompoundNBT)nbt;
                instance.setMaxMana(tag.getInt("max"));
                instance.setMana(tag.getDouble("current"));
                instance.setBookTier(tag.getInt("book_tier"));
                instance.setGlyphBonus(tag.getInt("glyph"));
            }
        }, () -> new Mana(null));
        System.out.println("Finished Registering ManaCapability");
    }

    /**
     * Get the {@link IMana} from the specified entity.
     *
     * @param entity The entity
     * @return A lazy optional containing the IMana, if any
     */
    public static LazyOptional<IMana> getMana(final LivingEntity entity){
        return entity.getCapability(MANA_CAPABILITY, DEFAULT_FACING);
    }

    public static ICapabilityProvider createProvider(final IMana mana) {
        return new SerializableCapabilityProvider<>(MANA_CAPABILITY, DEFAULT_FACING, mana);
    }

    /**
     * Event handler for the {@link IMana} capability.
     */
    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
    private static class EventHandler {

        /**
         * Attach the {@link IMana} capability to all living entities.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {

            if (event.getObject() instanceof PlayerEntity) {
                final Mana mana = new Mana((LivingEntity) event.getObject());
                event.addCapability(ID, createProvider(mana));
            }
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
