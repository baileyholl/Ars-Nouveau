package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.client.renderer.item.MobJarItemRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
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
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class MobJarItem extends BlockItem implements IAnimatable {
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
    public void registerControllers(AnimationData data) {

    }

    AnimationFactory factory = GeckoLibUtil.createFactory(this);
    @Override
    public AnimationFactory getFactory() {
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
