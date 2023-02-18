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


import com.hollingsworth.arsnouveau.common.block.tile.container.GlobalBlockPos;
import com.hollingsworth.arsnouveau.common.block.tile.container.IStorageControllerGui;
import com.hollingsworth.arsnouveau.common.block.tile.container.MachineReference;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This message sends the stacks in the currently opened storage controller.
 */
public class MessageUpdateLinkedMachines  {

    //region Fields
    private List<MachineReference> linkedMachines;
    //endregion Fields

    //region Initialization

    public MessageUpdateLinkedMachines(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public MessageUpdateLinkedMachines(List<MachineReference> linkedMachines) {
        this.linkedMachines = linkedMachines;
    }

    public MessageUpdateLinkedMachines(Map<GlobalBlockPos, MachineReference> linkedMachines) {
        this.linkedMachines = new ArrayList<>(linkedMachines.values());
    }
    //endregion Initialization

    //region Overrides

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if (Minecraft.getInstance().screen instanceof IStorageControllerGui screen) {
            screen.setLinkedMachines(this.linkedMachines);
        }
    }


    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.linkedMachines.size());
        for (MachineReference machineReference : this.linkedMachines) {
            machineReference.encode(buf);
        }
    }


    public void decode(FriendlyByteBuf buf) {
        int linkedMachinesSize = buf.readInt();
        this.linkedMachines = new ArrayList<>(linkedMachinesSize);

        for (int i = 0; i < linkedMachinesSize; i++) {
            MachineReference machineReference = MachineReference.from(buf);
            this.linkedMachines.add(machineReference);
        }
    }

    //endregion Overrides
}
