/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class SyncBookUnlockCapabilityMessage  {

    public CompoundTag tag;

    public SyncBookUnlockCapabilityMessage(BookUnlockCapability capability) {
        this.tag = capability.serializeNBT();
    }

    public SyncBookUnlockCapabilityMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(this.tag);
    }


    public void decode(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }


    public void handle(Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ArsNouveau.proxy.getPlayer().getCapability(CapabilityRegistry.BOOK_UNLOCK).ifPresent(capability -> {
                capability.deserializeNBT(this.tag);
            });

            if (BookGuiManager.get().openOverviewScreen != null) {
                BookGuiManager.get().openOverviewScreen.onSyncBookUnlockCapabilityMessage(this);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
