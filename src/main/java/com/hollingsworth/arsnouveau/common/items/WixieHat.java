package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyPotionBehavior;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

import java.util.function.Consumer;

public class WixieHat extends AnimModItem implements ICosmeticItem {

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack pStack, @NotNull Player pPlayer, @NotNull LivingEntity pInteractionTarget, @NotNull InteractionHand pUsedHand) {
        if (pInteractionTarget instanceof Starbuncle starbuncle) {
            starbuncle.setBehavior(new StarbyPotionBehavior(starbuncle, new CompoundTag()));
            PortUtil.sendMessage(pPlayer, Component.translatable("ars_nouveau.starbuncle.potion_behavior_set"));
        }
        if (pInteractionTarget instanceof IDecoratable toWix && canWear(pInteractionTarget)) {
            toWix.setCosmeticItem(pStack.split(1));
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext pContext) {
        return super.useOn(pContext);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final BlockEntityWithoutLevelRenderer renderer = new GenericItemRenderer(new GenericModel<>("witch_hat", "item")).withTranslucency();

            @Override
            public @Nullable BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public Vec3 getTranslations() {
        return new Vec3(0, -.24, -.1);
    }


    @Override
    public Vec3 getScaling() {
        return new Vec3(1.0, 1.0, 1.0);
    }

    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof Starbuncle || entity instanceof FamiliarStarbuncle;
    }

    @Override
    public ItemDisplayContext getTransformType() {
        return ItemDisplayContext.NONE;
    }
}
