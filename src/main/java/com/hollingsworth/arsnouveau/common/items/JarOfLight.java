package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.LightBlock;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class JarOfLight extends ModItem {

    public JarOfLight() {
        super(LibItemNames.JAR_OF_LIGHT);
    }
    //If the light is dispatched


    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

        if(worldIn.isClientSide)
            return;
        CompoundNBT tag = stack.getTag();
        if(tag == null)
            stack.setTag(new CompoundNBT());
        tag = stack.getTag();
        if(lightExists(tag)){

            //This shouldn't happen but catch it anyway
            if(getLightLocation(tag) == null) {
                setLightExists(tag, false);
                return;
            }

            BlockPos lightLocation = getLightLocation(tag);

            BlockState state = worldIn.getBlockState(lightLocation);
            // The previous light block was destroyed.
            if(!(state.getBlock() instanceof LightBlock)) {
                setLightExists(tag, false);
                setLightLocation(tag, null);
            }

            if(BlockUtil.distanceFrom(lightLocation, entityIn.blockPosition()) > 7 ){
                Direction opposite = entityIn.getDirection().getOpposite();
                BlockPos preferredLightPos = entityIn.blockPosition().relative(opposite, 1);
                removeLight(worldIn, tag);
                if(!placeLight(worldIn, preferredLightPos, tag))
                    placeLight(worldIn, preferredLightPos.above(2), tag);

            }
        }
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if(worldIn.isClientSide)
            return new ActionResult<>(ActionResultType.SUCCESS, itemstack);

        CompoundNBT tag = playerIn.getItemInHand(handIn).getTag();

        if(tag == null)
            return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
        //Remove light
        if(lightExists(tag)){
            removeLight(worldIn, tag);
            return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
        }
        // No light exists. Place a new one.
        placeLight(worldIn, playerIn.blockPosition(), tag);
        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }

    public boolean placeLight(World world, BlockPos pos, CompoundNBT tag){
        if(world.getBlockState(pos).getMaterial() == Material.AIR){
            world.setBlockAndUpdate(pos, BlockRegistry.LIGHT_BLOCK.defaultBlockState());
            setLightExists(tag, true);
            setLightLocation(tag, pos);
            return true;
        }
        return false;
    }

    public void removeLight(World world, CompoundNBT tag){
        if(getLightLocation(tag) == null)
            return;

        if(world.getBlockState(getLightLocation(tag)).getBlock() instanceof LightBlock)
            world.setBlockAndUpdate(getLightLocation(tag), Blocks.AIR.defaultBlockState());

        setLightExists(tag, false);
    }


    public boolean lightExists(CompoundNBT tag){
        return tag.contains("light_exists") && tag.getBoolean("light_exists");
    }

    public void setLightExists(CompoundNBT tag, boolean lightExists){
        tag.putBoolean("light_exists", lightExists);
    }

    public BlockPos getLightLocation(CompoundNBT tag){
        if(!tag.contains("x") || !tag.contains("y") || !tag.contains("z"))
            return null;
        return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }

    public void setLightLocation(CompoundNBT tag, BlockPos pos){
        if(pos == null){
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
