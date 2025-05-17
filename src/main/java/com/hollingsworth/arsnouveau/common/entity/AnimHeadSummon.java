package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AnimHeadSummon extends AnimBlockSummon  {


    public AnimHeadSummon(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public AnimHeadSummon(Level pLevel, BlockState state, CompoundTag head_data) {
        super(ModEntities.ANIMATED_HEAD.get(), pLevel, head_data);
        this.blockState = state;
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
        fallingBlock.blockData = head_data;
        level.addFreshEntity(fallingBlock);
    }

    public ItemStack getStack() {
        Item item = getBlockState().getBlock().asItem();
        ItemStack stack = item.getDefaultInstance();
        if (item instanceof PlayerHeadItem && head_data != null) {
            ResolvableProfile.CODEC
                    .parse(NbtOps.INSTANCE, head_data.get("profile"))
                    .resultOrPartial().ifPresent(profile -> stack.set(DataComponents.PROFILE, profile));
        }
        return stack;
    }

}
