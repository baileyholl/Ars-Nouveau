package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.potion.IPotionProvider;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.items.data.MultiPotionContents;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public abstract class PotionFlask extends ModItem implements IPotionProvider {

    public PotionFlask() {
        this(ItemsRegistry.defaultItemProperties().stacksTo(1).durability(8));
    }

    public PotionFlask(Item.Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide ||
                !(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof PotionJarTile jarTile))
            return super.useOn(context);
        ItemStack thisStack = context.getItemInHand();
        MultiPotionContents data = thisStack.getOrDefault(DataComponentRegistry.MULTI_POTION, new MultiPotionContents(0, PotionContents.EMPTY));
        Player playerEntity = context.getPlayer();

        if (playerEntity.isShiftKeyDown() && data.charges() > 0 && jarTile.getMaxFill() - jarTile.getAmount() >= 0 && jarTile.canAccept(data.contents(), 100)) {
            jarTile.add(data.contents(), 100);
            var contents = data.withCharges(data.charges() - 1);
            thisStack.set(DataComponentRegistry.MULTI_POTION, contents);
        }else if (!playerEntity.isShiftKeyDown() && !isMax(thisStack) && jarTile.getAmount() >= 100) {
            if (data.potionData.areSameEffects(jarTile.getData())) {
                var contents = data.withCharges(data.charges() + 1);
                jarTile.remove(100);
                thisStack.set(DataComponentRegistry.MULTI_POTION, contents);
            }else if (data.charges() == 0){
                var contents = new MultiPotionContents(1, jarTile.getData());
                thisStack.set(DataComponentRegistry.MULTI_POTION, contents);
                jarTile.remove(100);
            }
        }
        return super.useOn(context);
    }

    public boolean isMax(ItemStack stack) {
        MultiPotionContents data = stack.getOrDefault(DataComponentRegistry.MULTI_POTION, new MultiPotionContents(0, PotionContents.EMPTY));
        return data.charges() >= getMaxCapacity();
    }

    public int getMaxCapacity() {
        return 8;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        Player playerentity = entityLiving instanceof Player player ? player : null;

        if (!worldIn.isClientSide) {
            MultiPotionContents data = stack.getOrDefault(DataComponentRegistry.MULTI_POTION, new MultiPotionContents(0, PotionContents.EMPTY));
            for (MobEffectInstance effectinstance : data.contents().getAllEffects()) {
                effectinstance = getEffectInstance(effectinstance);
                if (effectinstance.getEffect().value().isInstantenous()) {
                    effectinstance.getEffect().value().applyInstantenousEffect(playerentity, playerentity, entityLiving, effectinstance.getAmplifier(), 1.0D);
                } else {
                    entityLiving.addEffect(new MobEffectInstance(effectinstance));
                }
            }
            stack.set(DataComponentRegistry.MULTI_POTION, data.withCharges(data.charges() - 1));
        }
        return stack;
    }

    //Get the modified EffectInstance from the parent class.
    public abstract@NotNull MobEffectInstance getEffectInstance(MobEffectInstance effectInstance);


    @Override
    public int getDamage(ItemStack stack) {
        MultiPotionContents data = stack.getOrDefault(DataComponentRegistry.MULTI_POTION, new MultiPotionContents(0, PotionContents.EMPTY));
        return (getMaxDamage(stack) - data.charges());
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getMaxCapacity();
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return 0.0f;
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
        MultiPotionContents data = stack.getOrDefault(DataComponentRegistry.MULTI_POTION, new MultiPotionContents(0, PotionContents.EMPTY));
        return data.charges() > 0 ? ItemUtils.startUsingInstantly(worldIn, playerIn, handIn) : InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        MultiPotionContents data = stack.getOrDefault(DataComponentRegistry.MULTI_POTION, new MultiPotionContents(0, PotionContents.EMPTY));
        tooltip.add(Component.translatable("ars_nouveau.flask.charges", data.charges()).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        PotionContents.addPotionTooltip(data.contents().getAllEffects(), tooltip::add, 1.0F, context.tickRate());
    }
}
