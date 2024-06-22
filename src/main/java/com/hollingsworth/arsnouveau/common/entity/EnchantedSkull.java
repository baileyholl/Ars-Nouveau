package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EnchantedSkull extends EnchantedFallingBlock {
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

    @Nullable
    @Override
    public ItemEntity spawnAtLocation(ItemStack pStack) {
        if (pStack.getItem() instanceof PlayerHeadItem)
            pStack.setTag(blockData);
        return this.spawnAtLocation(pStack, 0.0F);
    }

    public ItemStack getStack() {
        Item item = getBlockState().getBlock().asItem();
        ItemStack stack = item.getDefaultInstance();
        if (item instanceof PlayerHeadItem){
            stack.setTag(this.blockData);
        }
        return stack;
    }

    /**
     * Called by the server when constructing the spawn packet.
     * Data should be added to the provided stream.
     *
     * @param buffer The packet data stream
     */
    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(Block.getId(blockState));
        buffer.writeNbt(blockData);
    }

    /**
     * Called by the client when it receives a Entity spawn packet.
     * Data should be read out of the stream in the same way as it was written.
     *
     * @param additionalData The packet data stream
     */
    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        blockState = Block.stateById(additionalData.readInt());
        blockData = additionalData.readNbt();
    }

}
