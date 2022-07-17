package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
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
        if (context.getLevel().isClientSide ||
                !(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof PotionJarTile jarTile))
            return super.useOn(context);
        ItemStack thisStack = context.getItemInHand();
        FlaskData data = new FlaskData(thisStack);
        Player playerEntity = context.getPlayer();

        if (playerEntity.isShiftKeyDown() && data.getCount() > 0 && jarTile.getMaxFill() - jarTile.getAmount() >= 0 && jarTile.canAccept(data.getPotion())) {
            jarTile.add(data.getPotion(), 100);
            data.setCount(data.getCount() - 1);
        }

        if (!playerEntity.isShiftKeyDown() && !isMax(thisStack) && jarTile.getAmount() >= 100) {
            if (data.potionData.areSameEffects(jarTile.getData())) {
                data.setCount(data.getCount() + 1);
                jarTile.remove(100);
            }else if (data.getCount() == 0){
                data.potionData = jarTile.getData();
                data.setCount(1);
            }
        }
        return super.useOn(context);
    }

    public boolean isMax(ItemStack stack) {
        FlaskData data = new FlaskData(stack);
        return data.getCount() >= getMaxCapacity();
    }

    public int getMaxCapacity() {
        return 8;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        Player playerentity = entityLiving instanceof Player player ? player : null;
        if (playerentity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
        }

        if (!worldIn.isClientSide) {
            FlaskData data = new FlaskData(stack);
            for (MobEffectInstance effectinstance : data.getPotion().fullEffects()) {
                effectinstance = getEffectInstance(effectinstance);
                if (effectinstance.getEffect().isInstantenous()) {
                    effectinstance.getEffect().applyInstantenousEffect(playerentity, playerentity, entityLiving, effectinstance.getAmplifier(), 1.0D);
                } else {
                    entityLiving.addEffect(new MobEffectInstance(effectinstance));
                }
            }
            data.setCount(data.getCount() - 1);
        }
        return stack;
    }

    //Get the modified EffectInstance from the parent class.
    public abstract @Nonnull MobEffectInstance getEffectInstance(MobEffectInstance effectInstance);


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
        FlaskData data = new FlaskData(stack);
        return data.getCount() > 0 ? ItemUtils.startUsingInstantly(worldIn, playerIn, handIn) : InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        FlaskData data = new FlaskData(stack);
        tooltip.add(Component.translatable("ars_nouveau.flask.charges", data.getCount()).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        data.potionData.appendHoverText(tooltip);
    }

    public static class FlaskData extends ItemstackData {
        private PotionData potionData;
        private int count;

        public FlaskData(ItemStack stack) {
            super(stack);
            CompoundTag tag = getItemTag(stack);
            if(tag == null)
                return;
            potionData = PotionData.fromTag(tag.getCompound("PotionData"));
            this.count = tag.getInt("count");
        }

        public void setCount(int count) {
            this.count = Math.max(count, 0);
            if(count <= 0){
                potionData = new PotionData();
            }
            writeItem();
        }

        public int getCount() {
            return count;
        }

        public void setPotion(PotionData potion) {
            potionData = potion;
            writeItem();
        }

        public PotionData getPotion() {
            return potionData;
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            tag.put("PotionData", potionData.toTag());
            tag.putInt("count", count);
        }

        @Override
        public String getTagString() {
            return "an_potion_flask";
        }
    }
}
