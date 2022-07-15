package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class PotionFlask extends ModItem {

    public PotionFlask() {
        this(ItemsRegistry.defaultItemProperties().stacksTo(1));
    }

    public PotionFlask(Item.Properties props) {
        super(props);
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide)
            return super.useOn(context);
        ItemStack thisStack = context.getItemInHand();
        Potion potion = PotionUtils.getPotion(thisStack);
        Player playerEntity = context.getPlayer();
        if (!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof PotionJarTile jarTile))
            return super.useOn(context);
        int count = thisStack.getTag().getInt("count");
        if (jarTile == null)
            return InteractionResult.PASS;
        if (playerEntity.isShiftKeyDown() && potion != Potions.EMPTY && count > 0 && jarTile.getMaxFill() - jarTile.getCurrentFill() >= 0) {
            if (jarTile.getPotion() == Potions.EMPTY || jarTile.isMixEqual(thisStack)) {
                if (jarTile.getPotion() == Potions.EMPTY) {
                    jarTile.setPotion(potion, PotionUtils.getMobEffects(thisStack));
                }
                jarTile.addAmount(100);
                thisStack.getTag().putInt("count", count - 1);
                setCount(thisStack, count - 1);
            }
        }

        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof PotionJarTile && !playerEntity.isShiftKeyDown() && !isMax(thisStack)) {

            if (jarTile.getPotion() != Potions.EMPTY && (jarTile.isMixEqual(thisStack) || potion == Potions.EMPTY) && jarTile.getAmount() >= 100) {
                if (potion == Potions.EMPTY) {
                    PotionUtils.setPotion(thisStack, jarTile.getPotion());
                    PotionUtils.setCustomEffects(thisStack, jarTile.getCustomEffects());
                }
                setCount(thisStack, 1 + count);
                jarTile.addAmount(-100);
            }
        }
        return super.useOn(context);
    }

    public boolean isMax(ItemStack stack) {

        return stack.getOrCreateTag().getInt("count") >= getMaxCapacity();
    }

    public int getMaxCapacity() {
        return 8;
    }

    public void setCount(ItemStack stack, int count) {
        stack.getTag().putInt("count", count);
        if (count <= 0) {
            PotionUtils.setPotion(stack, Potions.EMPTY);
            stack.getTag().remove("CustomPotionEffects");
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        Player playerentity = entityLiving instanceof Player ? (Player) entityLiving : null;
        if (playerentity instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) playerentity, stack);
        }

        if (!worldIn.isClientSide) {
            for (MobEffectInstance effectinstance : PotionUtils.getMobEffects(stack)) {
                effectinstance = getEffectInstance(effectinstance);
                if (effectinstance.getEffect().isInstantenous()) {
                    effectinstance.getEffect().applyInstantenousEffect(playerentity, playerentity, entityLiving, effectinstance.getAmplifier(), 1.0D);
                } else {
                    entityLiving.addEffect(new MobEffectInstance(effectinstance));
                }
            }
            if (stack.hasTag()) {
                int count = stack.getTag().getInt("count") - 1;
                setCount(stack, count);
            }
        }
        return stack;
    }

    //Get the modified EffectInstance from the parent class.
    public abstract @Nonnull MobEffectInstance getEffectInstance(MobEffectInstance effectInstance);

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if (!worldIn.isClientSide) {
            //   PotionUtils.addPotionToItemStack(stack, Potions.WEAKNESS);
            if (!stack.hasTag())
                stack.setTag(new CompoundTag());
        }
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        return stack.hasTag() && stack.getTag().getInt("count") > 0 ? ItemUtils.startUsingInstantly(worldIn, playerIn, handIn) : InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.hasTag()) {
            tooltip.add(Component.translatable("ars_nouveau.flask.charges", stack.getTag().getInt("count")));
            PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
        }
    }
}
