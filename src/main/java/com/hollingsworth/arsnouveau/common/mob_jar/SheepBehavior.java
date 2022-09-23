package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SheepBehavior extends JarBehavior<Sheep> {

    @Override
    public void tick(MobJarTile tile) {
        Sheep sheep = entityFromJar(tile);
        if(!sheep.level.isClientSide) {
            if (sheep.getRandom().nextInt(sheep.isBaby() ? 50 : 1000) != 0) {
                return;
            }
            BlockPos pos1 = tile.getBlockPos().below();
            if (sheep.level.getBlockState(pos1).is(Blocks.GRASS_BLOCK)) {
                sheep.level.broadcastEntityEvent(sheep, (byte)10);
                sheep.level.levelEvent(2001, pos1, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                sheep.level.setBlock(pos1, Blocks.DIRT.defaultBlockState(), 2);
                sheep.ate();
                syncClient(tile);
            }
        }
    }

    @Override
    public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, MobJarTile tile) {
        ItemStack stack = player.getItemInHand(handIn);
        if(stack.getItem() instanceof ShearsItem shearsItem){
            shearsItem.interactLivingEntity(stack, player, entityFromJar(tile), handIn);
            syncClient(tile);
        }
    }
}
