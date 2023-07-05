package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.LightBlock;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
        CompoundTag tag = stack.getTag();
        if (tag == null)
            stack.setTag(new CompoundTag());
        tag = stack.getTag();
        if (lightExists(tag)) {

            //This shouldn't happen but catch it anyway
            if (getLightLocation(tag) == null) {
                setLightExists(tag, false);
                return;
            }

            BlockPos lightLocation = getLightLocation(tag);

            BlockState state = worldIn.getBlockState(lightLocation);
            // The previous light block was destroyed.
            if (!(state.getBlock() instanceof LightBlock)) {
                setLightExists(tag, false);
                setLightLocation(tag, null);
            }

            if (BlockUtil.distanceFrom(lightLocation, entityIn.blockPosition()) > 7) {
                Direction opposite = entityIn.getDirection().getOpposite();
                BlockPos preferredLightPos = entityIn.blockPosition().relative(opposite, 1);
                removeLight(worldIn, tag);
                if (!placeLight(worldIn, preferredLightPos, tag))
                    placeLight(worldIn, preferredLightPos.above(2), tag);

            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (worldIn.isClientSide)
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);

        CompoundTag tag = playerIn.getItemInHand(handIn).getTag();

        if (tag == null)
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
        //Remove light
        if (lightExists(tag)) {
            removeLight(worldIn, tag);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
        }
        // No light exists. Place a new one.
        placeLight(worldIn, playerIn.blockPosition(), tag);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
    }

    public boolean placeLight(Level world, BlockPos pos, CompoundTag tag) {
        if (world.getBlockState(pos).isAir()) {
            world.setBlockAndUpdate(pos, BlockRegistry.LIGHT_BLOCK.get().defaultBlockState());
            setLightExists(tag, true);
            setLightLocation(tag, pos);
            return true;
        }
        return false;
    }

    public void removeLight(Level world, CompoundTag tag) {
        if (getLightLocation(tag) == null)
            return;

        if (world.getBlockState(getLightLocation(tag)).getBlock() instanceof LightBlock)
            world.setBlockAndUpdate(getLightLocation(tag), Blocks.AIR.defaultBlockState());

        setLightExists(tag, false);
    }


    public boolean lightExists(CompoundTag tag) {
        return tag.contains("light_exists") && tag.getBoolean("light_exists");
    }

    public void setLightExists(CompoundTag tag, boolean lightExists) {
        tag.putBoolean("light_exists", lightExists);
    }

    public BlockPos getLightLocation(CompoundTag tag) {
        if (!tag.contains("x") || !tag.contains("y") || !tag.contains("z"))
            return null;
        return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }

    public void setLightLocation(CompoundTag tag, BlockPos pos) {
        if (pos == null) {
            tag.remove("x");
            tag.remove("y");
            tag.remove("z");
            return;
        }
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
    }

}
