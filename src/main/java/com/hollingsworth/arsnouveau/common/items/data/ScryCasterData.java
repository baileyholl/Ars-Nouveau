package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.IWrappedCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.ScryerCrystal;
import com.hollingsworth.arsnouveau.common.items.ScryerScroll;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ScryCasterData extends SpellCaster {

    public static final MapCodec<ScryCasterData> CODEC = createCodec(ScryCasterData::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, ScryCasterData> STREAM_CODEC = createStream(ScryCasterData::new);

    public ScryCasterData() {
        super();
    }

    public ScryCasterData(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots) {
        super(slot, flavorText, isHidden, hiddenText, maxSlots);
    }

    public ScryCasterData(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots, SpellSlotMap spells) {
        super(slot, flavorText, isHidden, hiddenText, maxSlots, spells);
    }

    @Override
    public InteractionResultHolder<ItemStack> castSpell(Level worldIn, LivingEntity entity, InteractionHand handIn, @org.jetbrains.annotations.Nullable Component invalidMessage, @NotNull Spell spell) {
        ItemStack stack = entity.getItemInHand(handIn);

        if (worldIn.isClientSide)
            return InteractionResultHolder.pass(entity.getItemInHand(handIn));
        spell = modifySpellBeforeCasting(worldIn, entity, handIn, spell);
        if (!spell.isValid() && invalidMessage != null) {
            PortUtil.sendMessageNoSpam(entity, invalidMessage);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        Player player = entity instanceof Player thisPlayer ? thisPlayer : ANFakePlayer.getPlayer((ServerLevel) worldIn);
        IWrappedCaster wrappedCaster = entity instanceof Player pCaster ? new PlayerCaster(pCaster) : new LivingCaster(entity);
        SpellResolver resolver = getSpellResolver(new SpellContext(worldIn, spell, entity, wrappedCaster, stack), worldIn, player, handIn);
        ITurretBehavior behavior = BasicSpellTurret.TURRET_BEHAVIOR_MAP.get(spell.getCastMethod());
        if(behavior == null){
            PortUtil.sendMessage(entity, Component.translatable("ars_nouveau.scry_caster.invalid_behavior"));
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }

        ScryPosData data = stack.get(DataComponentRegistry.SCRY_DATA);
        boolean playerHoldingScroll = entity.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ScryerScroll;
        BlockPos scryPos = playerHoldingScroll ? player.getItemInHand(InteractionHand.OFF_HAND).getOrDefault(DataComponentRegistry.SCRY_DATA, new ScryPosData(null)).pos() : data.pos();
        if(scryPos == null){
            PortUtil.sendMessage(entity, Component.translatable("ars_nouveau.scry_caster.no_pos"));
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }
        if(!worldIn.isLoaded(scryPos)){
            PortUtil.sendMessage(entity, Component.translatable("ars_nouveau.camera.not_loaded"));
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }
        BlockState castingAtState = worldIn.getBlockState(scryPos);
        if(!(castingAtState.getBlock() instanceof ScryerCrystal)){
            PortUtil.sendMessage(entity, Component.translatable("ars_nouveau.scry_caster.not_crystal"));
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }

        if(!resolver.canCast(player)){
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }

        Position position;
        Direction direction = castingAtState.getValue(ScryerCrystal.FACING);
        // Target the block the crystal is facing if the spell is a touch spell.
        if(spell.getCastMethod() instanceof MethodTouch){
            position = BasicSpellTurret.getDispensePosition(scryPos, direction);
        }else{
            position = ScryerCrystal.getDispensePosition(scryPos, direction);
        }
        behavior.onCast(resolver, (ServerLevel) worldIn, scryPos,
                player,
                position,
                direction);
        resolver.expendMana();
        playSound(entity.getOnPos(), worldIn, entity, getCurrentSound(), SoundSource.PLAYERS);
        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
    }

    @Override
    public DataComponentType getComponentType() {
        return DataComponentRegistry.SCRY_CASTER.get();
    }
}
