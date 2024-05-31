package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import java.util.function.Consumer;

public class StarbuncleShades extends AnimModItem implements ICosmeticItem {

    public StarbuncleShades() {
        super();
        withTooltip(Component.translatable("tooltip.starbuncle_shades"));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pInteractionTarget instanceof IDecoratable starbuncle && canWear(pInteractionTarget)) {
            starbuncle.setCosmeticItem(pStack.split(1));
            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public boolean canWear(LivingEntity entity) {
        return entity instanceof Starbuncle || entity instanceof FamiliarStarbuncle;
    }

    //translation applied to the renderer
    @Override
    public Vec3 getTranslations() {
        return new Vec3(0, -.259, .165);
    }

    //scaling applied to the renderer
    @Override
    public Vec3 getScaling() {
        return new Vec3(1.0, 1.0, 1.0);
    }


    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new GenericItemRenderer(new GenericModel<>("starbuncle_shades", "item")).withTranslucency();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
}
