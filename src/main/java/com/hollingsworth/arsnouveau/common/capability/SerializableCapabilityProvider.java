package com.hollingsworth.arsnouveau.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

/**
 * A simple implementation of ICapabilityProvider and {@link INBTSerializable} that supports a single {@link Capability} handler instance.
 * <p>
 * Uses the {@link Capability}'s {IStorage to serialise/deserialise NBT.
 *
 * @author Choonster
 */
//TODO: Remove in favor of simplified cap
    @Deprecated
public class SerializableCapabilityProvider<HANDLER> extends SimpleCapabilityProvider<HANDLER> implements INBTSerializable<Tag> {

    /**
     * Create a provider for the default handler instance.
     *
     * @param capability The Capability instance to provide the handler for
     * @param facing     The Direction to provide the handler for
     */
//    public SerializableCapabilityProvider(final Capability<HANDLER> capability, @Nullable final Direction facing) {
//        this(capability, facing, capability.getDefaultInstance());
//    }

    /**
     * Create a provider for the specified handler instance.
     *
     * @param capability The Capability instance to provide the handler for
     * @param facing     The Direction to provide the handler for
     * @param instance   The handler instance to provide
     */
    public SerializableCapabilityProvider(final Capability<HANDLER> capability, @Nullable final Direction facing, @Nullable final HANDLER instance) {
        super(capability, facing, instance);
    }

    @Nullable
    @Override
    public Tag serializeNBT() {
        final HANDLER instance = getInstance();

        if (instance == null) {
            return null;
        }
        if(getCapability() == null)
            return new CompoundTag();
        return new CompoundTag();// getCapability().writeNBT(instance, getFacing());
    }

    @Override
    public void deserializeNBT(final Tag nbt) {
        final HANDLER instance = getInstance();

        if (instance == null) {
            return;
        }

       // getCapability().readNBT(instance, getFacing(), nbt);
    }

}