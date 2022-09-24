package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class GoatBehavior extends JarBehavior<Goat> {

    @Override
    public void use(BlockState state, Level world, BlockPos pos, Player pPlayer, InteractionHand pHand, BlockHitResult hit, MobJarTile tile) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Goat goat = entityFromJar(tile);
        if (itemstack.is(Items.BUCKET) && !goat.isBaby()) {
            pPlayer.playSound(goat.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_MILK : SoundEvents.GOAT_MILK, 1.0F, 1.0F);
            ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, pPlayer, Items.MILK_BUCKET.getDefaultInstance());
            pPlayer.setItemInHand(pHand, itemstack1);
        }
    }
}
