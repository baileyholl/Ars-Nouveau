package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.common.items.data.PotionJarData;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.common.util.PotionUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PotionJarTile extends ModdedTile implements ITooltipProvider, IWandable {

    public boolean isLocked;
    private PotionContents data = PotionContents.EMPTY;
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
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 8);
    }

    public @NotNull PotionContents getData() {
        return data;
    }

    public int getColor() {
        return this.data.equals(PotionContents.EMPTY) ? 16253176 : this.data.getColor();
    }

    public boolean canAccept(PotionContents otherData, int amount) {
        if (otherData == null || !validContentTypeForJar(otherData))
            return false;
        return (!this.isLocked && this.getAmount() <= 0) || (amount <= (this.getMaxFill() - this.getAmount()) && PotionUtil.arePotionContentsEqual(otherData, this.data));
    }

    public boolean validContentTypeForJar(PotionContents otherData) {
        return !otherData.is(Potions.WATER) && !otherData.is(Potions.MUNDANE);
    }

    public void add(PotionContents other, int amount) {
        if (this.currentFill == 0) {
            if (!this.data.equals(other) || (this.data.equals(PotionContents.EMPTY))) {
                this.data = other;
            }
            currentFill += amount;
        } else {
            currentFill = Math.min(this.getAmount() + amount, this.getMaxFill());
        }
        currentFill = Math.min(currentFill, this.getMaxFill());
        updateBlock();
    }

    public void remove(int amount) {
        currentFill = Math.max(currentFill - amount, 0);
        if (currentFill == 0 && !isLocked) {
            this.data = PotionContents.EMPTY;
        }
        updateBlock();
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (!data.equals(PotionContents.EMPTY)) {
            ItemStack potion = new ItemStack(Items.POTION);
            potion.set(DataComponents.POTION_CONTENTS, data);
            tooltip.add(Component.translatable(potion.getDescriptionId()));
        }
        PotionContents.addPotionTooltip(data.getAllEffects(), tooltip::add, 1.0F, 20.0f);
        tooltip.add(Component.translatable("ars_nouveau.source_jar.fullness", (getAmount() * 100) / this.getMaxFill()));
        if (isLocked)
            tooltip.add(Component.translatable("ars_nouveau.locked"));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        if (tag.contains("potionData"))
            this.data = ANCodecs.decode(pRegistries, PotionContents.CODEC, tag.get("potionData"));
        this.isLocked = tag.getBoolean("locked");
        this.currentFill = tag.getInt("currentFill");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.put("potionData", ANCodecs.encode(pRegistries, PotionContents.CODEC, this.data));
        tag.putBoolean("locked", this.isLocked);
        tag.putInt("currentFill", this.currentFill);

        //TODO: restore string list of serialized potions

        // Include a sorted list of potion names so quests can check the jar's contents
//        Set<PotionContents> potionSet = this.data.getIncludedPotions();
//        List<String> potionNames = new ArrayList<>(potionSet.stream().map(potion -> BuiltInRegistries.POTION.getKey(potion).toString()).toList());
//        potionNames.sort(String::compareTo);
//        tag.putString("potionNames", String.join(",", potionNames));

    }

    public int getMaxFill() {
        return 10000;
    }

    public int getAmount() {
        return currentFill;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput pComponentInput) {
        super.applyImplicitComponents(pComponentInput);
        var jarContents = pComponentInput.getOrDefault(DataComponentRegistry.POTION_JAR, new PotionJarData(0, PotionContents.EMPTY, false));
        this.currentFill = jarContents.fill();
        this.data = jarContents.contents();
        this.isLocked = jarContents.locked();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder pComponents) {
        super.collectImplicitComponents(pComponents);
        if ((this.currentFill != 0 && !this.data.equals(PotionContents.EMPTY)) || this.isLocked) {
            pComponents.set(DataComponentRegistry.POTION_JAR, new PotionJarData(this.currentFill, this.data, this.isLocked));
        }
    }
}

