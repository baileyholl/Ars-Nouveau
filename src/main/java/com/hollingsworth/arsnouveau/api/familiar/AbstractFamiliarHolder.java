package com.hollingsworth.arsnouveau.api.familiar;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class AbstractFamiliarHolder {

    public Predicate<Entity> isEntity;
    public Supplier<ItemStack> outputRitualItem;
    public Supplier<EntityType> summonEntity;
    public String id;

    public AbstractFamiliarHolder(String id, Predicate<Entity> isConversionEntity, Supplier<ItemStack> outputRitualItem, Supplier<EntityType> getSummonEntity){
        this.id = id;
        this.isEntity = isConversionEntity;
        this.outputRitualItem = outputRitualItem;
        this.summonEntity = getSummonEntity;
    }

    public String getImagePath(){
        return "familiar_" + id + ".png";
    }

    public String getId(){
        return this.id;
    }

    public TranslationTextComponent getDescription(){
        return new TranslationTextComponent("ars_nouveau.familiar_desc." + this.id);
    }

    public TranslationTextComponent getName(){
        return new TranslationTextComponent("ars_nouveau.familiar_name." + this.id);
    }
}
