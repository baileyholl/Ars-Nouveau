package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.List;

public class PotionJarTile extends ModdedTile implements ITooltipProvider, IWandable {

    public boolean isLocked;
    private PotionData data = new PotionData();

    public PotionJarTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public PotionJarTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.POTION_JAR_TYPE, pos, state);
    }

    @Override
    public void onWanded(Player playerEntity) {
        if (!isLocked) {
            this.isLocked = true;
            playerEntity.sendSystemMessage(Component.translatable("ars_nouveau.locked"));
        } else {
            this.isLocked = false;
            playerEntity.sendSystemMessage(Component.translatable("ars_nouveau.unlocked"));
        }
        updateBlock();
    }

    @Override
    public boolean updateBlock() {
        BlockState state = level.getBlockState(worldPosition);
        int fillState = 0;
        if (this.getAmount() > 0 && this.getAmount() < 1000)
            fillState = 1;
        else if (this.getAmount() != 0) {
            fillState = (this.getAmount() / 1000) + 1;
        }
        level.setBlock(worldPosition, state.setValue(SourceJar.fill, fillState), 3);
        return super.updateBlock();
    }

    public @Nonnull PotionData getData() {
        return data;
    }

    public int getColor() {
        return this.data.potion == null ? 16253176 : PotionUtils.getColor(this.data.fullEffects());
    }

    public boolean canAccept(PotionData otherData){
        return (!this.isLocked && this.getAmount() <= 0) || (otherData.amount <= (this.getMaxFill() - this.getAmount()) && otherData.areSameEffects(this.data));
    }

    public void add(PotionData other){
        if(this.getAmount() == 0){
            this.data = other;
        }else{
            this.data.amount = Math.max(this.getAmount() + other.amount, this.getMaxFill());
        }
        updateBlock();
    }

    public void remove(int amount){
        this.data.amount = Math.max(this.data.amount - amount, 0);
        if(this.data.amount == 0 && !isLocked){
            this.data = new PotionData();
        }
        updateBlock();
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (this.data.potion != Potions.EMPTY) {
            ItemStack potionStack = data.asPotionStack();
            tooltip.add(potionStack.getHoverName());
            PotionUtils.addPotionTooltip(potionStack, tooltip, 1.0F);
        }
        tooltip.add(Component.translatable("ars_nouveau.source_jar.fullness", (getAmount() * 100) / this.getMaxFill()));
        if (isLocked)
            tooltip.add(Component.translatable("ars_nouveau.locked"));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("potionData"))
            this.data = PotionData.fromTag(tag.getCompound("potionData"));
        this.isLocked = tag.getBoolean("locked");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putInt("amount", this.getAmount());
        tag.put("potionData", this.data.toTag());
    }

    public int getMaxFill() {
        return 10000;
    }

    public int getAmount() {
        return data.amount;
    }

    public boolean canTakeAmount(int amount){
        return this.getAmount() + amount <= this.getMaxFill();
    }

    public void setAmount(int amount) {
        this.data.amount = amount;
        if (this.getAmount() <= 0 && !this.isLocked) {
            this.data = new PotionData();
        }
        updateBlock();
    }
}

