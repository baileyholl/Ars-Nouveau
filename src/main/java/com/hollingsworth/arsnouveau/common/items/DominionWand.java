package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
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
    public ActionResultType interactLivingEntity(ItemStack doNotUseStack, PlayerEntity playerEntity, LivingEntity target, Hand hand) {

        if(playerEntity.level.isClientSide || hand != Hand.MAIN_HAND)
            return ActionResultType.PASS;

        ItemStack stack = playerEntity.getItemInHand(hand);
        if(playerEntity.isShiftKeyDown() && target instanceof IWandable){
            ((IWandable) target).onWanded(playerEntity);
            clear(stack, playerEntity);
            return  ActionResultType.SUCCESS;
        }

        if((getPos(stack) == null || getPos(stack).equals(new BlockPos(0,0,0))) && getEntityID(stack) == -1){
            setEntityID(stack, target.getId());
            PortUtil.sendMessage(playerEntity, new TranslationTextComponent("ars_nouveau.dominion_wand.stored_entity").getString());
            return  ActionResultType.SUCCESS;
        }
        World world = playerEntity.getCommandSenderWorld();

        if(getPos(stack) != null){
            if(world.getBlockEntity(getPos(stack)) instanceof IWandable)
                ((IWandable) world.getBlockEntity(getPos(stack))).onFinishedConnectionFirst(getPos(stack),target, playerEntity);

        }
        if(target instanceof IWandable) {
            ((IWandable) target).onFinishedConnectionLast(getPos(stack), target, playerEntity);
            clear(stack, playerEntity);
        }

        return ActionResultType.SUCCESS;
    }

    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return false;
    }

    public void clear(ItemStack stack, PlayerEntity playerEntity){
        setPosTag(stack, null);
        setEntityID(stack, -1);
    }


    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if(context.getLevel().isClientSide || context.getPlayer() == null)
            return super.useOn(context);
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
        PlayerEntity playerEntity = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        

        if(playerEntity.isShiftKeyDown() && world.getBlockEntity(pos) instanceof IWandable){
            ((IWandable) world.getBlockEntity(pos)).onWanded(playerEntity);
            clear(stack, playerEntity);
            return ActionResultType.CONSUME;
        }

        if(getEntityID(stack) == - 1 && (getPos(stack) == null || getPos(stack).equals(new BlockPos(0,0,0)))){
            setPosTag(stack, pos);
            PortUtil.sendMessage(playerEntity, new TranslationTextComponent("ars_nouveau.dominion_wand.position_set").getString());
            return ActionResultType.SUCCESS;
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

    public void drawConnection(BlockPos pos1, BlockPos pos2, ServerWorld world){
        ParticleUtil.beam(pos1, pos2, world);
    }

    public void setPosTag(ItemStack stack, BlockPos pos){
        CompoundNBT tag = stack.getTag();
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
        CompoundNBT tag = stack.getTag();
        if(tag == null)
            return;
        stack.getTag().putInt("en_id", id);
    }
    public int getEntityID(ItemStack stack){
        CompoundNBT tag = stack.getTag();
        if(tag == null || !tag.contains("en_id"))
            return -1;
        return stack.getTag().getInt("en_id");
    }


    public BlockPos getPos(ItemStack stack){
        if(!stack.hasTag())
            return null;
        CompoundNBT tag = stack.getTag();
        return new BlockPos(tag.getInt("to_x"), tag.getInt("to_y"), tag.getInt("to_z"));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_) {
        BlockPos pos = getPos(stack);
        tooltip.add(getEntityID(stack) == -1 ? 
        		new TranslationTextComponent("ars_nouveau.dominion_wand.no_entity") : 
        		new TranslationTextComponent("ars_nouveau.dominion_wand.entity_stored"));
        if(pos == null){
            tooltip.add(new TranslationTextComponent("ars_nouveau.dominion_wand.no_location"));
            return;
        }

        tooltip.add(new TranslationTextComponent("ars_nouveau.dominion_wand.position_stored", getPosString(pos)));
    }

    public static String getPosString(BlockPos pos){
        return new TranslationTextComponent("ars_nouveau.position", pos.getX(), pos.getY(), pos.getZ()).getString();
    }
}
