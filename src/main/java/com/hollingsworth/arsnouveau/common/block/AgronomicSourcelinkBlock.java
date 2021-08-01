package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.AgronomicSourcelinkTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class AgronomicSourcelinkBlock extends SourcelinkBlock {

    public AgronomicSourcelinkBlock() {
        super(ModBlock.defaultProperties().noOcclusion(), LibBlockNames.MANA_CONDENSER);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if(worldIn.isClientSide)
            return;
        if(BlockUtil.containsStateInRadius(worldIn, pos, 15, AgronomicSourcelinkBlock.class)){
            ((AgronomicSourcelinkTile)worldIn.getBlockEntity(pos)).isDisabled = true;
            if(placer != null)
                PortUtil.sendMessage(placer, new TranslationTextComponent("block.agronomic_sourcelink.disabled"));
        }
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new AgronomicSourcelinkTile();
    }


}
