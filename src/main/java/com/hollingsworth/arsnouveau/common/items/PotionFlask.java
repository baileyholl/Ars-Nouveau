package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PotionFlask extends Item {
    public PotionFlask() {
        super(ItemsRegistry.defaultItemProperties().maxStackSize(1));
        setRegistryName(LibItemNames.POTION_FLASK);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if(context.getWorld().isRemote)
            return super.onItemUse(context);
        ItemStack thisStack = context.getItem();
        Potion potion = PotionUtils.getPotionFromItem(thisStack);
        PlayerEntity playerEntity = context.getPlayer();
        PotionJarTile jarTile = (PotionJarTile) context.getWorld().getTileEntity(context.getPos());
        int count = thisStack.getTag().getInt("count");
        if(jarTile == null)
            return ActionResultType.PASS;
        if(playerEntity.isSneaking() && potion != Potions.EMPTY && count > 0 && jarTile.getMaxFill() - jarTile.getCurrentFill() >= 0){
            if(jarTile.getPotion() == Potions.EMPTY || potion == jarTile.getPotion()){
                jarTile.setPotion(potion);
                jarTile.addAmount(potion, 100);
                thisStack.getTag().putInt("count", count - 1);
                setCount(thisStack, count -1);
            }
        }

        if(context.getWorld().getTileEntity(context.getPos()) instanceof PotionJarTile && !playerEntity.isSneaking()){

            if(jarTile.getPotion() != Potions.EMPTY && (jarTile.getPotion() == potion || potion == Potions.EMPTY) && jarTile.amount >= 100){
                if(potion == Potions.EMPTY)
                    PotionUtils.addPotionToItemStack(thisStack, jarTile.getPotion());
                setCount(thisStack, 1 + count);
                jarTile.addAmount(-100);
            }
        }
        return super.onItemUse(context);
    }

    public void setCount(ItemStack stack, int count){
        stack.getTag().putInt("count", count);
        if(count <= 0)
            PotionUtils.addPotionToItemStack(stack, Potions.EMPTY);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        PlayerEntity playerentity = entityLiving instanceof PlayerEntity ? (PlayerEntity)entityLiving : null;
        if (playerentity instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)playerentity, stack);
        }

        if (!worldIn.isRemote) {
            for(EffectInstance effectinstance : PotionUtils.getEffectsFromStack(stack)) {
                if (effectinstance.getPotion().isInstant()) {
                    effectinstance.getPotion().affectEntity(playerentity, playerentity, entityLiving, effectinstance.getAmplifier(), 1.0D);
                } else {
                    entityLiving.addPotionEffect(new EffectInstance(effectinstance));
                }
            }
            if(stack.hasTag()){
                int count = stack.getTag().getInt("count") - 1;
                setCount(stack, count);
            }
        }
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if(!worldIn.isRemote){
         //   PotionUtils.addPotionToItemStack(stack, Potions.WEAKNESS);
            if(!stack.hasTag())
                stack.setTag(new CompoundNBT());
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
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        return stack.hasTag() && stack.getTag().getInt("count") > 0 ? DrinkHelper.startDrinking(worldIn, playerIn, handIn) : ActionResult.resultPass(playerIn.getHeldItem(handIn));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if(stack.hasTag()){
            tooltip.add(new TranslationTextComponent("ars_nouveau.flask.charges", stack.getTag().getInt("count")));
            PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
        }
    }
}
