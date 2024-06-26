package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import com.hollingsworth.arsnouveau.client.renderer.item.FlaskCannonRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetLauncher;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class FlaskCannon extends ModItem implements IRadialProvider, GeoItem {
    public FlaskCannon(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        return InteractionResult.FAIL;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if(pLevel.isClientSide)
            return;
        if(!(pEntity instanceof ServerPlayer player)){
            return;
        }
        PotionLauncherData potionLauncherData = new PotionLauncherData(pStack);
        int lastSlot = potionLauncherData.lastSlot;
        if(lastSlot < 0 || lastSlot >= player.inventory.getContainerSize())
            return;
        ItemStack item = player.inventory.getItem(lastSlot);
        if (!(item.getItem() instanceof PotionFlask) && !(item.getItem() instanceof PotionItem)) {
            potionLauncherData.setAmountLeft(0);
        }
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        PotionLauncherData potionLauncherData = new PotionLauncherData(itemstack);
        if(pLevel.isClientSide)
            return InteractionResultHolder.consume(itemstack);
        PotionData potionData = potionLauncherData.getPotionDataFromSlot(pPlayer);
        if(potionData.isEmpty()){
            PortUtil.sendMessage(pPlayer, Component.translatable("ars_nouveau.flask_cannon.no_potion"));
            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
        }

        ThrownPotion thrownpotion = new ThrownPotion(pLevel, pPlayer);
        ItemStack stckToThrow = getThrownStack(pLevel, pPlayer,  pHand, itemstack);
        if(new PotionData(stckToThrow).isEmpty())
            return InteractionResultHolder.success(itemstack);
        thrownpotion.setItem(stckToThrow);
        thrownpotion.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), -20.0F, 0.5F, 1.0F);
        pLevel.addFreshEntity(thrownpotion);
        pPlayer.getCooldowns().addCooldown(this, 10);
        potionLauncherData.setLastDataForRender(new PotionData(stckToThrow));
        return new InteractionResultHolder<>(InteractionResult.CONSUME, itemstack);
    }

    public abstract ItemStack getThrownStack(Level pLevel, Player pPlayer, InteractionHand pHand, ItemStack launcherStack);

    /**
     * How long it takes to use or consume an item
     */
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    /**
     * returns the action that specifies what animation to play when the item is being used
     */
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int forKey() {
        return ModKeyBindings.OPEN_RADIAL_HUD.getKey().getValue();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onRadialKeyPressed(ItemStack stack, Player player) {
        List<RadialMenuSlot<AlchemistsCrown.SlotData>> slots = new ArrayList<>();
        for(int i = 0; i < player.inventory.getContainerSize(); i++) {
            if(slots.size() >= 9)
                break;
            ItemStack item = player.inventory.getItem(i);
            PotionData potionData = new PotionData(item);
            if(potionData.isEmpty() || item.getItem() instanceof ArrowItem)
                continue;
            slots.add(new RadialMenuSlot<>(item.getHoverName().getString(), new AlchemistsCrown.SlotData(i, item)));
        }
        if(slots.isEmpty()) {
            PortUtil.sendMessage(Minecraft.getInstance().player, Component.translatable("ars_nouveau.alchemists_crown.no_flasks"));
            return;
        }
        Minecraft.getInstance().setScreen(new GuiRadialMenu<>(new RadialMenu<>((int index) -> {
            Networking.sendToServer(new PacketSetLauncher(slots.get(index).primarySlotIcon().getSlot()));
        }, slots, (slotData, posestack, positionx, posy, size, transparent) -> RenderUtil.drawItemAsIcon(slotData.getStack(), posestack, positionx, posy, size, transparent), 3)));
    }

    public AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {}

    public static class SplashLauncher extends FlaskCannon {

        public SplashLauncher(Properties properties) {
            super(properties);
        }

        @Override
        public ItemStack getThrownStack(Level pLevel, Player pPlayer, InteractionHand pHand, ItemStack launcherStack) {
            PotionLauncherData data = new PotionLauncherData(launcherStack);
            ItemStack splashStack = new ItemStack(Items.SPLASH_POTION);
            PotionData potionData = data.expendPotion(pPlayer);
            PotionUtils.setPotion(splashStack, potionData.getPotion());
            PotionUtils.setCustomEffects(splashStack, potionData.getCustomEffects());
            return splashStack;
        }

        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer) {
            super.initializeClient(consumer);
            consumer.accept(new IClientItemExtensions() {
                private final BlockEntityWithoutLevelRenderer renderer = new FlaskCannonRenderer(new GenericModel<>("splash_flask_cannon", "item").withEmptyAnim());

                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return renderer;
                }
            });
        }
    }

    public static class LingeringLauncher extends FlaskCannon {

        public LingeringLauncher(Properties properties) {
            super(properties);
        }

        @Override
        public ItemStack getThrownStack(Level pLevel, Player pPlayer, InteractionHand pHand, ItemStack launcherStack) {
            PotionLauncherData data = new PotionLauncherData(launcherStack);
            ItemStack splashStack = new ItemStack(Items.LINGERING_POTION);
            PotionData potionData = data.expendPotion(pPlayer);
            PotionUtils.setPotion(splashStack, potionData.getPotion());
            PotionUtils.setCustomEffects(splashStack, potionData.getCustomEffects());
            return splashStack;
        }

        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer) {
            super.initializeClient(consumer);
            consumer.accept(new IClientItemExtensions() {
                private final BlockEntityWithoutLevelRenderer renderer = new FlaskCannonRenderer(new GenericModel<>("lingering_flask_cannon", "item").withEmptyAnim());

                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return renderer;
                }
            });
        }
    }

    public static class PotionLauncherData extends ItemstackData {
        private PotionData lastDataForRender;
        private int lastSlot;
        public int amountLeft;

        public PotionLauncherData(ItemStack stack) {
            super(stack);
            CompoundTag tag = getItemTag(stack);
            if(tag == null)
                return;
            lastDataForRender = PotionData.fromTag(tag.getCompound("lastDataForRender"));
            lastSlot = tag.getInt("lastSlot");
            amountLeft = tag.getInt("amountLeft");
        }

        public PotionData getPotionDataFromSlot(Player player){
            if(lastSlot < 0 || lastSlot >= player.inventory.getContainerSize())
                return new PotionData();
            ItemStack stack = player.inventory.getItem(lastSlot);
            return new PotionData(stack);
        }

        public PotionData expendPotion(Player player){
            if(lastSlot >= player.inventory.getContainerSize())
                return new PotionData();
            ItemStack item = player.inventory.getItem(lastSlot);
            if(item.getItem() instanceof PotionFlask){
                PotionFlask.FlaskData flaskData = new PotionFlask.FlaskData(item);
                if(flaskData.getCount() <= 0 || flaskData.getPotion().isEmpty())
                    return new PotionData();
                PotionData data = flaskData.getPotion().clone();
                flaskData.setCount(flaskData.getCount() - 1);
                setAmountLeft(flaskData.getCount());
                return data;
            }else if(item.getItem() instanceof PotionItem){
                PotionData data = new PotionData(item).clone();
                if(data.isEmpty())
                    return new PotionData();
                item.shrink(1);
                player.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
                setAmountLeft(0);
                return data;
            }
            return new PotionData();
        }

        public void setAmountLeft(int amount){
            amountLeft = amount;
            writeItem();
        }

        public void setLastSlot(int lastSlot) {
            this.lastSlot = lastSlot;
            writeItem();
        }

        public void setLastDataForRender(PotionData lastDataForRender) {
            this.lastDataForRender = lastDataForRender;
            writeItem();
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            tag.putInt("lastSlot", lastSlot);
            tag.put("lastDataForRender", lastDataForRender.toTag());
            tag.putInt("amountLeft", amountLeft);
        }

        public PotionData getLastDataForRender() {
            return lastDataForRender;
        }

        @Override
        public String getTagString() {
            return "potion_launcher";
        }
    }
}
