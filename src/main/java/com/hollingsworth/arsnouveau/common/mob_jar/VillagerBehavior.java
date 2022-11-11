package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class VillagerBehavior extends JarBehavior<Villager> {

    @Override
    public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, MobJarTile tile) {
        super.use(state, world, pos, player, handIn, hit, tile);
        Villager villager = entityFromJar(tile);
        villager.mobInteract(player, handIn);

    }

    @Override
    public void tick(MobJarTile tile) {
        if(tile.getLevel().isClientSide)
            return;
        Villager villager = entityFromJar(tile);
        if (!villager.isTrading() && villager.updateMerchantTimer > 0) {
            --villager.updateMerchantTimer;
            if (villager.updateMerchantTimer <= 0) {
                if (villager.increaseProfessionLevelOnUpdate) {
                    villager.increaseMerchantCareer();
                    villager.increaseProfessionLevelOnUpdate = false;
                }
            }
        }
        if(villager.shouldRestock()){
            villager.restock();
        }
    }
}
