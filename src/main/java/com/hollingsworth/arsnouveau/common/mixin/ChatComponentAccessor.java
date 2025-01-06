package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatComponent.class)
public interface ChatComponentAccessor {
//    @Accessor
//    List<String> getRecentChat();

    @Accessor
    List<GuiMessage> getAllMessages();

    @Accessor
    List<GuiMessage.Line> getTrimmedMessages();
}
