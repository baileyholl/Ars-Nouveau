package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CheatSerializer;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.Map;

public class ReactiveCasterData extends SpellCaster {
    public static MapCodec<ReactiveCasterData> CODEC = SpellCaster.createCodec(ReactiveCasterData::new);

    public static StreamCodec<RegistryFriendlyByteBuf, ReactiveCasterData> STREAM_CODEC = CheatSerializer.create(ReactiveCasterData.CODEC.codec());

    public ReactiveCasterData(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots) {
        super(slot, flavorText, isHidden, hiddenText, maxSlots);
    }

    public ReactiveCasterData(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots, Map<Integer, Spell> spells) {
        super(slot, flavorText, isHidden, hiddenText, maxSlots, spells);
    }

    @Override
    public SpellResolver getSpellResolver(SpellContext context, Level worldIn, LivingEntity playerIn, InteractionHand handIn) {
        if(!(playerIn instanceof Player) || playerIn instanceof FakePlayer){
            return new EntitySpellResolver(context);
        }
        return super.getSpellResolver(context, worldIn, playerIn, handIn);
    }
}