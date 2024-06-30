package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.LightBlock;
import com.hollingsworth.arsnouveau.common.items.data.LightJarData;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class JarOfLight extends ModItem {

    public JarOfLight() {
        super();
    }
    //If the light is dispatched


    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

        if (worldIn.isClientSide)
            return;
        LightJarData data = stack.getOrDefault(DataComponentRegistry.LIGHT_JAR, new LightJarData(null, false));
        if (!data.enabled()) {
            return;
        }
        BlockPos lightLocation = data.pos();
        BlockState state = worldIn.getBlockState(lightLocation);
        // The previous light block was destroyed.
        if (!(state.getBlock() instanceof LightBlock)) {
            stack.set(DataComponentRegistry.LIGHT_JAR, new LightJarData(null, true));
        }

        if (BlockUtil.distanceFrom(lightLocation, entityIn.blockPosition()) > 7) {
            Direction opposite = entityIn.getDirection().getOpposite();
            BlockPos preferredLightPos = entityIn.blockPosition().relative(opposite, 1);
            if (worldIn.getBlockState(lightLocation).getBlock() instanceof LightBlock) {
                worldIn.setBlockAndUpdate(lightLocation, Blocks.AIR.defaultBlockState());
            }
            if (!placeLight(worldIn, preferredLightPos)) {
                placeLight(worldIn, preferredLightPos.above(2));
                stack.set(DataComponentRegistry.LIGHT_JAR, new LightJarData(preferredLightPos.above(2), true));
            }else {
                stack.set(DataComponentRegistry.LIGHT_JAR, new LightJarData(preferredLightPos, true));
            }
        }

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (worldIn.isClientSide)
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);

        LightJarData tag = itemstack.getOrDefault(DataComponentRegistry.LIGHT_JAR, new LightJarData(null, false));
        if (tag.pos() != null) {
            removeLight(worldIn, tag);
            itemstack.set(DataComponentRegistry.LIGHT_JAR, new LightJarData(null, false));
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
        }
        // No light exists. Place a new one.
        placeLight(worldIn, playerIn.blockPosition());

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
    }

    public boolean placeLight(Level world, BlockPos pos) {
        if (world.getBlockState(pos).isAir()) {
            world.setBlockAndUpdate(pos, BlockRegistry.LIGHT_BLOCK.get().defaultBlockState());
            return true;
        }
        return false;
    }

    public void removeLight(Level world, LightJarData lightJarData) {
        BlockPos pos = lightJarData.pos();
        if(pos == null){
            return;
        }
        if (world.getBlockState(pos).getBlock() instanceof LightBlock)
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }
}
