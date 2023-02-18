/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.hollingsworth.arsnouveau.common.network;


import com.hollingsworth.arsnouveau.common.block.tile.container.IStorageController;
import com.hollingsworth.arsnouveau.common.block.tile.container.IStorageControllerContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageRequestStacks{

    //region Initialization
    public MessageRequestStacks() {

    }

    public MessageRequestStacks(FriendlyByteBuf buf) {
        this.decode(buf);
    }
    //endregion Initialization


    //region Overrides

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getSender() != null && ctx.get().getSender().containerMenu instanceof IStorageControllerContainer) {
            ServerPlayer player = ctx.get().getSender();
            IStorageController storageController = ((IStorageControllerContainer) ctx.get().getSender().containerMenu)
                    .getStorageController();
            if (storageController != null) {
                Networking.sendTo(player, storageController.getMessageUpdateStacks());
                Networking.sendTo(player, new MessageUpdateLinkedMachines(storageController.getLinkedMachines()));
                player.containerMenu.broadcastChanges();
            }
        }
    }

    public void encode(FriendlyByteBuf buf) {

    }

    public void decode(FriendlyByteBuf buf) {

    }
    //endregion Overrides
}
