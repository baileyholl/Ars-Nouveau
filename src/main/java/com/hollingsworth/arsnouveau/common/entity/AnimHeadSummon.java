package com.hollingsworth.arsnouveau.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class AnimHeadSummon extends AnimBlockSummon {

    CompoundTag head_data = new CompoundTag();

    public AnimHeadSummon(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public AnimHeadSummon(Level pLevel, BlockState state, CompoundTag head_data) {
        this(ModEntities.ANIMATED_HEAD.get(), pLevel);
        this.blockState = state;
        this.head_data = head_data;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ANIMATED_HEAD.get();
    }

    public void returnToFallingBlock(BlockState blockState) {
        EnchantedFallingBlock fallingBlock = new EnchantedSkull(level, blockPosition(), blockState);
        fallingBlock.setOwner(this.getOwner());
        fallingBlock.setDeltaMovement(this.getDeltaMovement());
        if (blockState.getBlock() == Blocks.PLAYER_HEAD) {
            fallingBlock.blockData = head_data;
        }
        level.addFreshEntity(fallingBlock);
    }

    public void setHeadData(CompoundTag data) {
        this.head_data = data;
    }

    public CompoundTag getHead_data() {
        return head_data;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new EnchantedSkull.SkullEntityPacket(this, Block.getId(this.getBlockState()), this.head_data);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        if (pPacket instanceof EnchantedSkull.SkullEntityPacket skullEntityPacket){
            this.head_data = skullEntityPacket.getTag();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("head_data", head_data);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        head_data = pCompound.getCompound("head_data");
    }

    public ItemStack getStack() {
        Item item = getBlockState().getBlock().asItem();
        ItemStack stack = item.getDefaultInstance();
        if (item instanceof PlayerHeadItem) {
            stack.setTag(this.head_data);
        }
        return stack;
    }
}
