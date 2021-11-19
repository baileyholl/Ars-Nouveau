package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        //entity.getCommandSenderWorld().getBlockState(entity.blockPosition().below()).getBlock().is(Recipes.DECORATIVE_AN)
        if(!entity.getCommandSenderWorld().isClientSide){
            String displayName = stack.hasCustomHoverName() ? stack.getHoverName().getString() : "";
            if(getPos(stack) != BlockPos.ZERO
                    && getDimension(stack).equals(entity.getCommandSenderWorld().dimension().getRegistryName().toString())
                    && ManaUtil.hasManaNearby(entity.blockPosition(), entity.getCommandSenderWorld(), 10, 9000)
                    && (BlockRegistry.PORTAL_BLOCK.trySpawnPortal(entity.getCommandSenderWorld(), entity.blockPosition(), getPos(stack), getDimension(stack), getRotationVector(stack), displayName)
                    || BlockRegistry.PORTAL_BLOCK.trySpawnHoriztonalPortal(entity.getCommandSenderWorld(), entity.blockPosition(), getPos(stack), getDimension(stack), getRotationVector(stack), displayName))
                    && ManaUtil.takeManaNearbyWithParticles(entity.blockPosition(), entity.getCommandSenderWorld(), 10, 9000) != null){
                BlockPos pos = entity.blockPosition();
                ServerWorld world = (ServerWorld) entity.getCommandSenderWorld();
                world.sendParticles(ParticleTypes.PORTAL, pos.getX(),  pos.getY() + 1,  pos.getZ(),
                        10,(world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
                world.playSound(null, pos, SoundEvents.ILLUSIONER_CAST_SPELL, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                stack.shrink(1);
                System.out.println("deleted");
                return true;
            }

        }

        return false;
    }
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockPos pos = getPos(stack);
        if(hand == Hand.OFF_HAND)
            return new ActionResult<>(ActionResultType.SUCCESS, stack);

        if(world.isClientSide())
            return new ActionResult<>(ActionResultType.SUCCESS, stack);

        if(!pos.equals(BlockPos.ZERO) ){
            if(getDimension(stack) == null || !getDimension(stack).equals(player.getCommandSenderWorld().dimension().getRegistryName().toString())){
                player.sendMessage(new TranslationTextComponent("ars_nouveau.warp_scroll.wrong_dim"), Util.NIL_UUID);
                return ActionResult.fail(stack);
            }
            player.teleportToWithTicket(pos.getX() +0.5, pos.getY(), pos.getZ() +0.5);
            Vector2f rotation = getRotationVector(stack);
            player.xRot = rotation.x;
            player.yRot = rotation.y;
            stack.shrink(1);
            return ActionResult.pass(stack);
        }
        if(player.isShiftKeyDown()){
            ItemStack newWarpStack = new ItemStack(ItemsRegistry.warpScroll);
            newWarpStack.setTag(new CompoundNBT());
            setTeleportTag(newWarpStack, player.blockPosition(), player.getCommandSenderWorld().dimension().getRegistryName().toString());
            setRotationVector(newWarpStack, player.getRotationVector());
            boolean didAdd;
            if(stack.getCount() == 1){
                stack = newWarpStack;
                didAdd = true;
            }else{
                didAdd = player.addItem(newWarpStack);
                if(didAdd)
                    stack.shrink(1);
            }
            if(!didAdd){
                player.sendMessage(new TranslationTextComponent("ars_nouveau.warp_scroll.inv_full"), Util.NIL_UUID);
                return ActionResult.fail(stack);
            }else{
                player.sendMessage(new TranslationTextComponent("ars_nouveau.warp_scroll.recorded"), Util.NIL_UUID);
            }
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    public void setTeleportTag(ItemStack stack, BlockPos pos, String dimension){
        stack.getTag().putInt("x", pos.getX());
        stack.getTag().putInt("y", pos.getY());
        stack.getTag().putInt("z", pos.getZ());
        stack.getTag().putString("dim_2", dimension); //dim refers to the old int value on old scrolls, no crash please!
    }

    public static Vector2f getRotationVector(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        return new Vector2f(tag.getFloat("xRot"), tag.getFloat("yRot"));
    }

    public static void setRotationVector(ItemStack stack, Vector2f vector2f){
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putFloat("xRot", vector2f.x);
        tag.putFloat("yRot", vector2f.y);
    }

    public static BlockPos getPos(ItemStack stack){
        CompoundNBT tag = stack.getOrCreateTag();
        return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }

    public String getDimension(ItemStack stack){
        return stack.getOrCreateTag().getString("dim_2"); //dim refers to the old int value on old scrolls, no crash please!
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_) {
        BlockPos pos = getPos(stack);
        if(pos.equals(BlockPos.ZERO)){
            tooltip.add(new TranslationTextComponent("ars_nouveau.warp_scroll.no_location"));
            return;
        }
        tooltip.add(new TranslationTextComponent("ars_nouveau.position", pos.getX(), pos.getY(), pos.getZ()));
    }
}
