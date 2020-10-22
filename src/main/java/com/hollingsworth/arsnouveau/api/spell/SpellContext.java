package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class SpellContext {

    private int manaCost;

    private boolean isCanceled;

    public final List<AbstractSpellPart> recipe;

    public final @Nullable LivingEntity caster;

    private int currentIndex;

    public @Nullable TileEntity castingTile;

    public SpellContext(List<AbstractSpellPart> recipe, @Nullable LivingEntity caster){
        this.recipe = recipe;
        this.caster = caster;
        this.isCanceled = false;
        this.currentIndex = 0;
    }

    public AbstractSpellPart nextSpell(){
        this.currentIndex++;
        return recipe.get(currentIndex - 1);
    }

    public void resetSpells(){
        this.currentIndex = 0;
    }

    public SpellContext withCastingTile(TileEntity tile){
        this.castingTile = tile;
        return this;
    }

    public int getCurrentIndex(){return currentIndex;}

    public int getManaCost() {
        return manaCost;
    }

    public void setManaCost(int manaCost) {
        this.manaCost = manaCost;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public List<AbstractSpellPart> getRecipe() {
        return recipe;
    }

    @Nullable
    public LivingEntity getCaster() {
        return caster;
    }
}
