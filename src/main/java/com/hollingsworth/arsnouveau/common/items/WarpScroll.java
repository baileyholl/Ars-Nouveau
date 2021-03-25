package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.datagen.Recipes;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
        if(!entity.getEntityWorld().isRemote && entity.getEntityWorld().getBlockState(entity.getPosition().down()).getBlock().isIn(Recipes.DECORATIVE_AN)){

            if(getPos(stack) != null
                    && getDimension(stack).equals(entity.getEntityWorld().getDimensionKey().getRegistryName().toString())
                    && ManaUtil.hasManaNearby(entity.getPosition(), entity.getEntityWorld(), 10, 9000)
                    && BlockRegistry.PORTAL_BLOCK.trySpawnPortal(entity.getEntityWorld(), entity.getPosition(), getPos(stack), getDimension(stack))
                    && ManaUtil.takeManaNearby(entity.getPosition(), entity.getEntityWorld(), 10, 9000) != null){
                BlockPos pos = entity.getPosition();
                ServerWorld world = (ServerWorld) entity.getEntityWorld();
                world.spawnParticle(ParticleTypes.PORTAL, pos.getX(),  pos.getY() + 1,  pos.getZ(),
                        10,(world.rand.nextDouble() - 0.5D) * 2.0D, -world.rand.nextDouble(), (world.rand.nextDouble() - 0.5D) * 2.0D, 0.1f);
                world.playSound(null, pos, SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                stack.shrink(1);
                return true;
            }

        }

        return false;
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        BlockPos pos = getPos(stack);
        if(hand == Hand.OFF_HAND && player.getHeldItemMainhand().getItem() instanceof SpellBook)
            return new ActionResult<>(ActionResultType.SUCCESS, stack);

        if(world.isRemote())
            return new ActionResult<>(ActionResultType.SUCCESS, stack);

        if(pos != null ){
            if(getDimension(stack) == null || !getDimension(stack).equals(player.getEntityWorld().getDimensionKey().getRegistryName().toString())){
                player.sendMessage(new TranslationTextComponent("ars_nouveau.warp_scroll.wrong_dim"), Util.DUMMY_UUID);
                return ActionResult.resultFail(stack);
            }
            player.teleportKeepLoaded(pos.getX() +0.5, pos.getY(), pos.getZ() +0.5);
            stack.shrink(1);
            return ActionResult.resultPass(stack);
        }
        if(player.isSneaking()){
            ItemStack newWarpStack = new ItemStack(ItemsRegistry.warpScroll);
            newWarpStack.setTag(new CompoundNBT());
            setTeleportTag(newWarpStack, player.getPosition(), player.getEntityWorld().getDimensionKey().getRegistryName().toString());
            if(!player.addItemStackToInventory(newWarpStack)){
                player.sendMessage(new TranslationTextComponent("ars_nouveau.warp_scroll.inv_full"), Util.DUMMY_UUID);
                return ActionResult.resultFail(stack);
            }else{
                player.sendMessage(new TranslationTextComponent("ars_nouveau.warp_scroll.recorded"), Util.DUMMY_UUID);
                stack.shrink(1);
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

    public static BlockPos getPos(ItemStack stack){
        if(!stack.hasTag())
            return null;
        CompoundNBT tag = stack.getTag();
        return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }

    public String getDimension(ItemStack stack){
        if(!stack.hasTag())
            return null;
        return stack.getTag().getString("dim_2"); //dim refers to the old int value on old scrolls, no crash please!
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_) {
        BlockPos pos = getPos(stack);
        if(pos == null){
            tooltip.add(new TranslationTextComponent("ars_nouveau.warp_scroll.no_location"));
            return;
        }
        tooltip.add(new TranslationTextComponent("ars_nouveau.position", pos.getX(), pos.getY(), pos.getZ()));
    }
}
