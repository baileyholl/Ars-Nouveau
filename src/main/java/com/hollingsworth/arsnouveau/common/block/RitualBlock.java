package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class RitualBlock extends ModBlock{
    public RitualBlock(String registryName) {
        super(defaultProperties().noOcclusion().lightLevel((b) -> 15), registryName);
    }


    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!(worldIn.getBlockEntity(pos) instanceof RitualTile) || handIn != Hand.MAIN_HAND || !player.getMainHandItem().isEmpty())
            return super.use(state, worldIn, pos, player, handIn, hit);
        RitualTile tile = (RitualTile) worldIn.getBlockEntity(pos);
        if(tile.ritual != null && !tile.isRitualDone() && (tile.canAffordCost(player.totalExperience) || player.isCreative())) {
            tile.startRitual();
        }



        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RitualTile();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
