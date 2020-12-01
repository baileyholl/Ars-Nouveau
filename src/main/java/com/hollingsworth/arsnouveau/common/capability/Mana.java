package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.api.mana.IMana;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nullable;

public class Mana implements IMana {

    private final LivingEntity livingEntity;

    private double mana;

    private int maxMana;

    private int glyphBonus;

    private int bookTier;


    public Mana(@Nullable final LivingEntity entity) {
        this.livingEntity = entity;
    }

    @Override
    public double getCurrentMana() {
        return mana;
    }

    @Override
    public int getMaxMana() {
        return maxMana;
    }

    @Override
    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    @Override
    public double setMana(double mana) {
        if(mana > getMaxMana()){
            this.mana = getMaxMana();
        }else if(mana < 0){
            this.mana = 0;
        }else {
            this.mana = mana;
        }
        return this.getCurrentMana();
    }

    @Override
    public double addMana(double manaToAdd) {
        this.setMana(this.getCurrentMana() + manaToAdd);
        return this.getCurrentMana();
    }

    @Override
    public double removeMana(double manaToRemove) {
        if(manaToRemove < 0)
            manaToRemove = 0;
        this.setMana(this.getCurrentMana() - manaToRemove);
        return this.getCurrentMana();
    }

    @Override
    public int getGlyphBonus(){
        return glyphBonus;
    }

    @Override
    public void setGlyphBonus(int glyphBonus){
        this.glyphBonus = glyphBonus;
    }

    @Override
    public int getBookTier(){
        return bookTier;
    }

    @Override
    public void setBookTier(int bookTier){
        this.bookTier = bookTier;
    }
}
