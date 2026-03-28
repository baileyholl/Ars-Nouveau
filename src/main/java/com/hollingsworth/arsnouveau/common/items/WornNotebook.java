package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.client.jei.AliasProvider;
import com.hollingsworth.arsnouveau.client.renderer.item.TatteredTomeRenderer;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
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
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (worldIn.isClientSide()) {
            DocClientUtils.openBook();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
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
            private final GeoItemRenderer<?> renderer = new TatteredTomeRenderer();

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
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
