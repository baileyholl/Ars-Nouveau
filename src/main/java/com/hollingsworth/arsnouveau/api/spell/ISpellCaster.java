package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.IntangibleAirTile;
import com.hollingsworth.arsnouveau.common.block.tile.PhantomBlockTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * An interface for handling NBT as it relates to items that may cast spells.
 * See SpellCaster for implementation.
 */
//TODO: Rework for spellbook
public interface ISpellCaster {

    @Nonnull Spell getSpell();

    Spell getSpell(int slot);

    int getMaxSlots();

    int getCurrentSlot();

    void setCurrentSlot(int slot);

    void setSpell(Spell spell, int slot);

    void setSpell(Spell spell);

    void setColor(ParticleColor.IntWrapper color);

    void setFlavorText(String str);

    String getFlavorText();
    //TODO: Make color a slotted map
    @Nonnull ParticleColor.IntWrapper getColor();

    Map<Integer, Spell> getSpells();

    //TODO: Add map of names for spells

    default Spell getSpell(Level world, Player playerEntity, InteractionHand hand, ISpellCaster caster){
        return caster.getSpell();
    }

    default InteractionResultHolder<ItemStack> castSpell(Level worldIn, Player playerIn, InteractionHand handIn, TranslatableComponent invalidMessage, Spell spell){
        ItemStack stack = playerIn.getItemInHand(handIn);

        if(worldIn.isClientSide)
            return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
        if(spell == null) {
            PortUtil.sendMessageNoSpam(playerIn,invalidMessage);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        SpellResolver resolver = new SpellResolver(new SpellContext(spell, playerIn)
                .withColors(getColor()));
        boolean isSensitive = resolver.spell.getBuffsAtIndex(0, playerIn, AugmentSensitive.INSTANCE) > 0;
        HitResult result = playerIn.pick(5, 0, isSensitive);
        if(result instanceof BlockHitResult && worldIn.getBlockEntity(((BlockHitResult) result).getBlockPos()) instanceof ScribesTile)
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        if(result instanceof BlockHitResult && !playerIn.isShiftKeyDown()){
            if(worldIn.getBlockEntity(((BlockHitResult) result).getBlockPos()) != null &&
                    !(worldIn.getBlockEntity(((BlockHitResult) result).getBlockPos()) instanceof IntangibleAirTile
                            ||(worldIn.getBlockEntity(((BlockHitResult) result).getBlockPos()) instanceof PhantomBlockTile))) {
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
            }
        }
        EntityHitResult entityRes = MathUtil.getLookedAtEntity(playerIn, 25);

        if(entityRes != null && entityRes.getEntity() instanceof LivingEntity){
            resolver.onCastOnEntity(stack, playerIn, (LivingEntity) entityRes.getEntity(), handIn);
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }

        if(result.getType() == HitResult.Type.BLOCK || (isSensitive && result instanceof BlockHitResult)){
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

    default void copySlotFrom(ISpellCaster caster){
        setColor(caster.getColor());
        setSpell(caster.getSpell());
        setFlavorText(caster.getFlavorText());
    }
}
