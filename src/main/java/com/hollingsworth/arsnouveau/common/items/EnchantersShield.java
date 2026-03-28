package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.client.renderer.item.ShieldModel;
import com.hollingsworth.arsnouveau.common.perk.RepairingPerk;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

import static com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry.defaultItemProperties;

public class EnchantersShield extends ShieldItem implements GeoItem {

    public EnchantersShield() {
        super(defaultItemProperties().durability(500));
    }

    public EnchantersShield(Properties p_i48470_1_) {
        super(p_i48470_1_);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel world, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (entity instanceof Player player)
            RepairingPerk.attemptRepair(stack, player);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level p_77659_1_, @NotNull Player p_77659_2_, @NotNull InteractionHand p_77659_3_) {
        return super.use(p_77659_1_, p_77659_2_, p_77659_3_);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
    }

    public AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final GeoItemRenderer<?> renderer = new GeoItemRenderer<>(new ShieldModel());

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                return renderer;
            }
        });
    }

}
