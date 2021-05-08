package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.block.RitualBlock;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

}
