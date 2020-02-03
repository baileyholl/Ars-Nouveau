package com.hollingsworth.craftedmagic.capability;

import com.hollingsworth.craftedmagic.ArsNouveau;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class SpellBookCapability {

    @CapabilityInject(SpellBook.class)
    public static final Capability<SpellBook> SPELL_BOOK_CAPABILITY = null;

    public static final Direction DEFAULT_FACING = null;

    /**
     * The ID of this capability.
     */
    public static final ResourceLocation ID = new ResourceLocation(ArsNouveau.MODID, "spell_book");

    public static void register() {
        final String BOOK_MODE_TAG = "mode";

        CapabilityManager.INSTANCE.register(SpellBook.class, new Capability.IStorage<SpellBook>() {
            @Override
            public INBT writeNBT(final Capability<SpellBook> capability, final SpellBook instance, final Direction side) {
                CompoundNBT tag = new CompoundNBT();
                tag.putInt(BOOK_MODE_TAG, instance.getBookMode());
                return tag;
            }

            @Override
            public void readNBT(final Capability<SpellBook> capability, final SpellBook instance, final Direction side, final INBT nbt) {
                instance.setBookMode(((CompoundNBT)nbt).getInt(BOOK_MODE_TAG));
            }
        }, () -> new SpellBook());

        //CapabilityContainerListenerManager.registerListenerFactory(LastUseTimeContainerListener::new);
    }

    /**
     * Get the {@link ILastUseTime} from the specified {@link ItemStack}'s capabilities, if any.
     *
     * @param itemStack The ItemStack
     * @return A lazy optional containing the ILastUseTime, if any
     */
    public static LazyOptional<SpellBook> getLastUseTime(final ItemStack itemStack) {
        return itemStack.getCapability(SPELL_BOOK_CAPABILITY, DEFAULT_FACING);
    }

    /**
     * Update the last use time of the player's held item.
     *
     * @param player    The player
     * @param itemStack The held ItemStack
     */
    public static void updateLastUseTime(final PlayerEntity player, final ItemStack itemStack) {
        getLastUseTime(itemStack).ifPresent((lastUseTime) -> {
            final World world = player.getEntityWorld();

            //lastUseTime.set(world.getGameTime());
        });
    }

    /**
     * Create a provider for the default {@link ILastUseTime} instance.
     *
     * @return The provider
     */
    public static ICapabilityProvider createProvider() {
        return new SerializableCapabilityProvider<>(SPELL_BOOK_CAPABILITY, DEFAULT_FACING);
    }

    /**
     * Create a provider for the specified {@link ILastUseTime} instance.
     *
     * @param spellBook The ILastUseTime
     * @return The provider
     */
    public static ICapabilityProvider createProvider(final SpellBook spellBook) {
        return new SerializableCapabilityProvider<>(SPELL_BOOK_CAPABILITY, DEFAULT_FACING, spellBook);
    }

    /**
     * Event handler for the {@link ILastUseTime} capability.
     */
    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
    public static class EventHandler {
        /**
         * Update the {@link ILastUseTime} of the player's held item when they right click.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void playerInteract(final PlayerInteractEvent.RightClickItem event) {
            final ItemStack itemStack = event.getItemStack();

            getLastUseTime(itemStack).ifPresent(lastUseTime -> {
//                if (lastUseTime.automaticUpdates()) {
//                    updateLastUseTime(event.getPlayer(), itemStack);
//                }
            });
        }
    }

    /**
     * {@link IItemPropertyGetter} to get the ticks since the last use of the item. Returns {@link Float#MAX_VALUE} if the required information isn't available.
     */
    public static class TicksSinceLastUseGetter {
        /**
         * The ID of this getter.
         */
        public static final ResourceLocation ID = new ResourceLocation(ArsNouveau.MODID, "ticks_since_last_use");

        /**
         * The getter.
         */
        private static final IItemPropertyGetter GETTER = (stack, worldIn, entityIn) ->
        {
            final World world = worldIn != null ? worldIn : entityIn != null ? entityIn.getEntityWorld() : null;

            if (world == null) {
                return Float.MAX_VALUE;
            }
            return 0.1f;
//            return getLastUseTime(stack)
//                    .map(lastUseTime -> (float) world.getGameTime() - lastUseTime.get())
//                    .orElse(Float.MAX_VALUE);
        };

        /**
         * Add this getter to an {@link Item}.
         *
         * @param item The item
         */
        public static void addToItem(final Item item) {
            item.addPropertyOverride(ID, GETTER);
        }
    }
}
