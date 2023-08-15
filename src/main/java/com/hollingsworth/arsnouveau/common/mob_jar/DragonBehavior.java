package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class DragonBehavior extends JarBehavior<EnderDragon> {
    @Override
    public void use(BlockState state, Level world, BlockPos pos, Player pPlayer, InteractionHand pHand, BlockHitResult hit, MobJarTile tile) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (itemstack.is(Items.GLASS_BOTTLE)) {
            pPlayer.playSound(SoundEvents.BOTTLE_FILL_DRAGONBREATH, 1.0F, 1.0F);
            pPlayer.level.gameEvent(pPlayer, GameEvent.FLUID_PICKUP, pPlayer.position());
            pPlayer.awardStat(Stats.ITEM_USED.get(itemstack.getItem()));
            pPlayer.setItemInHand(pHand, ItemUtils.createFilledResult(itemstack, pPlayer, new ItemStack(Items.DRAGON_BREATH)));
        }
    }
}
