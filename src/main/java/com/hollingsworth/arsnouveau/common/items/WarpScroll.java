package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.nbt.AbstractData;
import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nullable;
import java.util.List;

public class WarpScroll extends ModItem{
    public WarpScroll() {
        super(ItemsRegistry.defaultItemProperties());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!stack.hasTag())
            stack.setTag(new CompoundTag());
    }


    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        //entity.getCommandSenderWorld().getBlockState(entity.blockPosition().below()).getBlock().is(Recipes.DECORATIVE_AN)
        if(!entity.getCommandSenderWorld().isClientSide){
            String displayName = stack.hasCustomHoverName() ? stack.getHoverName().getString() : "";
            if(getPos(stack) != BlockPos.ZERO
                    && getDimension(stack).equals(entity.getCommandSenderWorld().dimension().location().toString())
                    && SourceUtil.hasSourceNearby(entity.blockPosition(), entity.getCommandSenderWorld(), 10, 9000)
                    && (BlockRegistry.PORTAL_BLOCK.trySpawnPortal(entity.getCommandSenderWorld(), entity.blockPosition(), getPos(stack), getDimension(stack), getRotationVector(stack), displayName)
                    || BlockRegistry.PORTAL_BLOCK.trySpawnHoriztonalPortal(entity.getCommandSenderWorld(), entity.blockPosition(), getPos(stack), getDimension(stack), getRotationVector(stack), displayName))
                    && SourceUtil.takeSourceNearbyWithParticles(entity.blockPosition(), entity.getCommandSenderWorld(), 10, 9000) != null){
                BlockPos pos = entity.blockPosition();
                ServerLevel world = (ServerLevel) entity.getCommandSenderWorld();
                world.sendParticles(ParticleTypes.PORTAL, pos.getX(),  pos.getY() + 1,  pos.getZ(),
                        10,(world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
                world.playSound(null, pos, SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.NEUTRAL, 1.0f, 1.0f);
                stack.shrink(1);
                return true;
            }

        }

        return false;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockPos pos = getPos(stack);
        if(hand == InteractionHand.OFF_HAND)
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

        if(world.isClientSide())
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

        if(!pos.equals(BlockPos.ZERO) ){
            if (getDimension(stack) == null || !getDimension(stack).equals(player.getCommandSenderWorld().dimension().location().toString())) {
                player.sendSystemMessage(Component.translatable("ars_nouveau.warp_scroll.wrong_dim"));
                return InteractionResultHolder.fail(stack);
            }
            player.teleportToWithTicket(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            Vec2 rotation = getRotationVector(stack);
            player.setXRot(rotation.x);
            player.setYRot(rotation.y);
            stack.shrink(1);
            return InteractionResultHolder.pass(stack);
        }
        if(player.isShiftKeyDown()){
            ItemStack newWarpStack = new ItemStack(ItemsRegistry.WARP_SCROLL.get());
            newWarpStack.setTag(new CompoundTag());
            setTeleportTag(newWarpStack, player.blockPosition(), player.getCommandSenderWorld().dimension().location().toString());
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
                player.sendSystemMessage(Component.translatable("ars_nouveau.warp_scroll.inv_full"));
                return InteractionResultHolder.fail(stack);
            }else{
                player.sendSystemMessage(Component.translatable("ars_nouveau.warp_scroll.recorded"));
            }
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    public void setTeleportTag(ItemStack stack, BlockPos pos, String dimension){
        stack.getTag().putInt("x", pos.getX());
        stack.getTag().putInt("y", pos.getY());
        stack.getTag().putInt("z", pos.getZ());
        stack.getTag().putString("dim_2", dimension); //dim refers to the old int value on old scrolls, no crash please!
    }

    public static Vec2 getRotationVector(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        return new Vec2(tag.getFloat("xRot"), tag.getFloat("yRot"));
    }

    public static void setRotationVector(ItemStack stack, Vec2 vector2f){
        CompoundTag tag = stack.getOrCreateTag();
        tag.putFloat("xRot", vector2f.x);
        tag.putFloat("yRot", vector2f.y);
    }

    public static BlockPos getPos(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }

    public String getDimension(ItemStack stack){
        return stack.getOrCreateTag().getString("dim_2"); //dim refers to the old int value on old scrolls, no crash please!
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag p_77624_4_) {
        BlockPos pos = getPos(stack);
        if(pos.equals(BlockPos.ZERO)){
            tooltip.add(Component.translatable("ars_nouveau.warp_scroll.no_location"));
            return;
        }
        tooltip.add(Component.translatable("ars_nouveau.position", pos.getX(), pos.getY(), pos.getZ()));
    }

    public static class WarpScrollData extends ItemstackData {
        public @Nullable BlockPos pos;
        public String dimension;
        public Vec2 rotation;

        public WarpScrollData(ItemStack stack) {
            super(stack);
        }

        public boolean isValid(){
            return pos != null && dimension != null && rotation != null;
        }

        @Override
        public void readFromNBT(CompoundTag tag1) {
            pos = tag1.contains("x") ? new BlockPos(tag1.getInt("x"), tag1.getInt("y"), tag1.getInt("z")) : null;
            dimension = tag1.getString("dim");
            rotation = new Vec2(tag1.getFloat("xRot"), tag1.getFloat("yRot"));
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            if(pos != null){
                tag.putInt("x", pos.getX());
                tag.putInt("y", pos.getY());
                tag.putInt("z", pos.getZ());
            }
            tag.putString("dim", dimension);
            tag.putFloat("xRot", rotation.x);
            tag.putFloat("yRot", rotation.y);
        }

        @Override
        public String getTagString() {
            return "an_warp_scroll";
        }
    }
}
