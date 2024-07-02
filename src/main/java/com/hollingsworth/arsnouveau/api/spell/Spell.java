package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spell implements Cloneable {

    public static final MapCodec<Spell> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(s -> s.name),
            ParticleColor.CODEC.fieldOf("color").forGetter(s -> s.color),
            ConfiguredSpellSound.CODEC.fieldOf("sound").forGetter(s -> s.sound),
            Codec.list(AbstractSpellPart.CODEC).fieldOf("recipe").forGetter(s -> s.recipe)
    ).apply(instance, Spell::new));

    private List<AbstractSpellPart> recipe = new ArrayList<>();
    private String name = "";
    private ParticleColor color = ParticleColor.defaultParticleColor();
    private ConfiguredSpellSound sound = ConfiguredSpellSound.DEFAULT;

    public Spell(List<AbstractSpellPart> recipe) {
        this.recipe = recipe == null ? new ArrayList<>() : new ArrayList<>(recipe); // Safe check for tiles initializing a null
    }

    public Spell() {
    }

    public Spell(AbstractSpellPart... spellParts) {
        super();
        recipe.addAll(Arrays.asList(spellParts));
    }



    public Spell(String name, ParticleColor color, ConfiguredSpellSound configuredSpellSound, List<AbstractSpellPart> abstractSpellParts) {
        this.name = name;
        this.color = color;
        this.sound = configuredSpellSound;
        this.recipe = abstractSpellParts;
    }

    public ConfiguredSpellSound sound(){
        return sound;
    }

    public Iterable<AbstractSpellPart> recipe(){
        return recipe;
    }

    /**
     * DO NOT MUTATE.
     * See {@link Spell#mutable()} for a mutable version.
     */
    public List<AbstractSpellPart> unsafeList(){
        return recipe;
    }

    public AbstractSpellPart get(int index){
        return recipe.get(index);
    }

    public int size(){
        return recipe.size();
    }

    public int indexOf(AbstractSpellPart part){
        return recipe.indexOf(part);
    }

    public Spell add(AbstractSpellPart spellPart) {
        return new Spell(name, color, sound, Util.copyAndAdd(recipe, spellPart));
    }

    public Spell add(AbstractSpellPart... spellParts) {
        var spell = this;
        for (AbstractSpellPart part : spellParts)
            spell = spell.add(part);
        return spell;
    }

    public Spell add(AbstractSpellPart spellPart, int count) {
        var spell = this;
        for (int i = 0; i < count; i++) {
            spell = spell.add(spellPart);
        }
        return spell;
    }

    public Spell setRecipe(@NotNull List<AbstractSpellPart> recipe) {
        return new Spell(name, color, sound, new ArrayList<>(recipe));
    }

    public Spell withColor(@NotNull ParticleColor color) {
        return new Spell(name, color, sound, recipe);
    }

    public Spell withSound(@NotNull ConfiguredSpellSound sound){
        this.sound = sound;
        return this;
    }

    public ParticleColor color(){
        return color;
    }

    public String name(){
        return name;
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

    public int getCost(){
        int cost = 0;
        AbstractSpellPart augmentedPart = null;
        for(AbstractSpellPart part : recipe){
            if(part == null)
                continue;
            if(!(part instanceof AbstractAugment))
                augmentedPart = part;

            if(augmentedPart != null && part instanceof AbstractAugment augment) {
                cost += augment.getCostForPart(augmentedPart);
            }else {
                cost += part.getCastingCost();
            }
        }
        return cost;
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
        return tag == null ? new Spell() : ANCodecs.decode(Spell.CODEC.codec(), tag);
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

    public Mutable mutable(){
        return new Mutable(new ArrayList<>(recipe), name, color, sound);
    }

    public static class Mutable{
        public List<AbstractSpellPart> recipe;
        public String name;
        public ParticleColor color;
        public ConfiguredSpellSound sound;

        public Mutable(List<AbstractSpellPart> recipe, String name, ParticleColor color, ConfiguredSpellSound spellSound) {
            this.recipe = recipe;
            this.name = name;
            this.color = color;
            this.sound = spellSound;
        }

        public Mutable add(AbstractSpellPart spellPart) {
            recipe.add(spellPart);
            return this;
        }

        public Mutable add(AbstractSpellPart... spellParts) {
            recipe.addAll(Arrays.asList(spellParts));
            return this;
        }

        public Mutable add(int index, AbstractSpellPart spellPart) {
            recipe.add(index, spellPart);
            return this;
        }

        public Mutable setRecipe(@NotNull List<AbstractSpellPart> recipe) {
            this.recipe = recipe;
            return this;
        }

        public Spell immutable(){
            return new Spell(name, color, sound, recipe);
        }
    }
}
