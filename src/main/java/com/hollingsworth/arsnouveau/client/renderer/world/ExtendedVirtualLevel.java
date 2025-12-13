package com.hollingsworth.arsnouveau.client.renderer.world;

import dev.compactmods.gander.level.VirtualLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.function.Consumer;

public class ExtendedVirtualLevel extends VirtualLevel {

    public ExtendedVirtualLevel(RegistryAccess access, boolean isClientside) {
        super(access, isClientside);
    }

    public ExtendedVirtualLevel(RegistryAccess access, boolean isClientside, Consumer<VirtualLevel> onBlockUpdate) {
        super(access, isClientside, onBlockUpdate);
    }

    @Override
    public RecipeManager getRecipeManager() {
        return Minecraft.getInstance().level.getRecipeManager();
    }
}