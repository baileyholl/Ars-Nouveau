package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.item.MobJarItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.Item;

public class MobJarItem extends BlockItem implements GeoItem {
    public MobJarItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }


    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            final MobJarItemRenderer renderer = new MobJarItemRenderer();

            @Override
            public software.bernie.geckolib.renderer.GeoItemRenderer<?> getGeoItemRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {

    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext pLevel, @NotNull TooltipDisplay display, @NotNull Consumer<Component> pTooltip, @NotNull TooltipFlag pFlag) {
        super.appendHoverText(stack, pLevel, display, pTooltip, pFlag);
        Entity entity = fromItem(stack, ArsNouveau.proxy.getClientWorld());
        if (entity == null)
            return;
        pTooltip.accept(entity.getDisplayName());
        if (entity.hasCustomName()) {
            MutableComponent name = entity.getType().getDescription().copy();
            name.withStyle(ChatFormatting.GRAY);
            pTooltip.accept(name);
        }
        if (entity.getType().is(EntityTags.DRYGMY_BLACKLIST)) {
            Component blacklisted = Component.translatable("ars_nouveau.drygmy.blacklist").withStyle(ChatFormatting.DARK_RED);
            pTooltip.accept(blacklisted);
        }
    }

    public static Entity fromItem(ItemStack stack, Level level) {
        var jarData = stack.get(DataComponentRegistry.MOB_JAR);
        if (jarData == null)
            return null;
        CompoundTag entityTag = jarData.entityTag().orElse(new CompoundTag());
        if (entityTag.isEmpty())
            return null;
        return MobJarTile.loadEntityFromTag(level, entityTag);
    }
}
