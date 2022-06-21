package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class RitualTablet extends ModItem{
    public AbstractRitual ritual;

    public RitualTablet(Properties properties) {
        super(properties);
    }

    public RitualTablet(String registryName, AbstractRitual ritual){
        super(ItemsRegistry.defaultItemProperties());
        this.registryName = registryName;
        this.ritual = ritual;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        if(!context.getLevel().isClientSide() && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof RitualBrazierTile tile){
            if(!tile.canTakeAnotherRitual()){
                context.getPlayer().sendSystemMessage(Component.translatable("ars_nouveau.ritual.no_start"));
                return InteractionResult.PASS;
            }

            tile.setRitual(ritual.getRegistryName());
            if(!context.getPlayer().isCreative())
                context.getItemInHand().shrink(1);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        tooltip2.add(Component.translatable("tooltip.ars_nouveau.tablet"));
    }
}
