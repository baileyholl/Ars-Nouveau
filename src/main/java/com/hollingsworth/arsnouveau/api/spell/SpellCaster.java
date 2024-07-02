package com.hollingsworth.arsnouveau.api.spell;

import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CheatSerializer;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public class SpellCaster implements ISpellCaster<SpellCaster> {

    public static final MapCodec<SpellCaster> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("current_slot", 0).forGetter(s -> s.slot),
            Codec.STRING.optionalFieldOf("flavor_text", "").forGetter(s -> s.flavorText),
            Codec.BOOL.optionalFieldOf("is_hidden", false).forGetter(s -> s.isHidden),
            Codec.STRING.optionalFieldOf("hidden_text", "").forGetter(s -> s.hiddenText),
            Codec.INT.optionalFieldOf("max_slots", 1).forGetter(s -> s.maxSlots)
    ).apply(instance, SpellCaster::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpellCaster> STREAM = CheatSerializer.create(SpellCaster.CODEC);

    private final Map<Integer, Spell> spells;
    private final int slot;
    private final String flavorText;
    private final boolean isHidden;
    private final String hiddenText;
    private final int maxSlots;

    public SpellCaster(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots) {
        this(slot, flavorText, isHidden, hiddenText, maxSlots, ImmutableMap.of());
    }

    public SpellCaster(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots, Map<Integer, Spell> spells){
        this.slot = slot;
        this.flavorText = flavorText;
        this.isHidden = isHidden;
        this.hiddenText = hiddenText;
        this.spells = ImmutableMap.copyOf(spells);
        this.maxSlots = maxSlots;
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
        return maxSlots;
    }

    @Override
    public SpellCaster setMaxSlots(int slots) {
        return new SpellCaster(slot, flavorText, isHidden, hiddenText, slots, spells);
    }

    @Override
    public int getCurrentSlot() {
        return slot;
    }

    @Override
    public SpellCaster setCurrentSlot(int slot) {
        return new SpellCaster(slot, flavorText, isHidden, hiddenText, maxSlots, spells);
    }

    @Override
    public SpellCaster setSpell(Spell spell, int slot) {
        return new SpellCaster(slot, flavorText, isHidden, hiddenText, maxSlots, Util.copyAndPut(this.spells, slot, spell));
    }

    @Override
    public ParticleColor getColor(int slot) {
        return this.getSpell(slot).color();
    }

    @Override
    public SpellCaster setFlavorText(String str) {
        return new SpellCaster(slot, str, isHidden, hiddenText, maxSlots, spells);
    }

    @Override
    public String getSpellName(int slot) {
        return this.getSpell(slot).name();
    }

    @Override
    public SpellCaster setSpellName(String name, int slot) {
        var spell = this.getSpell(slot);
        return new SpellCaster(slot, flavorText, isHidden, hiddenText, maxSlots, Util.copyAndPut(this.spells, slot, new Spell(name, spell.color(), spell.sound(), new ArrayList<>(spell.unsafeList()))));
    }

    @Override
    public boolean isSpellHidden() {
        return isHidden;
    }

    @Override
    public SpellCaster setHidden(boolean hidden) {
        return new SpellCaster(slot, flavorText, hidden, hiddenText, maxSlots, spells);
    }

    @Override
    public SpellCaster setHiddenRecipe(String recipe) {
        return new SpellCaster(slot, flavorText, isHidden, recipe, maxSlots, spells);
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
    public SpellCaster setColor(ParticleColor color, int slot) {
        var spell = this.getSpell(slot);
        return new SpellCaster(slot, flavorText, isHidden, hiddenText, maxSlots, Util.copyAndPut(this.spells, slot, new Spell(spell.name(), color, spell.sound(), new ArrayList<>(spell.unsafeList()))));
    }

   @NotNull
    @Override
    public ConfiguredSpellSound getSound(int slot) {
        return this.getSpell(slot).sound();
    }

    @Override
    public SpellCaster setSound(ConfiguredSpellSound sound, int slot) {
        var spell = this.getSpell(slot);
        return new SpellCaster(slot, flavorText, isHidden, hiddenText, maxSlots, Util.copyAndPut(this.spells, slot, new Spell(spell.name(), spell.color(), sound, new ArrayList<>(spell.unsafeList()))));
    }

   @NotNull
    @Override
    public ParticleColor getColor() {
        return this.getSpell().color();
    }

    @Override
    public Map<Integer, Spell> getSpells() {
        return spells;
    }

    public DataComponentType getComponentType() {
        return DataComponentRegistry.SPELL_CASTER.get();
    }

    public void saveToStack(ItemStack stack){
        stack.set(this.getComponentType(), this);
    }
}
