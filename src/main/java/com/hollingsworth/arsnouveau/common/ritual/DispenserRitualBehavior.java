package com.hollingsworth.arsnouveau.common.ritual;


import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class DispenserRitualBehavior implements DispenseItemBehavior {

    @Override
    public ItemStack dispense(BlockSource pSource, ItemStack pStack) {
        BlockPos blockpos = pSource.getPos().relative(pSource.getBlockState().getValue(DispenserBlock.FACING));

        if (pStack.getItem() instanceof RitualTablet tablet && pSource.getLevel().getBlockEntity(blockpos) instanceof RitualBrazierTile brazier){
            if (brazier.canTakeAnotherRitual()){
                brazier.setRitual(tablet.ritual.getID());
                pStack.shrink(1);
            }
        }

        return pStack;
    }

}