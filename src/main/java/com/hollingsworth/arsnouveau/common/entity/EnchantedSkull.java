package com.hollingsworth.arsnouveau.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EnchantedSkull extends EnchantedFallingBlock{
    public EnchantedSkull(EntityType<? extends ColoredProjectile> p_31950_, Level p_31951_) {
        super(p_31950_, p_31951_);
    }

    public EnchantedSkull(Level world, double v, double y, double v1, BlockState blockState) {
        super(world, v, y, v1, blockState);
    }

    public EnchantedSkull(Level world, BlockPos pos, BlockState blockState) {
        super(world, pos, blockState);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENCHANTED_HEAD_BLOCK.get();
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new SkullEntityPacket(this, Block.getId(this.getBlockState()), this.blockData);
    }

    @Nullable
    @Override
    public ItemEntity spawnAtLocation(ItemStack pStack) {
        if (pStack.getItem() instanceof PlayerHeadItem)
            pStack.setTag(blockData);
        return this.spawnAtLocation(pStack, 0.0F);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        if (pPacket instanceof SkullEntityPacket skullEntityPacket){
            this.blockData = skullEntityPacket.getTag();
        }
    }

    public ItemStack getStack() {
        Item item = getBlockState().getBlock().asItem();
        ItemStack stack = item.getDefaultInstance();
        if (item instanceof PlayerHeadItem){
            stack.setTag(this.blockData);
        }
        return stack;
    }

    public static class SkullEntityPacket extends ClientboundAddEntityPacket {

        private CompoundTag compound = new CompoundTag();

        public CompoundTag getTag(){return compound;}
        public SkullEntityPacket(Entity pEntity, int pData) {
            super(pEntity, pData);
        }

        public SkullEntityPacket(FriendlyByteBuf pBuffer){
            super(pBuffer);
            this.compound = pBuffer.readNbt();
        }

        public SkullEntityPacket(Entity pEntity, int pData, CompoundTag tag) {
            this(pEntity, pData);
            this.compound = tag;
        }

        @Override
        public void write(FriendlyByteBuf pBuffer) {
            super.write(pBuffer);
            pBuffer.writeNbt(this.compound);
        }


    }
}
