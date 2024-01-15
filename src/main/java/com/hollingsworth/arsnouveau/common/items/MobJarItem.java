package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.client.renderer.item.MobJarItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class MobJarItem extends BlockItem implements GeoItem {
    public MobJarItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            MobJarItemRenderer renderer = new MobJarItemRenderer();
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
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
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(stack, pLevel, pTooltip, pFlag);
        if(pLevel == null)
            return;
        Entity entity = fromItem(stack, pLevel);
        if(entity == null)
            return;
        pTooltip.add(entity.getDisplayName());
        if (entity.hasCustomName()) {
            MutableComponent name = entity.getType().getDescription().copy();
            name.withStyle(ChatFormatting.GRAY);
            pTooltip.add(name);
        }
        if (entity.getType().is(EntityTags.DRYGMY_BLACKLIST)) {
            Component blacklisted = Component.translatable("ars_nouveau.drygmy.blacklist").withStyle(ChatFormatting.DARK_RED);
            pTooltip.add(blacklisted);
        }
    }

    public static Entity fromItem(ItemStack stack, Level level){
        if(!stack.hasTag())
            return null;
        CompoundTag blockTag = stack.getTag().getCompound("BlockEntityTag");
        CompoundTag entityTag = blockTag.getCompound("entityTag");
        if(entityTag.isEmpty())
            return null;
        return MobJarTile.loadEntityFromTag(level, entityTag);
    }
}
