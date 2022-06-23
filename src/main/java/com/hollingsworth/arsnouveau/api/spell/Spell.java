package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Spell implements Cloneable{
    public static final Spell EMPTY = new Spell();

    public List<AbstractSpellPart> recipe = new ArrayList<>();
    private int cost;

    public String name = "";
    public ParticleColor color =  ParticleUtil.defaultParticleColor();
    public ConfiguredSpellSound sound = ConfiguredSpellSound.DEFAULT;


    public Spell(List<AbstractSpellPart> recipe){
        this.recipe = recipe == null ? new ArrayList<>() : recipe; // Safe check for tiles initializing a null
        this.cost = getInitialCost();
    }

    public Spell(){ }

    public Spell(AbstractSpellPart... spellParts){
        super();
        add(spellParts);
    }

    public Spell add(AbstractSpellPart spellPart){
        recipe.add(spellPart);
        return this;
    }

    public Spell add(AbstractSpellPart... spellParts){
        for(AbstractSpellPart part : spellParts)
            add(part);
        return this;
    }

    public Spell add(AbstractSpellPart spellPart, int count){
        for(int i = 0; i < count; i++)
            recipe.add(spellPart);
        return this;
    }

    public Spell setRecipe(@Nonnull List<AbstractSpellPart> recipe){
        this.recipe = recipe;
        return this;
    }

    public int getSpellSize(){
        return recipe.size();
    }

    public @Nullable AbstractCastMethod getCastMethod(){
        if(this.recipe == null || this.recipe.isEmpty())
            return null;
        return this.recipe.get(0) instanceof AbstractCastMethod ? (AbstractCastMethod) recipe.get(0) : null;

    }

    public List<AbstractAugment> getAugments(int startPosition, @Nullable LivingEntity caster){
        ArrayList<AbstractAugment> augments = new ArrayList<>();
        if(recipe == null || recipe.isEmpty())
            return augments;
        for(int j = startPosition + 1; j < recipe.size(); j++){
            AbstractSpellPart next_spell = recipe.get(j);
            if(next_spell instanceof AbstractAugment){
                augments.add((AbstractAugment) next_spell);
            }else{
                break;
            }
        }

        return augments;
    }

    public int getInstanceCount(AbstractSpellPart spellPart){
        int count = 0;
        for (AbstractSpellPart abstractSpellPart : this.recipe) {
            if (abstractSpellPart.equals(spellPart))
                count++;
        }
        return count;
    }

    public int getBuffsAtIndex(int startPosition, @Nullable LivingEntity caster, AbstractAugment augment){
        return (int) getAugments(startPosition, caster).stream().filter(a -> a.equals(augment)).count();
    }

    private int getInitialCost(){
        int cost = 0;
        if(recipe == null)
            return cost;
        for (AbstractSpellPart spell : recipe) {
            cost += spell.getConfigCost();
        }
        return Math.max(0, cost);
    }

    public int getCastingCost(){
        return Math.max(0, cost);
    }

    public void setCost(int cost){
        this.cost = Math.max(0, cost);
    }

    public boolean isEmpty(){
        return recipe == null || recipe.isEmpty();
    }

    public CompoundTag serialize(){
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.putString("color", color.toWrapper().serialize());
        tag.put("sound", sound.serialize());
        CompoundTag recipeTag = new CompoundTag();
        for(int i = 0; i < recipe.size(); i++){
            AbstractSpellPart part = recipe.get(i);
            recipeTag.putString("part" + i, part.getRegistryName().toString());
        }
        recipeTag.putInt("size", recipe.size());
        tag.put("recipe", recipeTag);
        return tag;
    }

    public static Spell fromTag(@Nullable CompoundTag tag) {
        if(tag == null)
            return EMPTY;
        Spell spell = new Spell();
        spell.name = tag.getString("name");
        spell.color = ParticleColor.IntWrapper.deserialize(tag.getString("color")).toParticleColor();
        spell.sound = ConfiguredSpellSound.fromTag(tag.getCompound("sound"));
        CompoundTag recipeTag = tag.getCompound("recipe");
        int size = recipeTag.getInt("size");
        for(int i = 0; i < size; i++){
            ResourceLocation registryName = new ResourceLocation(recipeTag.getString("part" + i));
            AbstractSpellPart part = ArsNouveauAPI.getInstance().getSpellpartMap().get(registryName);
            if(part != null)
                spell.recipe.add(part);
        }
        return spell;
    }

    public String getDisplayString(){
        StringBuilder str = new StringBuilder();

        for(int i = 0; i < recipe.size(); i++){
            AbstractSpellPart spellPart = recipe.get(i);
            int num = 1;
            for(int j = i + 1; j < recipe.size(); j++){
                if(spellPart.name.equals(recipe.get(j).name))
                    num++;
                else
                    break;
            }
            if(num > 1){
                str.append(spellPart.getLocaleName()).append(" x").append(num);
                i += num - 1;
            }else{
                str.append(spellPart.getLocaleName());
            }
            if(i < recipe.size() - 1){
                str.append(" -> ");
            }
        }
        return str.toString();
    }

    public boolean isValid(){
        return !this.isEmpty();
    }

    public Spell add(AbstractSpellPart spellPart, int count, int index){
        for(int i = 0; i < count; i++)
            recipe.add(index, spellPart);
        return this;
    }

    @Override
    public Spell clone() {
        try {
            // TODO: Make above cloneable
            Spell clone = (Spell) super.clone();
            clone.recipe = new ArrayList<>(this.recipe);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
