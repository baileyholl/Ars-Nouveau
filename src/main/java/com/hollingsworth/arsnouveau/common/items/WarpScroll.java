package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class WarpScroll extends ModItem{
    public WarpScroll() {
        super(ItemsRegistry.defaultItemProperties(), LibItemNames.WARP_SCROLL);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!stack.hasTag())
            stack.setTag(new CompoundNBT());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        BlockPos pos = getPos(stack);
        if(world.isRemote())
            return new ActionResult<>(ActionResultType.SUCCESS, stack);

        if(pos != null ){
            if(getDimension(stack) !=  player.dimension.getId()){
                player.sendMessage(new StringTextComponent("Using this scroll from a different dimension would be a bad idea."));
                return ActionResult.resultFail(stack);
            }
            player.teleportKeepLoaded(pos.getX(), pos.getY(), pos.getZ());
            stack.shrink(1);
            return ActionResult.resultPass(stack);
        }
        if(player.isSneaking()){
            ItemStack newWarpStack = new ItemStack(ItemsRegistry.warpScroll);
            newWarpStack.setTag(new CompoundNBT());
            setTeleportTag(newWarpStack, player.getPosition(), player.dimension.getId());
            if(!player.addItemStackToInventory(newWarpStack)){
                player.sendMessage(new StringTextComponent("There is no room in your inventory."));
                return ActionResult.resultFail(stack);
            }else{
                player.sendMessage(new StringTextComponent("You record your location to your Warp Scroll."));
                stack.shrink(1);
            }
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    public void setTeleportTag(ItemStack stack, BlockPos pos, int dimension){
        stack.getTag().putInt("x", pos.getX());
        stack.getTag().putInt("y", pos.getY());
        stack.getTag().putInt("z", pos.getZ());
        stack.getTag().putInt("dim", dimension);
    }

    public BlockPos getPos(ItemStack stack){
        if(!stack.hasTag())
            return null;
        CompoundNBT tag = stack.getTag();
        return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }

    public int getDimension(ItemStack stack){
        if(!stack.hasTag())
            return -999;
        return stack.getTag().getInt("dim");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_) {
        BlockPos pos = getPos(stack);
        if(pos == null){
            tooltip.add(new StringTextComponent("No location set."));
            return;
        }
        tooltip.add(new StringTextComponent("X: " + pos.getX() + " Y: " + pos.getY() + " Z:" + pos.getZ()));
    }
}
