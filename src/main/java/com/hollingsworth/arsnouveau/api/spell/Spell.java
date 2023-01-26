package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Spell implements Cloneable {
    @Deprecated(forRemoval = true)
    public static final Spell EMPTY = new Spell();

    public List<AbstractSpellPart> recipe = new ArrayList<>();
    public String name = "";
    public ParticleColor color = ParticleColor.defaultParticleColor();
    public ConfiguredSpellSound sound = ConfiguredSpellSound.DEFAULT;

    /**
     * The discount removed from the casting cost of the spell.
     * This value is not saved, but is set to 0 after each cast.
     */
    private int discount = 0;


    public Spell(List<AbstractSpellPart> recipe) {
        this.recipe = recipe == null ? new ArrayList<>() : recipe; // Safe check for tiles initializing a null
    }

    public Spell() {
    }

    public Spell(AbstractSpellPart... spellParts) {
        super();
        add(spellParts);
    }

    public Spell add(AbstractSpellPart spellPart) {
        recipe.add(spellPart);
        return this;
    }

    public Spell add(AbstractSpellPart... spellParts) {
        for (AbstractSpellPart part : spellParts)
            add(part);
        return this;
    }

    public Spell add(AbstractSpellPart spellPart, int count) {
        for (int i = 0; i < count; i++)
            recipe.add(spellPart);
        return this;
    }

    public Spell setRecipe(@NotNull List<AbstractSpellPart> recipe) {
        this.recipe = recipe;
        return this;
    }

    public Spell withColor(@NotNull ParticleColor color) {
        this.color = color;
        return this;
    }

    public int getSpellSize() {
        return recipe.size();
    }

    public @Nullable AbstractCastMethod getCastMethod() {
        if (this.recipe == null || this.recipe.isEmpty())
            return null;
        return this.recipe.get(0) instanceof AbstractCastMethod ? (AbstractCastMethod) recipe.get(0) : null;
    }

    public List<AbstractAugment> getAugments(int startPosition, @Nullable LivingEntity caster) {
        ArrayList<AbstractAugment> augments = new ArrayList<>();
        if (recipe == null || recipe.isEmpty())
            return augments;
        for (int j = startPosition + 1; j < recipe.size(); j++) {
            AbstractSpellPart nextGlyph = recipe.get(j);
            if (nextGlyph instanceof AbstractAugment augment) {
                augments.add(augment);
            } else {
                break;
            }
        }

        return augments;
    }

    public int getInstanceCount(AbstractSpellPart spellPart) {
        int count = 0;
        for (AbstractSpellPart abstractSpellPart : this.recipe) {
            if (abstractSpellPart.equals(spellPart))
                count++;
        }
        return count;
    }

    public int getBuffsAtIndex(int startPosition, @Nullable LivingEntity caster, AbstractAugment augment) {
        return (int) getAugments(startPosition, caster).stream().filter(a -> a.equals(augment)).count();
    }


    /**
     * Returns the cost of casting this spell with discounts.
     * Does not reset the discount value.
     * THIS SHOULD NOT BE USED FOR EXPENDING MANA.
     */
    public int getDiscountedCost() {
        return Math.max(0, getNoDiscountCost() - discount);
    }

    /**
     * Returns the original cost of casting this spell without discounts.
     * THIS SHOULD NOT BE USED FOR EXPENDING MANA.
     */
    public int getNoDiscountCost() {
        int cost = 0;
        if (recipe == null)
            return cost;
        for (AbstractSpellPart spell : recipe) {
            cost += spell.getCastingCost();
        }
        cost = Math.max(0, cost);
        return cost;
    }

    /**
     * Returns the final cost of the spell with all discounts applied.
     * This will reset the discount to 0, so this should only be used before expending mana.
     */
    public int getFinalCostAndReset() {
        int cost = getDiscountedCost();
        discount = 0;
        return cost;
    }

    public void addDiscount(int discount) {
        this.discount += discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getDiscount() {
        return discount;
    }

    public boolean isEmpty() {
        return recipe == null || recipe.isEmpty();
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.put("spellColor", color.serialize());
        tag.put("sound", sound.serialize());
        CompoundTag recipeTag = new CompoundTag();
        for (int i = 0; i < recipe.size(); i++) {
            AbstractSpellPart part = recipe.get(i);
            recipeTag.putString("part" + i, part.getRegistryName().toString());
        }
        recipeTag.putInt("size", recipe.size());
        tag.put("recipe", recipeTag);
        return tag;
    }

    public static Spell fromTag(@Nullable CompoundTag tag) {
        if (tag == null)
            return new Spell();
        Spell spell = new Spell();
        spell.name = tag.getString("name");
        spell.color = ParticleColor.deserialize(tag.getCompound("spellColor"));
        spell.sound = ConfiguredSpellSound.fromTag(tag.getCompound("sound"));
        CompoundTag recipeTag = tag.getCompound("recipe");
        int size = recipeTag.getInt("size");
        for (int i = 0; i < size; i++) {
            ResourceLocation registryName = new ResourceLocation(recipeTag.getString("part" + i));
            AbstractSpellPart part = ArsNouveauAPI.getInstance().getSpellpartMap().get(registryName);
            if (part != null)
                spell.recipe.add(part);
        }
        return spell;
    }

    public String getDisplayString() {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < recipe.size(); i++) {
            AbstractSpellPart spellPart = recipe.get(i);
            int num = 1;
            for (int j = i + 1; j < recipe.size(); j++) {
                if (spellPart.name.equals(recipe.get(j).name))
                    num++;
                else
                    break;
            }
            if (num > 1) {
                str.append(spellPart.getLocaleName()).append(" x").append(num);
                i += num - 1;
            } else {
                str.append(spellPart.getLocaleName());
            }
            if (i < recipe.size() - 1) {
                str.append(" -> ");
            }
        }
        return str.toString();
    }

    public boolean isValid() {
        return !this.isEmpty();
    }

    public Spell add(AbstractSpellPart spellPart, int count, int index) {
        for (int i = 0; i < count; i++)
            recipe.add(index, spellPart);
        return this;
    }

    public List<ResourceLocation> serializeRecipe(){
        return this.recipe.stream().map(AbstractSpellPart::getRegistryName).toList();
    }

    @Override
    public Spell clone() {
        try {
            // TODO: Make above cloneable
            Spell clone = (Spell) super.clone();
            clone.recipe = new ArrayList<>(this.recipe);
            clone.color = this.color.clone();
            clone.sound = this.sound;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
