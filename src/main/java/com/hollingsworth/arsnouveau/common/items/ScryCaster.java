package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.client.renderer.item.ScryCasterRenderer;
import com.hollingsworth.arsnouveau.common.block.ScryerCrystal;
import com.hollingsworth.arsnouveau.common.items.data.ScryPosData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class ScryCaster extends ModItem implements ICasterTool, GeoItem {

    public ScryCaster(Properties properties) {
        super(properties);
    }

    public ScryCaster() {
        super();
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        ItemStack stack = pContext.getItemInHand();
        if (pContext.getLevel().getBlockState(pos).getBlock() instanceof ScryerCrystal) {
            if (!pContext.getLevel().isClientSide) {
                stack.set(DataComponentRegistry.SCRY_DATA, new ScryPosData(pos));
                PortUtil.sendMessage(pContext.getPlayer(), Component.translatable("ars_nouveau.dominion_wand.position_set"));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide) {
            return InteractionResultHolder.pass(stack);
        }

        var caster = this.getSpellCaster(stack);
        if (caster == null) {
            return InteractionResultHolder.pass(stack);
        }
        caster.castOnServer(pUsedHand, Component.translatable("ars_nouveau.invalid_spell"));

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        getInformation(stack, context, tooltip2, flagIn);
        ScryPosData data = stack.get(DataComponentRegistry.SCRY_DATA);
        var pos = data.pos().orElse(null);

        if (pos == null) {
            tooltip2.add(Component.translatable("ars_nouveau.scry_caster.no_pos"));
        } else {
            tooltip2.add(Component.translatable("ars_nouveau.scryer_scroll.bound", pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
        }
        super.appendHoverText(stack, context, tooltip2, flagIn);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final BlockEntityWithoutLevelRenderer renderer = new ScryCasterRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return renderer;
            }
        });
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}
