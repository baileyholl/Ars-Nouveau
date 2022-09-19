package com.hollingsworth.arsnouveau.api.mob_jar;

import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;

public class JarBehavior<T extends Entity> {

    public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, MobJarTile tile){

    }

    public void tick(MobJarTile tile){}

    /**
     * A helper method to sync the jar change to the client, also sends a block update.
     */
    public void syncClient(MobJarTile tile){
        tile.updateBlock();
    }

    public @Nonnull T entityFromJar(MobJarTile tile){
        return (T) tile.getEntity();
    }

    public boolean isEntityBaby(Entity entity){
        if(!(entity instanceof AgeableMob ageableMob))
            return false;
        return ageableMob.isBaby();
    }
}
