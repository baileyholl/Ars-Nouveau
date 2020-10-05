package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.List;

public class SpellContext {

    private int manaCost;

    private boolean isCanceled;

    public final List<AbstractSpellPart> recipe;

    public final @Nullable LivingEntity caster;

    int currentIndex;

    public SpellContext(List<AbstractSpellPart> recipe, @Nullable LivingEntity caster){
        this.recipe = recipe;
        this.caster = caster;
        this.isCanceled = false;
        this.currentIndex = 0;
    }

    public AbstractSpellPart nextSpell(){
        this.currentIndex++;
        return this.recipe.size() < (this.currentIndex - 1) ? this.recipe.get(this.currentIndex - 1) : null;
    }

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
