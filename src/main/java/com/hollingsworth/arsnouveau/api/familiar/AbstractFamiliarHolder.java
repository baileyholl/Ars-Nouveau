package com.hollingsworth.arsnouveau.api.familiar;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public abstract class AbstractFamiliarHolder {

    public Predicate<Entity> isEntity;
    public String id;


    public AbstractFamiliarHolder(String id, Predicate<Entity> isConversionEntity){
        this.id = id;
        this.isEntity = isConversionEntity;
    }

    public abstract IFamiliar getSummonEntity(Level world, CompoundTag tag);

    public ItemStack getOutputItem(){
        return new ItemStack(ArsNouveauAPI.getInstance().getFamiliarItem(getId()));
    }

    public String getImagePath(){
        return "familiar_" + id + ".png";
    }

    public String getId(){
        return this.id;
    }

    public Component getLangDescription(){
        return Component.translatable("ars_nouveau.familiar_desc." + this.id);
    }

    public Component getLangName(){
        return Component.translatable("ars_nouveau.familiar_name." + this.id);
    }

    public String getEntityKey(){
        return this.id;
    }

    public String getBookName(){
        return "";
    }

    public String getBookDescription(){
        return "";
    }
}
