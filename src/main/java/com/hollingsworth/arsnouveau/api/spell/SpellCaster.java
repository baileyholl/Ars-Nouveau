package com.hollingsworth.arsnouveau.api.spell;

import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SpellCaster extends AbstractCaster<SpellCaster> {
    public static final MapCodec<SpellCaster> CODEC = createCodec(SpellCaster::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, SpellCaster> STREAM_CODEC = createStream(SpellCaster::new);

    @Override
    public MapCodec<SpellCaster> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SpellCaster> streamCodec() {
        return STREAM_CODEC;
    }

    public SpellCaster() {
        this(0, "", false, "", 1);
    }

    public SpellCaster(int maxSlots) {
        this(0, "", false, "", maxSlots);
    }

    public SpellCaster(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots) {
        this(slot, flavorText, isHidden, hiddenText, maxSlots, new SpellSlotMap(ImmutableMap.of()));
    }

    public SpellCaster(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots, SpellSlotMap spells) {
        super(slot, flavorText, isHidden, hiddenText, maxSlots, spells);
    }

    public static SpellCaster create(int slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots) {
        return new SpellCaster(slot, flavorText, isHidden, hiddenText, maxSlots);
    }

    public DataComponentType<SpellCaster> getComponentType() {
        return DataComponentRegistry.SPELL_CASTER.get();
    }

    @Override
    protected SpellCaster build(int slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots, SpellSlotMap spells) {
        return new SpellCaster(slot, flavorText, isHidden, hiddenText, maxSlots, spells);
    }
}
