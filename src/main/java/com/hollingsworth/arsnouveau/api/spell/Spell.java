package com.hollingsworth.arsnouveau.api.spell;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Spell {

    public static final MapCodec<Spell> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(s -> s.name),
            ParticleColor.CODEC.fieldOf("color").forGetter(s -> s.color),
            ConfiguredSpellSound.CODEC.fieldOf("sound").forGetter(s -> s.sound),
            Codec.list(AbstractSpellPart.CODEC).fieldOf("recipe").forGetter(s -> s.recipe)
    ).apply(instance, Spell::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Spell> STREAM = StreamCodec.of(
            (buf, val) -> {
                buf.writeUtf(val.name);
                ParticleColor.STREAM.encode(buf, val.color);
                ConfiguredSpellSound.STREAM.encode(buf, val.sound);
                AbstractSpellPart.STREAM_LIST.encode(buf, val.recipe);
            },
            buf -> {
                String name = buf.readUtf();
                ParticleColor color = ParticleColor.STREAM.decode(buf);
                ConfiguredSpellSound sound = ConfiguredSpellSound.STREAM.decode(buf);
                List<AbstractSpellPart> recipe = AbstractSpellPart.STREAM_LIST.decode(buf);
                return new Spell(name, color, sound, recipe);
            }
    );


    private final List<AbstractSpellPart> recipe;
    private final String name;
    private final ParticleColor color;
    private final ConfiguredSpellSound sound;


    public Spell() {
        this("", ParticleColor.defaultParticleColor(), ConfiguredSpellSound.DEFAULT, ImmutableList.of());
    }

    public Spell(AbstractSpellPart... spellParts) {
        this(Arrays.asList(spellParts));
    }

    public Spell(List<AbstractSpellPart> recipe) {
        this("", ParticleColor.defaultParticleColor(), ConfiguredSpellSound.DEFAULT, recipe);
    }

    public Spell(String name, ParticleColor color, ConfiguredSpellSound configuredSpellSound, List<AbstractSpellPart> abstractSpellParts) {
        this.name = name;
        this.color = color;
        this.sound = configuredSpellSound;
        this.recipe = ImmutableList.copyOf(abstractSpellParts);
    }

    public Spell(String name, ParticleColor color, ConfiguredSpellSound configuredSpellSound, ImmutableList<AbstractSpellPart> abstractSpellParts) {
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
        return new Spell(name, color, sound, ImmutableList.copyOf(recipe));
    }

    public Spell setRecipe(@NotNull ImmutableList<AbstractSpellPart> recipe) {
        return new Spell(name, color, sound, recipe);
    }

    public Spell withColor(@NotNull ParticleColor color) {
        return new Spell(name, color, sound, recipe);
    }

    public Spell withSound(@NotNull ConfiguredSpellSound sound){
        return new Spell(name, color, sound, recipe);
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

    public Mutable mutable(){
        return new Mutable(new ArrayList<>(recipe), name, color, sound);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spell spell = (Spell) o;
        return Objects.equals(recipe, spell.recipe) && Objects.equals(name, spell.name) && Objects.equals(color, spell.color) && Objects.equals(sound, spell.sound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipe, name, color, sound);
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
