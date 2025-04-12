package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.client.jei.AliasProvider;
import com.hollingsworth.arsnouveau.client.renderer.item.TatteredTomeRenderer;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class WornNotebook extends ModItem implements GeoItem, AliasProvider {

    public WornNotebook() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1));
        withTooltip(Component.translatable("tooltip.worn_notebook"));
    }

   @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if(worldIn.isClientSide){
            DocClientUtils.openBook();
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }
    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final BlockEntityWithoutLevelRenderer renderer = new TatteredTomeRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public Collection<Alias> getAliases() {
        return List.of(
            new Alias("worn_notebook", "Worn Notebook"),
            new Alias("guide", "Guide"),
            new Alias("documentation", "Documentation"),
            new Alias("book", "Book")
        );
    }
}
