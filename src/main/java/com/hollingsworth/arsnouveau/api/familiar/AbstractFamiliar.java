package com.hollingsworth.arsnouveau.api.familiar;

import com.hollingsworth.arsnouveau.common.entity.goal.FamiliarEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class AbstractFamiliar {

    public Predicate<Entity> isEntity;
    public Supplier<ItemStack> outputRitualItem;
    public Supplier<FamiliarEntity> summonEntity;
    public String id;

    public AbstractFamiliar(String id, Predicate<Entity> isConversionEntity, Supplier<ItemStack> outputRitualItem, Supplier<FamiliarEntity> getSummonEntity){
        this.id = id;
        this.isEntity = isConversionEntity;
        this.outputRitualItem = outputRitualItem;
        this.summonEntity = getSummonEntity;
    }
}
