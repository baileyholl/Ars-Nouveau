package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * An interface for handling NBT as it relates to items that may cast spells.
 * See SpellCaster for implementation.
 */
public interface ISpellCaster {

    @Nonnull Spell getSpell();

    @Nonnull Spell getSpell(int slot);

    int getMaxSlots();

    int getCurrentSlot();

    void setCurrentSlot(int slot);

    default void setNextSlot(){
        int slot = getCurrentSlot() + 1;
        if(slot > getMaxSlots()){
            slot = 1;
        }
        setCurrentSlot(slot);
    }

    default void setPreviousSlot(){
        int slot = getCurrentSlot() - 1;
        if(slot < 1)
            slot = getMaxSlots();
        setCurrentSlot(slot);
    }

    void setSpell(Spell spell, int slot);

    void setSpell(Spell spell);

    @Nonnull ParticleColor.IntWrapper getColor(int slot);

    @Nonnull ParticleColor.IntWrapper getColor();

    void setColor(ParticleColor.IntWrapper color);

    void setColor(ParticleColor.IntWrapper color, int slot);

    void setFlavorText(String str);

    String getSpellName(int slot);

    String getSpellName();

    void setSpellName(String name);

    void setSpellName(String name, int slot);

    String getFlavorText();

    Map<Integer, Spell> getSpells();

    Map<Integer, String> getSpellNames();

    Map<Integer, ParticleColor.IntWrapper> getColors();

    @Nonnull
    default Spell getSpell(Level world, Player playerEntity, InteractionHand hand, ISpellCaster caster){
        return caster.getSpell();
    }

    default InteractionResultHolder<ItemStack> castSpell(Level worldIn, Player playerIn, InteractionHand handIn, @Nullable TranslatableComponent invalidMessage, @Nonnull Spell spell){
        ItemStack stack = playerIn.getItemInHand(handIn);

        if(worldIn.isClientSide)
            return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));

        if(!spell.isValid() && invalidMessage != null) {
            PortUtil.sendMessageNoSpam(playerIn,invalidMessage);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        SpellResolver resolver = getSpellResolver(new SpellContext(spell, playerIn).withColors(getColor()), worldIn, playerIn, handIn);
        boolean isSensitive = resolver.spell.getBuffsAtIndex(0, playerIn, AugmentSensitive.INSTANCE) > 0;
        HitResult result = SpellUtil.rayTrace(playerIn, 5, 0, isSensitive);
        if(result instanceof BlockHitResult blockHit){
            BlockEntity tile = worldIn.getBlockEntity(blockHit.getBlockPos());
            if(tile instanceof ScribesTile)
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

            if(!playerIn.isShiftKeyDown() && tile != null && !(worldIn.getBlockState(blockHit.getBlockPos()).is(BlockTagProvider.IGNORE_TILE))){
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
            }

        }


        if(result instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity){
            resolver.onCastOnEntity(stack, playerIn, entityHitResult.getEntity(), handIn);
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }

        if(result instanceof BlockHitResult && (result.getType() == HitResult.Type.BLOCK || isSensitive)){
            UseOnContext context = new UseOnContext(playerIn, handIn, (BlockHitResult) result);
            resolver.onCastOnBlock(context);
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }

        resolver.onCast(stack,playerIn,worldIn);
        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
    }

    default InteractionResultHolder<ItemStack> castSpell(Level worldIn, Player playerIn, InteractionHand handIn, TranslatableComponent invalidMessage){
        return castSpell(worldIn, playerIn, handIn, invalidMessage, getSpell(worldIn, playerIn, handIn, this));
    }

    default SpellResolver getSpellResolver(SpellContext context, Level worldIn, Player playerIn, InteractionHand handIn){
        return new SpellResolver(context);
    }

    String getTagID();
}
