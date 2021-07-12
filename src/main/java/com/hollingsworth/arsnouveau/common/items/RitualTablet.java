package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.block.RitualBlock;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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
    public ActionResultType useOn(ItemUseContext context) {
        if(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof RitualBlock){
            World world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            RitualTile tile = (RitualTile) world.getBlockEntity(pos);
            tile.setRitual(ritual.getID());
            if(!context.getPlayer().isCreative())
                context.getItemInHand().shrink(1);
            return ActionResultType.CONSUME;
        }
        return ActionResultType.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        tooltip2.add(new TranslationTextComponent("tooltip.ars_nouveau.tablet"));
    }
}
