package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.api.potion.IPotionProvider;
import com.hollingsworth.arsnouveau.api.registry.PotionProviderRegistry;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import com.hollingsworth.arsnouveau.client.renderer.item.FlaskCannonRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.items.data.PotionLauncherData;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetLauncher;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.common.util.PotionUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
//TODO: Give flask cannons internal inventory instead of using player inventory
public abstract class FlaskCannon extends ModItem implements IRadialProvider, GeoItem {
    public FlaskCannon(Properties properties) {
        super(properties.component(DataComponentRegistry.POTION_LAUNCHER, new PotionLauncherData()));
    }


    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack pStack, @NotNull Player pPlayer, @NotNull LivingEntity pInteractionTarget, @NotNull InteractionHand pUsedHand) {
        return InteractionResult.FAIL;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if(pLevel.isClientSide)
            return;
        if(!(pEntity instanceof ServerPlayer player)){
            return;
        }
        PotionLauncherData potionLauncherData = pStack.getOrDefault(DataComponentRegistry.POTION_LAUNCHER, new PotionLauncherData());
        int lastSlot = potionLauncherData.lastSlot();
        if(lastSlot < 0 || lastSlot >= player.inventory.getContainerSize())
            return;
        ItemStack item = player.inventory.getItem(lastSlot);
        if (potionLauncherData.amountLeft(player) > 0 && PotionProviderRegistry.from(item) == null && !(item.getItem() instanceof PotionItem)) {
            pStack.set(DataComponentRegistry.POTION_LAUNCHER, new PotionLauncherData(potionLauncherData.renderData(), lastSlot));
        }
    }

    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @NotNull InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        PotionLauncherData potionLauncherData = itemstack.get(DataComponentRegistry.POTION_LAUNCHER);
        if(pLevel.isClientSide)
            return InteractionResultHolder.consume(itemstack);
        ItemStack selectedPotion = potionLauncherData.getSelectedStack(pPlayer);
        IPotionProvider potionData = potionLauncherData.getPotionDataFromSlot(pPlayer);
        if(potionData == null || PotionUtil.isEmpty(potionData.getPotionData(selectedPotion)) || potionData.usesRemaining(selectedPotion) <= 0){
            PortUtil.sendMessage(pPlayer, Component.translatable("ars_nouveau.flask_cannon.no_potion"));
            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
        }

        ThrownPotion thrownpotion = new ThrownPotion(pLevel, pPlayer);
        ItemStack stckToThrow = getThrownStack(pLevel, pPlayer,  pHand, itemstack);
        PotionContents contents = stckToThrow.get(DataComponents.POTION_CONTENTS);
        if(contents == PotionContents.EMPTY)
            return InteractionResultHolder.success(itemstack);
        thrownpotion.setItem(stckToThrow);
        thrownpotion.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), -20.0F, 0.5F, 1.0F);
        pLevel.addFreshEntity(thrownpotion);
        pPlayer.getCooldowns().addCooldown(this, 10);
        itemstack.set(DataComponentRegistry.POTION_LAUNCHER, new PotionLauncherData(contents, potionLauncherData.lastSlot()));
        return new InteractionResultHolder<>(InteractionResult.CONSUME, itemstack);
    }

    public abstract ItemStack getThrownStack(Level pLevel, Player pPlayer, InteractionHand pHand, ItemStack launcherStack);

    @Override
    public boolean doesSneakBypassUse(@NotNull ItemStack stack, @NotNull LevelReader world, @NotNull BlockPos pos, @NotNull Player player) {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
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
            PotionContents contents = PotionUtil.getContents(item);
            if(PotionUtil.isEmpty(contents) || item.getItem() instanceof ArrowItem)
                continue;
            slots.add(new RadialMenuSlot<>(item.getHoverName().getString(), new AlchemistsCrown.SlotData(i, item)));
        }
        if(slots.isEmpty()) {
            PortUtil.sendMessage(Minecraft.getInstance().player, Component.translatable("ars_nouveau.alchemists_crown.no_flasks"));
            return;
        }
        Minecraft.getInstance().setScreen(new GuiRadialMenu<>(new RadialMenu<>((int index) -> {
            Networking.sendToServer(new PacketSetLauncher(slots.get(index).primarySlotIcon().getSlot()));
        }, slots, (slotData, posestack, positionx, posy, size, transparent) -> RenderUtils.drawItemAsIcon(slotData.getStack(), posestack, positionx, posy, size, transparent), 3)));
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
            PotionLauncherData data = launcherStack.getOrDefault(DataComponentRegistry.POTION_LAUNCHER, new PotionLauncherData());
            ItemStack splashStack = new ItemStack(Items.SPLASH_POTION);
            PotionContents potionData = data.expendPotion(pPlayer, launcherStack);
            splashStack.set(DataComponents.POTION_CONTENTS, potionData);
            return splashStack;
        }

        @Override
        public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
            consumer.accept(new GeoRenderProvider() {
                private final BlockEntityWithoutLevelRenderer renderer = new FlaskCannonRenderer(new GenericModel<>("splash_flask_cannon", "item").withEmptyAnim());

                public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
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
            PotionLauncherData data = launcherStack.getOrDefault(DataComponentRegistry.POTION_LAUNCHER, new PotionLauncherData());
            ItemStack splashStack = new ItemStack(Items.LINGERING_POTION);
            PotionContents potionData = data.expendPotion(pPlayer, launcherStack);
            splashStack.set(DataComponents.POTION_CONTENTS, potionData);
            return splashStack;
        }


        @Override
        public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
            consumer.accept(new GeoRenderProvider() {
                private final BlockEntityWithoutLevelRenderer renderer = new FlaskCannonRenderer(new GenericModel<>("lingering_flask_cannon", "item").withEmptyAnim());

                public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                    return renderer;
                }
            });
        }

    }

}
