package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import javax.annotation.Nullable;
import java.util.List;

public class DominionWand extends ModItem{
    public DominionWand() {
        super(LibItemNames.DOMINION_WAND);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(stack, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(!stack.hasTag())
            stack.setTag(new CompoundTag());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack doNotUseStack, Player playerEntity, LivingEntity target, InteractionHand hand) {

        if(playerEntity.level.isClientSide || hand != InteractionHand.MAIN_HAND)
            return InteractionResult.PASS;

        ItemStack stack = playerEntity.getItemInHand(hand);
        if(playerEntity.isShiftKeyDown() && target instanceof IWandable){
            ((IWandable) target).onWanded(playerEntity);
            clear(stack, playerEntity);
            return  InteractionResult.SUCCESS;
        }

        if((getPos(stack) == null || getPos(stack).equals(new BlockPos(0,0,0))) && getEntityID(stack) == -1){
            setEntityID(stack, target.getId());
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.dominion_wand.stored_entity"));
            return  InteractionResult.SUCCESS;
        }
        Level world = playerEntity.getCommandSenderWorld();

        if(getPos(stack) != null){
            if(world.getBlockEntity(getPos(stack)) instanceof IWandable)
                ((IWandable) world.getBlockEntity(getPos(stack))).onFinishedConnectionFirst(getPos(stack),target, playerEntity);

        }
        if(target instanceof IWandable) {
            ((IWandable) target).onFinishedConnectionLast(getPos(stack), target, playerEntity);
            clear(stack, playerEntity);
        }

        if (playerEntity.isShiftKeyDown() && target instanceof IDecoratable coolBoy){
           coolBoy.setCosmeticItem(ItemStack.EMPTY);
        }

        return InteractionResult.SUCCESS;
    }

    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return false;
    }

    public void clear(ItemStack stack, Player playerEntity){
        setPosTag(stack, null);
        setEntityID(stack, -1);
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().isClientSide || context.getPlayer() == null)
            return super.useOn(context);
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        Player playerEntity = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        

        if(playerEntity.isShiftKeyDown() && world.getBlockEntity(pos) instanceof IWandable){
            ((IWandable) world.getBlockEntity(pos)).onWanded(playerEntity);
            clear(stack, playerEntity);
            return InteractionResult.CONSUME;
        }

        if(getEntityID(stack) == - 1 && (getPos(stack) == null || getPos(stack).equals(new BlockPos(0,0,0)))){
            setPosTag(stack, pos);
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.dominion_wand.position_set"));
            return InteractionResult.SUCCESS;
        }

        if(getPos(stack) != null){
            if(world.getBlockEntity(getPos(stack)) instanceof IWandable) {
                ((IWandable) world.getBlockEntity(getPos(stack))).onFinishedConnectionFirst(pos, (LivingEntity) world.getEntity(getEntityID(stack)), playerEntity);
            }
        }
        if(world.getBlockEntity(pos) instanceof IWandable)
            ((IWandable) world.getBlockEntity(pos)).onFinishedConnectionLast(getPos(stack), (LivingEntity) world.getEntity(getEntityID(stack)), playerEntity);

        if(getEntityID(stack) != -1 && world.getEntity(getEntityID(stack)) instanceof  IWandable){
            ((IWandable)world.getEntity(getEntityID(stack))).onFinishedConnectionFirst(pos, null, playerEntity);
        }


        clear(stack, playerEntity);
        return super.useOn(context);
    }

    public void drawConnection(BlockPos pos1, BlockPos pos2, ServerLevel world){
        ParticleUtil.beam(pos1, pos2, world);
    }

    public void setPosTag(ItemStack stack, BlockPos pos){
        CompoundTag tag = stack.getTag();
        if(pos == null && tag != null && tag.contains("to_x")){
            tag.remove("to_x");
            tag.remove("to_y");
            tag.remove("to_z");
        }else if(pos != null && tag != null){
            stack.getTag().putInt("to_x", pos.getX());
            stack.getTag().putInt("to_y", pos.getY());
            stack.getTag().putInt( "to_z", pos.getZ());
        }
    }

    public void setEntityID(ItemStack stack, int id){
        CompoundTag tag = stack.getTag();
        if(tag == null)
            return;
        stack.getTag().putInt("en_id", id);
    }
    public int getEntityID(ItemStack stack){
        CompoundTag tag = stack.getTag();
        if(tag == null || !tag.contains("en_id"))
            return -1;
        return stack.getTag().getInt("en_id");
    }


    public BlockPos getPos(ItemStack stack){
        if(!stack.hasTag())
            return null;
        CompoundTag tag = stack.getTag();
        return new BlockPos(tag.getInt("to_x"), tag.getInt("to_y"), tag.getInt("to_z"));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag p_77624_4_) {
        BlockPos pos = getPos(stack);
        tooltip.add(getEntityID(stack) == -1 ? 
        		Component.translatable("ars_nouveau.dominion_wand.no_entity") : 
        		Component.translatable("ars_nouveau.dominion_wand.entity_stored"));
        if(pos == null){
            tooltip.add(Component.translatable("ars_nouveau.dominion_wand.no_location"));
            return;
        }

        tooltip.add(Component.translatable("ars_nouveau.dominion_wand.position_stored", getPosString(pos)));
    }

    public static String getPosString(BlockPos pos){
        return Component.translatable("ars_nouveau.position", pos.getX(), pos.getY(), pos.getZ()).getString();
    }
}
