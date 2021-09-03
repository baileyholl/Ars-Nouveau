package com.hollingsworth.arsnouveau.api.familiar;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.function.Predicate;

public abstract class AbstractFamiliarHolder {

    public Predicate<Entity> isEntity;
    public String id;


    public AbstractFamiliarHolder(String id, Predicate<Entity> isConversionEntity){
        this.id = id;
        this.isEntity = isConversionEntity;
    }

    public abstract IFamiliar getSummonEntity(World world);

    public ItemStack getOutputItem(){
        return new ItemStack(ArsNouveauAPI.getInstance().getFamiliarItem(getId()));
    }

    public String getImagePath(){
        return "familiar_" + id + ".png";
    }

    public String getId(){
        return this.id;
    }

    public TranslationTextComponent getLangDescription(){
        return new TranslationTextComponent("ars_nouveau.familiar_desc." + this.id);
    }

    public TranslationTextComponent getLangName(){
        return new TranslationTextComponent("ars_nouveau.familiar_name." + this.id);
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
