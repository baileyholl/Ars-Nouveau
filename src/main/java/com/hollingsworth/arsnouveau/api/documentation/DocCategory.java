package com.hollingsworth.arsnouveau.api.documentation;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record DocCategory(ResourceLocation id, ItemStack renderIcon, int order) implements Comparable<DocCategory>{

    public Component getTitle(){
        return Component.translatable(id.getNamespace() + ".section." + id.getPath());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocCategory that = (DocCategory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(@NotNull DocCategory o) {
        return this.order - o.order;
    }
}
