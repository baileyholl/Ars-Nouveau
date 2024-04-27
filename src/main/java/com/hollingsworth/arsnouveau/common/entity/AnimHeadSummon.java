package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public class AnimHeadSummon extends AnimBlockSummon implements IEntityAdditionalSpawnData {

    public CompoundTag head_data = new CompoundTag();

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
        if(level.isClientSide || !this.dropItem || blockState == null){
            return;
        }
        EnchantedFallingBlock fallingBlock = new EnchantedSkull(level, blockPosition(), blockState);
        fallingBlock.setOwner(this.getOwner());
        fallingBlock.setDeltaMovement(this.getDeltaMovement());
        if (blockState.getBlock() == Blocks.PLAYER_HEAD) {
            fallingBlock.blockData = head_data;
        }
        level.addFreshEntity(fallingBlock);
    }


    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
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

    public static CompoundTag getHeadTagFromName(String playerName){
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("SkullOwner", playerName);
        return compoundtag;
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
        buffer.writeNbt(head_data);
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
        head_data = additionalData.readNbt();
    }
}
