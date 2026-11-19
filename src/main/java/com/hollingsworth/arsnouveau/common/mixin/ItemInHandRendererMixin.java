package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @WrapMethod(method = "itemUsed")
    public void itemUsed(InteractionHand hand, Operation<Void> original) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.getItemInHand(hand).getItem() instanceof ICasterTool) {
            return;
        }

        original.call(hand);
    }
}
