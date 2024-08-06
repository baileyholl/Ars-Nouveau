package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.IWrappedCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * An interface for handling NBT as it relates to items that may cast spells.
 * See SpellCaster for implementation.
 */
public interface ISpellCaster {

    @NotNull
    Spell getSpell();

    @NotNull
    Spell getSpell(int slot);

    int getMaxSlots();

    int getCurrentSlot();

    void setCurrentSlot(int slot);

    default void setNextSlot() {
        int slot = getCurrentSlot() + 1;
        if (slot >= getMaxSlots()) {
            slot = 0;
        }
        setCurrentSlot(slot);
    }

    default void setPreviousSlot() {
        int slot = getCurrentSlot() - 1;
        if (slot < 0)
            slot = getMaxSlots() - 1;
        setCurrentSlot(slot);
    }

    void setSpell(Spell spell, int slot);

    void setSpell(Spell spell);

    @NotNull
    ParticleColor getColor(int slot);

    @NotNull
    ParticleColor getColor();

    void setColor(ParticleColor color);

    void setColor(ParticleColor color, int slot);

    @NotNull
    ConfiguredSpellSound getSound(int slot);

    void setSound(ConfiguredSpellSound sound);

    void setSound(ConfiguredSpellSound sound, int slot);

    default ConfiguredSpellSound getCurrentSound() {
        return getSound(getCurrentSlot());
    }

    void setFlavorText(String str);

    String getSpellName(int slot);

    String getSpellName();

    void setSpellName(String name);

    void setSpellName(String name, int slot);

    void setSpellHidden(boolean hidden);

    boolean isSpellHidden();

    void setHiddenRecipe(String recipe);

    String getHiddenRecipe();

    String getFlavorText();

    Map<Integer, Spell> getSpells();

    @NotNull
    default Spell getSpell(Level world, LivingEntity playerEntity, InteractionHand hand, ISpellCaster caster) {
        return caster.getSpell();
    }

    default Spell modifySpellBeforeCasting(Level worldIn, @Nullable Entity playerIn, @Nullable InteractionHand handIn, Spell spell) {
        return spell;
    }

    default InteractionResultHolder<ItemStack> castSpell(Level worldIn, LivingEntity entity, InteractionHand handIn, @Nullable Component invalidMessage, @NotNull Spell spell) {
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
        boolean isSensitive = resolver.spell.getBuffsAtIndex(0, entity, AugmentSensitive.INSTANCE) > 0;
        HitResult result = SpellUtil.rayTrace(entity, 0.5 + player.getBlockReach(), 0, isSensitive);
        if (result instanceof BlockHitResult blockHit) {
            BlockEntity tile = worldIn.getBlockEntity(blockHit.getBlockPos());
            if (tile instanceof ScribesTile)
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

            if (!entity.isShiftKeyDown() && tile != null && !(worldIn.getBlockState(blockHit.getBlockPos()).is(BlockTagProvider.IGNORE_TILE))) {
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
            }
        }

        if (result instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity) {
            if (resolver.onCastOnEntity(stack, entityHitResult.getEntity(), handIn))
                playSound(entity.getOnPos(), worldIn, entity, spell.sound, SoundSource.PLAYERS);
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }

        if (result instanceof BlockHitResult blockHitResult && (result.getType() == HitResult.Type.BLOCK || isSensitive)) {
            if (entity instanceof Player) {
                UseOnContext context = new UseOnContext(player, handIn, (BlockHitResult) result);
                if (resolver.onCastOnBlock(context))
                    playSound(entity.getOnPos(), worldIn, entity, spell.sound, SoundSource.PLAYERS);
            } else if (resolver.onCastOnBlock(blockHitResult)) {
                playSound(entity.getOnPos(), worldIn, entity, spell.sound, SoundSource.NEUTRAL);
            }
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }

        if (resolver.onCast(stack, worldIn))
            playSound(entity.getOnPos(), worldIn, entity, spell.sound, SoundSource.PLAYERS);
        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
    }

    default InteractionResultHolder<ItemStack> castSpell(Level worldIn, LivingEntity playerIn, InteractionHand handIn, Component invalidMessage) {
        return castSpell(worldIn, playerIn, handIn, invalidMessage, getSpell(worldIn, playerIn, handIn, this));
    }

    default void copyFromCaster(ISpellCaster other) {
        for (int i = 0; i < getMaxSlots() && i < other.getMaxSlots(); i++) {
            setSpell(other.getSpell(i), i);
            setFlavorText(other.getFlavorText());
        }
    }

    default SpellResolver getSpellResolver(SpellContext context, Level worldIn, LivingEntity playerIn, InteractionHand handIn) {
        return new SpellResolver(context);
    }

    default void playSound(BlockPos pos, Level worldIn, @Nullable Entity playerIn, ConfiguredSpellSound configuredSound, SoundSource source) {
        if (configuredSound == null || configuredSound.sound == null || configuredSound.sound.getSoundEvent() == null || configuredSound.equals(ConfiguredSpellSound.EMPTY))
            return;
        worldIn.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, configuredSound.sound.getSoundEvent(), source, configuredSound.volume, configuredSound.pitch);
    }

    ResourceLocation getTagID();
}
