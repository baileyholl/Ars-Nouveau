package com.hollingsworth.arsnouveau.api.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.FamiliarRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public abstract class AbstractFamiliarHolder {

    public Predicate<Entity> isEntity;
    private ResourceLocation id;

    public AbstractFamiliarHolder(String id, Predicate<Entity> isConversionEntity) {
        this(ArsNouveau.prefix( id), isConversionEntity);
    }

    public AbstractFamiliarHolder(ResourceLocation id, Predicate<Entity> isConversionEntity) {
        this.id = id;
        this.isEntity = isConversionEntity;
    }

    public abstract IFamiliar getSummonEntity(Level world, CompoundTag tag);

    public ItemStack getOutputItem() {
        return new ItemStack(FamiliarRegistry.getFamiliarScriptMap().get(getRegistryName()));
    }

    public ResourceLocation getRegistryName() {
        return this.id;
    }

    public Component getLangDescription() {
        return Component.translatable(this.id.getNamespace() + ".familiar_desc." + this.id.getPath());
    }

    public Component getLangName() {
        return Component.translatable(this.id.getNamespace() + ".familiar_name." + this.id.getPath());
    }

    public String getBookName() {
        return "";
    }

    public String getBookDescription() {
        return "";
    }
}
