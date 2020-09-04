package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.AbstractManaTile;
import com.hollingsworth.arsnouveau.common.block.tile.ArcaneRelayTile;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class DominionWand extends ModItem{
    public DominionWand() {
        super(LibItemNames.DOMINION_WAND);
    }

    @Override
    public void inventoryTick(ItemStack stack, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(stack, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(!stack.hasTag())
            stack.setTag(new CompoundNBT());
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if(context.getWorld().isRemote)
            return super.onItemUse(context);
        BlockPos pos = context.getPos();
        World world = context.getWorld();
        PlayerEntity playerEntity = context.getPlayer();
        ItemStack stack = context.getItem();
        AbstractManaTile manaTile = world.getTileEntity(pos) instanceof AbstractManaTile ? (AbstractManaTile) world.getTileEntity(pos) : null;
        if(playerEntity.isSneaking() && manaTile != null && manaTile instanceof ArcaneRelayTile){
            ((ArcaneRelayTile) manaTile).clearPos();
            playerEntity.sendMessage(new StringTextComponent("Connections cleared."), Util.DUMMY_UUID);
            return super.onItemUse(context);
        }

        if(pos.equals(getPos(stack))){
            this.setPosTag(stack,null, 0);
            playerEntity.sendMessage(new StringTextComponent("Cleared link."), Util.DUMMY_UUID);
            return super.onItemUse(context);
        }

        if(manaTile == null){
            if(getPos(stack) != null) {
                this.setPosTag(stack,null, 0);
                playerEntity.sendMessage(new StringTextComponent("Cleared link."), Util.DUMMY_UUID);
            }
            return super.onItemUse(context);
        }
        if(getPos(stack) == null){
            setPosTag(stack, pos, 0);
            playerEntity.sendMessage(new StringTextComponent("Stored position."), Util.DUMMY_UUID);
            return super.onItemUse(context);
        }
        // If we are going FROM a non-relay mana tile to a relay. (Jar to relay)
        if(manaTile instanceof ArcaneRelayTile && world.getTileEntity(getPos(stack)) instanceof AbstractManaTile && !(world.getTileEntity(getPos(stack)) instanceof ArcaneRelayTile)){
            if(((ArcaneRelayTile) manaTile).setTakeFrom(getPos(stack))){
                playerEntity.sendMessage(new StringTextComponent("Relay set to take from " + getPosString(getPos(stack))),Util.DUMMY_UUID);
                drawConnection(getPos(stack),pos, (ServerWorld) world);
                setPosTag(stack, null, 0);
            }else{
                playerEntity.sendMessage(new StringTextComponent("Too far away."), Util.DUMMY_UUID);
            }
            return super.onItemUse(context);
        }
        // From relay to any other mana tile
        if(world.getTileEntity(getPos(stack)) instanceof ArcaneRelayTile){
            if(((ArcaneRelayTile) world.getTileEntity(getPos(stack))).setSendTo(pos)){
                playerEntity.sendMessage(new StringTextComponent("Relay set to send to " + getPosString(getPos(stack))), Util.DUMMY_UUID);
                drawConnection(getPos(stack),pos, (ServerWorld) world);
                setPosTag(stack, null, 0);
            }else{
                playerEntity.sendMessage(new StringTextComponent("Too far away."), Util.DUMMY_UUID);
            }
            return super.onItemUse(context);
        }
        return super.onItemUse(context);
    }

    public void drawConnection(BlockPos pos1, BlockPos pos2, ServerWorld world){
        ParticleUtil.beam(pos1, pos2, world);
    }

    public void setPosTag(ItemStack stack, BlockPos pos, int dim){
        CompoundNBT tag = stack.getTag();
        if(pos == null && tag != null && tag.contains("to_x")){
            tag.remove("to_x");
            tag.remove("to_y");
            tag.remove("to_z");
            tag.remove("to_dim");
        }else if(pos != null && tag != null){
            stack.getTag().putInt("to_x", pos.getX());
            stack.getTag().putInt("to_y", pos.getY());
            stack.getTag().putInt( "to_z", pos.getZ());

        }
    }



    public BlockPos getPos(ItemStack stack){
        if(!stack.hasTag())
            return null;
        CompoundNBT tag = stack.getTag();
        return new BlockPos(tag.getInt("to_x"), tag.getInt("to_y"), tag.getInt("to_z"));
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
        tooltip.add(new StringTextComponent("Stored: " + getPosString(pos)));
    }

    public String getPosString(BlockPos pos){
        return "X: " + pos.getX() + " Y: " + pos.getY() + " Z:" + pos.getZ();
    }
}
