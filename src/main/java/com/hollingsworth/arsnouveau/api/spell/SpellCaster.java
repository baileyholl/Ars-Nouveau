package com.hollingsworth.arsnouveau.api.spell;

import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.datafixers.util.Function6;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class SpellCaster implements ISpellCaster<SpellCaster>, TooltipProvider {

    public static final MapCodec<SpellCaster> DEFAULT_CODEC = createCodec(SpellCaster::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, SpellCaster> DEFAULT_STREAM = createStream(SpellCaster::new);

    public static <T extends SpellCaster> MapCodec<T> createCodec(Function6<Integer, String, Boolean, String, Integer, SpellSlotMap, T> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.optionalFieldOf("current_slot", 0).forGetter(s -> s.slot),
                Codec.STRING.optionalFieldOf("flavor_text", "").forGetter(s -> s.flavorText),
                Codec.BOOL.optionalFieldOf("is_hidden", false).forGetter(s -> s.isHidden),
                Codec.STRING.optionalFieldOf("hidden_text", "").forGetter(s -> s.hiddenText),
                Codec.INT.optionalFieldOf("max_slots", 1).forGetter(s -> s.maxSlots),
                SpellSlotMap.CODEC.optionalFieldOf("spells", new SpellSlotMap(ImmutableMap.of())).forGetter(s -> s.spells)
        ).apply(instance, constructor));
    }

    public static <T extends SpellCaster> StreamCodec<RegistryFriendlyByteBuf, T> createStream(Function6<Integer, String, Boolean, String, Integer, SpellSlotMap, T> constructor) {
        return StreamCodec.composite(ByteBufCodecs.INT, s -> s.slot, ByteBufCodecs.STRING_UTF8, s -> s.flavorText,
                ByteBufCodecs.BOOL, s -> s.isHidden, ByteBufCodecs.STRING_UTF8, s -> s.hiddenText, ByteBufCodecs.INT, s -> s.maxSlots, SpellSlotMap.STREAM, s -> s.spells,
                constructor);
    }

    protected final SpellSlotMap spells;
    protected final int slot;
    protected final String flavorText;
    protected final boolean isHidden;
    protected final String hiddenText;
    protected final int maxSlots;

    public SpellCaster(){
        this(0, "", false, "", 1);
    }

    public SpellCaster(int maxSlots){
        this(0, "", false, "", maxSlots);
    }

    public SpellCaster(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots) {
        this(slot, flavorText, isHidden, hiddenText, maxSlots, new SpellSlotMap(ImmutableMap.of()));
    }

    public SpellCaster(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots, SpellSlotMap spells){
        this.slot = slot;
        this.flavorText = flavorText;
        this.isHidden = isHidden;
        this.hiddenText = hiddenText;
        this.spells = spells;
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
        return new SpellCaster(slot, flavorText, isHidden, hiddenText, maxSlots, spells.put(slot, spell));
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
        return new SpellCaster(slot, flavorText, isHidden, hiddenText, maxSlots, spells.put(slot, new Spell(name, spell.color(), spell.sound(), new ArrayList<>(spell.unsafeList()))));
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
        return new SpellCaster(slot, flavorText, isHidden, hiddenText, maxSlots, this.spells.put(slot, new Spell(spell.name(), color, spell.sound(), new ArrayList<>(spell.unsafeList()))));
    }

   @NotNull
    @Override
    public ConfiguredSpellSound getSound(int slot) {
        return this.getSpell(slot).sound();
    }

    @Override
    public SpellCaster setSound(ConfiguredSpellSound sound, int slot) {
        var spell = this.getSpell(slot);
        return new SpellCaster(slot, flavorText, isHidden, hiddenText, maxSlots, this.spells.put(slot, new Spell(spell.name(), spell.color(), sound, new ArrayList<>(spell.unsafeList()))));
    }

   @NotNull
    @Override
    public ParticleColor getColor() {
        return this.getSpell().color();
    }

    @Override
    public SpellSlotMap getSpells() {
        return spells;
    }

    public DataComponentType getComponentType() {
        return DataComponentRegistry.SPELL_CASTER.get();
    }

    public void saveToStack(ItemStack stack){
        stack.set(this.getComponentType(), this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpellCaster caster = (SpellCaster) o;
        return slot == caster.slot && isHidden == caster.isHidden && maxSlots == caster.maxSlots && Objects.equals(spells, caster.spells) && Objects.equals(flavorText, caster.flavorText) && Objects.equals(hiddenText, caster.hiddenText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spells, slot, flavorText, isHidden, hiddenText, maxSlots);
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {

    }
}
