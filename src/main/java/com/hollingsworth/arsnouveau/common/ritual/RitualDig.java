package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.ritual.RitualContext;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RitualDig extends AbstractRitual {

    public RitualDig(RitualTile tile, RitualContext context) {
        super(tile, context);
    }

    @Override
    public void tick() {
        World world = tile.getLevel();
        if(world.getGameTime() % 20 == 0){
            BlockPos pos = tile.getBlockPos().north().below(context.progress);
            if(pos.getY() <= 1){
                onEnd();
                return;
            }
            world.destroyBlock(pos, true);
            world.destroyBlock(pos.south().south(), true);
            world.destroyBlock(pos.south().east(), true);
            world.destroyBlock(pos.south().west(), true);
            context.progress++;
        }
    }

    @Override
    public String getID() {
        return "Dig";
    }
}
