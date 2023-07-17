package com.hollingsworth.arsnouveau.common.book;/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BookIcon {
    private final ItemStack itemStack;
    private final ResourceLocation texture;

    public BookIcon(ItemStack stack) {
        this.itemStack = stack;
        this.texture = null;
    }

    public BookIcon(ResourceLocation texture) {
        this.texture = texture;
        this.itemStack = ItemStack.EMPTY;
    }

    public static BookIcon fromString(ResourceLocation value) {
        if (value.getPath().endsWith(".png")) {
            return new BookIcon(value);
        } else {
            Item item = ForgeRegistries.ITEMS.getValue(value);
            return new BookIcon(new ItemStack(item));
        }
    }

    public static BookIcon fromNetwork(FriendlyByteBuf buffer) {
        ResourceLocation rl = buffer.readResourceLocation();
        if (rl.getPath().endsWith(".png")) {
            return new BookIcon(rl);
        } else {
            Item item = ForgeRegistries.ITEMS.getValue(rl);
            return new BookIcon(new ItemStack(item));
        }
    }

    public void render(GuiGraphics guiGraphics, int x, int y) {
        if (this.texture != null) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.blit(this.texture, x, y, 0, 0, 0, 16, 16, 16, 16);
        } else {
            guiGraphics.renderItem(this.itemStack, x, y);
        }
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        if (this.texture != null) {
            buffer.writeResourceLocation(this.texture);
        } else {
            buffer.writeResourceLocation(ForgeRegistries.ITEMS.getKey(this.itemStack.getItem()));
        }
    }
}
