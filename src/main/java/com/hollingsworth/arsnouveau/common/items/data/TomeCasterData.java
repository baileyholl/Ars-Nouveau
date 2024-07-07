package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellSlotMap;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.NotEnoughManaPacket;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TomeCasterData extends SpellCaster {
    public static final MapCodec<TomeCasterData> CODEC = SpellCaster.createCodec(TomeCasterData::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, TomeCasterData> STREAM_CODEC = createStream(TomeCasterData::new);


    public TomeCasterData() {
        super();
    }

    public TomeCasterData(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots) {
        super(slot, flavorText, isHidden, hiddenText, maxSlots);
    }

    public TomeCasterData(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots, SpellSlotMap spells) {
        super(slot, flavorText, isHidden, hiddenText, maxSlots, spells);
    }

    @Override
    public SpellResolver getSpellResolver(SpellContext context, Level worldIn, LivingEntity playerIn, InteractionHand handIn) {
        return new SpellResolver(context) {
            @Override
            protected boolean enoughMana(LivingEntity entity) {
                int totalCost = getResolveCost();
                IManaCap manaCap = CapabilityRegistry.getMana(entity).orElse(null);
                if (manaCap == null)
                    return false;
                boolean canCast = totalCost <= manaCap.getCurrentMana() || manaCap.getCurrentMana() == manaCap.getMaxMana() || (entity instanceof Player player && player.isCreative());
                if (!canCast && !entity.getCommandSenderWorld().isClientSide && !silent) {
                    PortUtil.sendMessageNoSpam(entity, Component.translatable("ars_nouveau.spell.no_mana"));
                    if (entity instanceof ServerPlayer serverPlayer)
                        Networking.sendToPlayerClient(new NotEnoughManaPacket(totalCost), serverPlayer);
                }
                return canCast;
            }

        };
    }

    @Override
    public DataComponentType getComponentType() {
        return DataComponentRegistry.TOME_CASTER.get();
    }
}
