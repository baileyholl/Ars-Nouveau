package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

import java.util.function.Consumer;

public class StarbuncleShades extends AnimModItem implements ICosmeticItem {

    public static final Vec3 starbyTrans = new Vec3(0, -.259, .165);
    public static final Vec3 starbyScale = new Vec3(1.0, 1.0, 1.0);

    public StarbuncleShades() {
        super();
        withTooltip(Component.translatable("tooltip.starbuncle_shades"));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack pStack, @NotNull Player pPlayer, @NotNull LivingEntity pInteractionTarget, @NotNull InteractionHand pUsedHand) {
        if (pInteractionTarget instanceof IDecoratable starbuncle && canWear(pInteractionTarget)) {
            starbuncle.setCosmeticItem(pStack.split(1));
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof Starbuncle || entity instanceof FamiliarEntity;
    }

    //translation applied to the renderer
    @Override
    public Vec3 getTranslations(LivingEntity entity) {
        return switch (entity) {
            case FamiliarBookwyrm ignored -> new Vec3(0, -.235, .095);
            case FamiliarWixie ignored -> new Vec3(0, -.15, .26);
            case FamiliarDrygmy ignored -> new Vec3(0, -.13, .275);
            case FamiliarWhirlisprig ignored -> new Vec3(0, -.175, .275);
            case null, default -> starbyTrans;
        };
    }

    //scaling applied to the renderer
    @Override
    public Vec3 getScaling(LivingEntity entity) {
        return switch (entity) {
            case FamiliarBookwyrm ignored -> defaultScaling;
            case FamiliarWixie ignored -> new Vec3(1.0, 1.0, 1.0);
            case FamiliarDrygmy ignored -> defaultScaling.scale(1.125);
            case FamiliarWhirlisprig ignored -> defaultScaling.scale(1.125);
            case null, default -> starbyScale;
        };
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final BlockEntityWithoutLevelRenderer renderer = new GenericItemRenderer(new GenericModel<>("starbuncle_shades", "item")).withTranslucency();

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return renderer;
            }
        });
    }

}
