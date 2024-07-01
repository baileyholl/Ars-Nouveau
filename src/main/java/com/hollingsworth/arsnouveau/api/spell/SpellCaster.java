package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CheatSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SpellCaster implements ISpellCaster {

    public static final MapCodec<SpellCaster> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("current_slot", 0).forGetter(s -> s.slot),
            Codec.STRING.optionalFieldOf("flavor_text", "").forGetter(s -> s.flavorText),
            Codec.BOOL.optionalFieldOf("is_hidden", false).forGetter(s -> s.isHidden),
            Codec.STRING.optionalFieldOf("hidden_text", "").forGetter(s -> s.hiddenText)
    ).apply(instance, SpellCaster::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpellCaster> STREAM = CheatSerializer.create(SpellCaster.CODEC);

    private Map<Integer, Spell> spells = new HashMap<>();
    private int slot;
    public ItemStack stack = ItemStack.EMPTY;
    public String flavorText = "";
    public boolean isHidden;
    public String hiddenText = "";


    public SpellCaster(Integer slot, String flavorText, Boolean isHidden, String hiddenText) {
        this.slot = slot;
        this.flavorText = flavorText;
        this.isHidden = isHidden;
        this.hiddenText = hiddenText;
    }

    @NotNull
    @Override
    public Spell getSpell() {
        return spells.getOrDefault(getCurrentSlot(), new Spell());
    }

    @Override
    public @NotNull Spell getSpell(int slot) {
        return spells.getOrDefault(slot, new Spell());
    }

    @Override
    public int getMaxSlots() {
        return 1;
    }

    @Override
    public int getCurrentSlot() {
        return slot;
    }

    @Override
    public void setCurrentSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public void setSpell(Spell spell, int slot) {
        this.spells.put(slot, spell);
    }

    @Override
    public void setSpell(Spell spell) {
        setSpell(spell, getCurrentSlot());
    }

    @Override
    public ParticleColor getColor(int slot) {
        return this.getSpell(slot).color;
    }

    @Override
    public void setFlavorText(String str) {
        this.flavorText = str;
    }

    @Override
    public String getSpellName(int slot) {
        return this.getSpell(slot).name;
    }

    @Override
    public String getSpellName() {
        return this.getSpellName(getCurrentSlot());
    }

    @Override
    public void setSpellName(String name) {
        setSpellName(name, getCurrentSlot());
    }

    @Override
    public void setSpellName(String name, int slot) {
        this.getSpell(slot).name = name;
    }

    @Override
    public void setSpellHidden(boolean hidden) {
        this.isHidden = hidden;
    }

    @Override
    public boolean isSpellHidden() {
        return isHidden;
    }

    @Override
    public void setHiddenRecipe(String recipe) {
        this.hiddenText = recipe;
    }

    @Override
    public String getHiddenRecipe() {
        return hiddenText;
    }

    @Override
    public String getFlavorText() {
        return flavorText == null ? "" : flavorText;
    }

    @Override
    public void setColor(ParticleColor color) {
        setColor(color, getCurrentSlot());
    }

    @Override
    public void setColor(ParticleColor color, int slot) {
        this.getSpell(slot).color = color;
    }

   @NotNull
    @Override
    public ConfiguredSpellSound getSound(int slot) {
        return this.getSpell(slot).sound;
    }

    @Override
    public void setSound(ConfiguredSpellSound sound) {
        this.setSound(sound, getCurrentSlot());
    }

    @Override
    public void setSound(ConfiguredSpellSound sound, int slot) {
        this.getSpell(slot).sound = sound;
    }

   @NotNull
    @Override
    public ParticleColor getColor() {
        return this.getSpell().color;
    }

    @Override
    public Map<Integer, Spell> getSpells() {
        return spells;
    }

    @Override
    public ResourceLocation getTagID() {
        return ArsNouveau.prefix( "caster");
    }
}
