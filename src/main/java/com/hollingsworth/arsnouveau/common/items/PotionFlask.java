package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import com.hollingsworth.arsnouveau.api.potion.IPotionProvider;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
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
        FlaskData data = new FlaskData(thisStack);
        Player playerEntity = context.getPlayer();

        if (playerEntity.isShiftKeyDown() && data.getCount() > 0 && jarTile.getMaxFill() - jarTile.getAmount() >= 0 && jarTile.canAccept(data.getPotion(), 100)) {
            jarTile.add(data.getPotion(), 100);
            data.setCount(data.getCount() - 1);
        }

        if (!playerEntity.isShiftKeyDown() && !isMax(thisStack) && jarTile.getAmount() >= 100) {
            if (data.potionData.areSameEffects(jarTile.getData())) {
                data.setCount(data.getCount() + 1);
                jarTile.remove(100);
            }else if (data.getCount() == 0){
                data.setPotion(jarTile.getData());
                data.setCount(data.getCount() + 1);
                jarTile.remove(100);
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
    public abstract@NotNull MobEffectInstance getEffectInstance(MobEffectInstance effectInstance);


    @Override
    public int getDamage(ItemStack stack) {
        PotionFlask.FlaskData data = new PotionFlask.FlaskData(stack);
        return (getMaxDamage(stack) - data.getCount());
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
        FlaskData data = new FlaskData(stack);
        return data.getCount() > 0 ? ItemUtils.startUsingInstantly(worldIn, playerIn, handIn) : InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        FlaskData data = new FlaskData(stack);
        tooltip.add(Component.translatable("ars_nouveau.flask.charges", data.getCount()).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        data.potionData.appendHoverText(tooltip);
    }

    @Override
    public PotionData getPotionData(ItemStack stack) {
        FlaskData data = new FlaskData(stack);
        return new EnchantedPotionData(data.potionData, data.stack.getItem());
    }

    public static class FlaskData extends ItemstackData {
        private EnchantedPotionData potionData;
        private int count;

        public FlaskData(ItemStack stack) {
            super(stack);
            CompoundTag tag = getItemTag(stack);
            potionData = new EnchantedPotionData();
            if(tag == null)
                return;
            potionData = new EnchantedPotionData(PotionData.fromTag(tag.getCompound("PotionData")), stack.getItem());
            this.count = tag.getInt("count");
        }

        public void setCount(int count) {
            this.count = Math.max(count, 0);
            if(count <= 0){
                potionData = new EnchantedPotionData();
            }
            writeItem();
        }

        public int getCount() {
            return count;
        }

        public void setPotion(PotionData potion) {
            potionData = new EnchantedPotionData(potion, stack.getItem());
            writeItem();
        }

        public PotionData getPotion() {
            return this.getCount() <= 0 ? new PotionData() : new EnchantedPotionData(potionData, stack.getItem());
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

    public static class EnchantedPotionData extends PotionData{
        public Item flaskPotion;

        public EnchantedPotionData(ItemStack stack) {
            super(stack);
            flaskPotion = stack.getItem();
        }

        public EnchantedPotionData(){
            super();
        }

        public EnchantedPotionData(PotionData potionData, Item item) {
            super(potionData);
            flaskPotion = item;
        }

        @Override
        public void applyEffects(Entity source, Entity inDirectSource, LivingEntity target) {
            if(!(flaskPotion instanceof PotionFlask potionFlask)) {
                super.applyEffects(source, inDirectSource, target);
                return;
            }
            for (MobEffectInstance effectinstance : fullEffects()) {
                effectinstance = potionFlask.getEffectInstance(effectinstance);
                if (effectinstance.getEffect().isInstantenous()) {
                    effectinstance.getEffect().applyInstantenousEffect(source, inDirectSource, target, effectinstance.getAmplifier(), 1.0D);
                } else {
                    target.addEffect(new MobEffectInstance(effectinstance), source);
                }
            }
        }
    }
}
