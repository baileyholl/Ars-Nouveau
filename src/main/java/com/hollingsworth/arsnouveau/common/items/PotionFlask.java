package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.potion.IPotionProvider;
import com.hollingsworth.arsnouveau.api.registry.PotionProviderRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.items.data.MultiPotionContents;
import com.hollingsworth.arsnouveau.common.util.PotionUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class PotionFlask extends ModItem {

    public PotionFlask() {
        this(ItemsRegistry.defaultItemProperties()
                .stacksTo(1)
                .durability(8)
                .component(DataComponentRegistry.MULTI_POTION, new MultiPotionContents(0, PotionContents.EMPTY, 8)));
    }

    public PotionFlask(Item.Properties props) {
        super(props);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide ||
                !(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof PotionJarTile jarTile))
            return super.useOn(context);
        ItemStack thisStack = context.getItemInHand();
        IPotionProvider data = PotionProviderRegistry.from(thisStack);
        Player playerEntity = context.getPlayer();

        if (data == null || playerEntity == null)
            return super.useOn(context);

        PotionContents contents = data.getPotionData(thisStack);
        int usesRemaining = data.usesRemaining(thisStack);
        int maxUses = data.maxUses(thisStack);

        if (playerEntity.isShiftKeyDown() && usesRemaining > 0 && jarTile.getMaxFill() - jarTile.getAmount() >= 0 && jarTile.canAccept(contents, 100)) {
            jarTile.add(contents, 100);
            var newContents = new MultiPotionContents(usesRemaining - 1, contents, maxUses);
            thisStack.set(DataComponentRegistry.MULTI_POTION, newContents);
        } else if (!playerEntity.isShiftKeyDown() && usesRemaining < maxUses && jarTile.getAmount() >= 100) {
            if (PotionUtil.arePotionContentsEqual(contents, jarTile.getData())) {
                var newContents = new MultiPotionContents(usesRemaining + 1, contents, maxUses);
                jarTile.remove(100);
                thisStack.set(DataComponentRegistry.MULTI_POTION, newContents);
            } else if (usesRemaining == 0) {
                var newContents = new MultiPotionContents(1, jarTile.getData(), maxUses);
                thisStack.set(DataComponentRegistry.MULTI_POTION, newContents);
                jarTile.remove(100);
            }
        }
        return super.useOn(context);
    }

    public int getMaxCapacity() {
        return 8;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level worldIn, @NotNull LivingEntity entityLiving) {
        Player playerentity = entityLiving instanceof Player player ? player : null;

        if (!worldIn.isClientSide) {
            MultiPotionContents data = stack.getOrDefault(DataComponentRegistry.MULTI_POTION, new MultiPotionContents(0, PotionContents.EMPTY, 8));
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
    public abstract @NotNull MobEffectInstance getEffectInstance(MobEffectInstance effectInstance);

    public PotionContents getModifiedPotionContents(PotionContents contents) {
        List<MobEffectInstance> newPotionEffects = contents.potion()
            .map(Holder::value)
            .map(Potion::getEffects)
            .stream()
            .flatMap(List::stream)
            .map(this::getEffectInstance)
            .collect(Collectors.toList());

        contents.customEffects().stream().map(this::getEffectInstance).forEach(newPotionEffects::add);

        return new PotionContents(Optional.empty(), contents.customColor(), newPotionEffects);
    }


    @Override
    public int getDamage(ItemStack stack) {
        MultiPotionContents data = stack.getOrDefault(DataComponentRegistry.MULTI_POTION, new MultiPotionContents(0, PotionContents.EMPTY, 8));
        return (getMaxDamage(stack) - data.charges());
    }

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        return getMaxCapacity();
    }

    @Override
    public boolean isDamaged(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack pStack) {
        return true;
    }

    @Override
    public float getXpRepairRatio(@NotNull ItemStack stack) {
        return 0.0f;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, @NotNull LivingEntity p_344979_) {
        return 32;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        MultiPotionContents data = stack.getOrDefault(DataComponentRegistry.MULTI_POTION, new MultiPotionContents(0, PotionContents.EMPTY, 8));
        return data.charges() > 0 ? ItemUtils.startUsingInstantly(worldIn, playerIn, handIn) : InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        MultiPotionContents data = stack.getOrDefault(DataComponentRegistry.MULTI_POTION, new MultiPotionContents(0, PotionContents.EMPTY, 8));
        tooltip.add(Component.translatable("ars_nouveau.flask.charges", data.charges()).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        PotionContents.addPotionTooltip(data.contents().getAllEffects(), tooltip::add, 1.0F, context.tickRate());
    }
}
