package com.hollingsworth.arsnouveau.common.items.summon_charms;

import com.hollingsworth.arsnouveau.api.item.AbstractSummonCharm;
import com.hollingsworth.arsnouveau.common.block.BookwyrmLectern;
import com.hollingsworth.arsnouveau.common.block.tile.BookwyrmLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BookwyrmCharm extends AbstractSummonCharm {
    public BookwyrmCharm() {
        super();
    }

    @Override
    public InteractionResult useOnBlock(UseOnContext context, Level world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == Blocks.LECTERN) {
            world.setBlockAndUpdate(pos, BlockRegistry.BOOKWYRM_LECTERN.defaultBlockState().setValue(BookwyrmLectern.FACING, world.getBlockState(pos).getValue(LecternBlock.FACING)));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOnSummonTile(UseOnContext context, Level world, SummoningTile tile, BlockPos pos) {
        if (tile instanceof BookwyrmLecternTile) {
            EntityBookwyrm wyrm = new EntityBookwyrm(world, pos);
            wyrm.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            world.addFreshEntity(wyrm);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    //TODO:Remove this when rework is done
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        tooltip2.add(Component.literal("Currently disabled for rework, stay tuned for its comeback in the future"));
    }
}
