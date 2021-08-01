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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class PotionFlask extends ModItem {
    public PotionFlask() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1));
        setRegistryName(LibItemNames.POTION_FLASK);
    }

    public PotionFlask(Item.Properties props, String registryName){
        super(props);
        setRegistryName(registryName);
    }

    public PotionFlask(String registryName){
        super(ItemsRegistry.defaultItemProperties().stacksTo(1));
        setRegistryName(registryName);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if(context.getLevel().isClientSide)
            return super.useOn(context);
        ItemStack thisStack = context.getItemInHand();
        Potion potion = PotionUtils.getPotion(thisStack);
        PlayerEntity playerEntity = context.getPlayer();
        if(!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof PotionJarTile))
            return super.useOn(context);
        PotionJarTile jarTile = (PotionJarTile) context.getLevel().getBlockEntity(context.getClickedPos());
        int count = thisStack.getTag().getInt("count");
        if(jarTile == null)
            return ActionResultType.PASS;
        if(playerEntity.isShiftKeyDown() && potion != Potions.EMPTY && count > 0 && jarTile.getMaxFill() - jarTile.getCurrentFill() >= 0){
            if(jarTile.getPotion() == Potions.EMPTY || jarTile.isMixEqual(thisStack)){
                if(jarTile.getPotion() == Potions.EMPTY) {
                    jarTile.setPotion(potion, PotionUtils.getMobEffects(thisStack));
                }
                jarTile.addAmount(100);
                thisStack.getTag().putInt("count", count - 1);
                setCount(thisStack, count -1);
            }
        }

        if(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof PotionJarTile && !playerEntity.isShiftKeyDown() && !isMax(thisStack)){

            if(jarTile.getPotion() != Potions.EMPTY && (jarTile.isMixEqual(thisStack) || potion == Potions.EMPTY) && jarTile.getAmount() >= 100){
                if(potion == Potions.EMPTY) {
                    PotionUtils.setPotion(thisStack, jarTile.getPotion());
                    PotionUtils.setCustomEffects(thisStack, jarTile.getCustomEffects());
                }
                setCount(thisStack, 1 + count);
                jarTile.addAmount(-100);
            }
        }
        return super.useOn(context);
    }

    public boolean isMax(ItemStack stack){

        return stack.getOrCreateTag().getInt("count") >= getMaxCapacity();
    }

    public int getMaxCapacity(){
        return 8;
    }

    public void setCount(ItemStack stack, int count){
        stack.getTag().putInt("count", count);
        if(count <= 0) {
            PotionUtils.setPotion(stack, Potions.EMPTY);
            stack.getTag().remove("CustomPotionEffects");
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        PlayerEntity playerentity = entityLiving instanceof PlayerEntity ? (PlayerEntity)entityLiving : null;
        if (playerentity instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)playerentity, stack);
        }

        if (!worldIn.isClientSide) {
            for(EffectInstance effectinstance : PotionUtils.getMobEffects(stack)) {
                effectinstance = getEffectInstance(effectinstance);
                if (effectinstance.getEffect().isInstantenous()) {
                    effectinstance.getEffect().applyInstantenousEffect(playerentity, playerentity, entityLiving, effectinstance.getAmplifier(), 1.0D);
                } else {
                    entityLiving.addEffect(new EffectInstance(effectinstance));
                }
            }
            if(stack.hasTag()){
                int count = stack.getTag().getInt("count") - 1;
                setCount(stack, count);
            }
        }
        return stack;
    }

    //Get the modified EffectInstance from the parent class.
    public abstract @Nonnull EffectInstance getEffectInstance(EffectInstance effectInstance);

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if(!worldIn.isClientSide){
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
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        return stack.hasTag() && stack.getTag().getInt("count") > 0 ? DrinkHelper.useDrink(worldIn, playerIn, handIn) : ActionResult.pass(playerIn.getItemInHand(handIn));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if(stack.hasTag()){
            tooltip.add(new TranslationTextComponent("ars_nouveau.flask.charges", stack.getTag().getInt("count")));
            PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
        }
    }
}
