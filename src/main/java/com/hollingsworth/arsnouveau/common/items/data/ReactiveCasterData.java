package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;

public class ReactiveCasterData extends AbstractCaster<ReactiveCasterData> {
    public static MapCodec<ReactiveCasterData> CODEC = SpellCaster.createCodec(ReactiveCasterData::new);

    public static StreamCodec<RegistryFriendlyByteBuf, ReactiveCasterData> STREAM_CODEC = createStream(ReactiveCasterData::new);

    @Override
    public MapCodec<ReactiveCasterData> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ReactiveCasterData> streamCodec() {
        return STREAM_CODEC;
    }

    public ReactiveCasterData(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots) {
        super(slot, flavorText, isHidden, hiddenText, maxSlots);
    }

    public ReactiveCasterData(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots, SpellSlotMap spells) {
        super(slot, flavorText, isHidden, hiddenText, maxSlots, spells);
    }

    @Override
    public SpellResolver getSpellResolver(SpellContext context, Level worldIn, LivingEntity playerIn, InteractionHand handIn) {
        if(!(playerIn instanceof Player) || playerIn instanceof FakePlayer){
            return new EntitySpellResolver(context);
        }
        return super.getSpellResolver(context, worldIn, playerIn, handIn);
    }

    @Override
    public DataComponentType<ReactiveCasterData> getComponentType() {
        return DataComponentRegistry.REACTIVE_CASTER.get();
    }

    @Override
    protected ReactiveCasterData build(int slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots, SpellSlotMap spells) {
        return new ReactiveCasterData(slot, flavorText, isHidden, hiddenText, maxSlots, spells);
    }
}
