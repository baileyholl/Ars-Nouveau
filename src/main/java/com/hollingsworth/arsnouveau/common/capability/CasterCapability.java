package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.common.items.Caster;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

public class CasterCapability {

    @CapabilityInject(ISpellCaster.class)
    public static final Capability<ISpellCaster> CASTER_CAPABILITY = null;

    public static final Direction DEFAULT_FACING = null;

    public static final ResourceLocation ID = new ResourceLocation(ArsNouveau.MODID, "caster");

    public static void register(){

        CapabilityManager.INSTANCE.register(ISpellCaster.class, new Capability.IStorage<ISpellCaster>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<ISpellCaster> capability, ISpellCaster instance, Direction side) {
                CompoundNBT tag = new CompoundNBT();
                tag.putInt("current_slot", instance.getCurrentSlot());
                tag.putInt("max_slot", instance.getMaxSlots());
                int i = 0;
                for(Integer s : instance.getSpells().keySet()){
                    tag.putString("spell_" + i, instance.getSpells().get(s).serialize());
                    i++;
                }
              //  System.out.println("writing");
                return tag;
            }

            @Override
            public void readNBT(Capability<ISpellCaster> capability, ISpellCaster instance, Direction side, INBT nbt) {
                CompoundNBT tag = (CompoundNBT)nbt;
                instance.setCurrentSlot(tag.getInt("current_slot"));
                for(int i = 0; i < instance.getMaxSlots(); i++){
                    if(tag.contains("spell_" + i)){
                        instance.getSpells().put(i, Spell.deserialize(tag.getString("spell_" + i)));
                    }
                }
            }
        }, () -> new SpellCaster(null));
    }

    public static ICapabilityProvider createProvider(final ISpellCaster caster) {
        return new SerializableCapabilityProvider<>(CASTER_CAPABILITY, DEFAULT_FACING, caster);
    }

    public static LazyOptional<ISpellCaster> getCaster(final ItemStack stack){
        return stack.getCapability(CASTER_CAPABILITY, DEFAULT_FACING);
    }

    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
    private static class EventHandler {

        /**
         * Attach the {@link IMana} capability to all living entities.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<ItemStack> event) {
            if(event.getObject().getItem() instanceof Caster){
                final SpellCaster caster = new SpellCaster(event.getObject());
                event.addCapability(ID, createProvider(caster));
            }
        }
    }
}
