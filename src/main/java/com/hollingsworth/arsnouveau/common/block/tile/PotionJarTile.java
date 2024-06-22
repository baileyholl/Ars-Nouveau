package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PotionJarTile extends ModdedTile implements ITooltipProvider, IWandable {

    public boolean isLocked;
    private PotionData data = new PotionData();
    int currentFill;

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

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition),  level.getBlockState(worldPosition), 8);
    }

    public@NotNull PotionData getData() {
        return data;
    }

    public int getColor() {
        return this.data.getPotion() == null ? 16253176 : PotionUtils.getColor(this.data.fullEffects());
    }

    public boolean canAccept(PotionData otherData, int amount){
        if(otherData == null || otherData.getPotion() == Potions.EMPTY)
            return false;
        return (!this.isLocked && this.getAmount() <= 0) || (amount <= (this.getMaxFill() - this.getAmount()) && otherData.areSameEffects(this.data));
    }

    public void add(PotionData other, int amount){
        if(this.currentFill == 0){
            if(!this.data.equals(other) || (this.data.getPotion() == Potions.EMPTY)) {
                this.data = other;
            }
            currentFill += amount;
        }else{
            currentFill = Math.min(this.getAmount() + amount, this.getMaxFill());
        }
        currentFill = Math.min(currentFill, this.getMaxFill());
        updateBlock();
    }

    public void remove(int amount){
        currentFill = Math.max(currentFill - amount, 0);
        if(currentFill == 0 && !isLocked){
            this.data = new PotionData();
        }
        updateBlock();
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        data.appendHoverText(tooltip);
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
        this.currentFill = tag.getInt("currentFill");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("potionData", this.data.toTag());
        tag.putBoolean("locked", this.isLocked);
        tag.putInt("currentFill", this.currentFill);

        // Include a sorted list of potion names so quests can check the jar's contents
        Set<Potion> potionSet = this.data.getIncludedPotions();
        List<String> potionNames = new ArrayList<>(potionSet.stream().map(potion -> ForgeRegistries.POTIONS.getKey(potion).toString()).toList());
        potionNames.sort(String::compareTo);
        tag.putString("potionNames", String.join(",", potionNames));

    }

    public int getMaxFill() {
        return 10000;
    }

    public int getAmount() {
        return currentFill;
    }
}

