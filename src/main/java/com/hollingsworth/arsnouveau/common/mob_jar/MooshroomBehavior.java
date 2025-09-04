package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MooshroomBehavior extends JarBehavior<MushroomCow> {

    @Override
    public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, MobJarTile tile) {
        ItemStack itemstack = player.getItemInHand(handIn);
        MushroomCow mooshroom = entityFromJar(tile);
        if (itemstack.getItem() == Items.SHEARS && mooshroom.readyForShearing()) {
            Cow cow = EntityType.COW.create(world);
            cow.moveTo(mooshroom.getX(), mooshroom.getY(), mooshroom.getZ(), mooshroom.getYRot(), mooshroom.getXRot());
            cow.setHealth(mooshroom.getHealth());
            cow.yBodyRot = mooshroom.yBodyRot;
            if (mooshroom.hasCustomName()) {
                cow.setCustomName(mooshroom.getCustomName());
                cow.setCustomNameVisible(mooshroom.isCustomNameVisible());
            }

            if (mooshroom.isPersistenceRequired()) {
                cow.setPersistenceRequired();
            }

            cow.setInvulnerable(mooshroom.isInvulnerable());
            tile.setEntityData(cow);

            Block mushroomType = mooshroom.getVariant().getBlockState().getBlock();
            for (int i = 0; i < 5; ++i) {
                world.addFreshEntity(new ItemEntity(world, mooshroom.getX(), mooshroom.getY(1.0D), mooshroom.getZ(), new ItemStack(mushroomType)));
            }
        } else {
            super.use(state, world, pos, player, handIn, hit, tile);
        }
    }
}
