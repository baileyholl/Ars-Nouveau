package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.block.RitualBrazierBlock;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
        super(ItemsRegistry.defaultItemProperties(), registryName);
        this.ritual = ritual;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof RitualBrazierBlock){
            Level world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            RitualTile tile = (RitualTile) world.getBlockEntity(pos);
            tile.setRitual(ritual.getID());
            if(!context.getPlayer().isCreative())
                context.getItemInHand().shrink(1);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        tooltip2.add(new TranslatableComponent("tooltip.ars_nouveau.tablet"));
    }
}
