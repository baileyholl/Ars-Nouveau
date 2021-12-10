package com.hollingsworth.arsnouveau.api.mana;

import com.hollingsworth.arsnouveau.common.block.tile.AgronomicSourcelinkTile;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static com.hollingsworth.arsnouveau.api.NbtTags.MANA_TAG;
import static com.hollingsworth.arsnouveau.api.NbtTags.MAX_MANA_TAG;

public abstract class AbstractManaTile extends ModdedTile implements IManaTile {
    private int mana = 0;
    private int maxMana = 0;

    public AbstractManaTile(BlockEntityType<?> manaTile, BlockPos pos, BlockState state) {
        super(manaTile, pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        mana = tag.getInt(MANA_TAG);
        maxMana = tag.getInt(MAX_MANA_TAG);
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putInt(MANA_TAG, getCurrentMana());
        tag.putInt(MAX_MANA_TAG, getMaxMana());
    }

    @Override
    public int setMana(int mana) {
        this.mana = mana;
        if(this.mana > this.getMaxMana())
            this.mana = this.getMaxMana();
        if(this.mana < 0)
            this.mana = 0;
        update();
        return this.mana;
    }

    @Override
    public int addMana(int manaToAdd) {
        return this.setMana(this.getCurrentMana() + manaToAdd);
    }

    @Override
    public int getCurrentMana() {
        return this.mana;
    }

    @Override
    public int removeMana(int manaToRemove) {
        this.setMana(this.getCurrentMana() - manaToRemove);
        update();
        return this.getCurrentMana();
    }

    @Override
    public void setMaxMana(int max) {
        this.maxMana = max;
        update();
    }

    public boolean update(){
        if(this.worldPosition != null && this.level != null){
            level.sendBlockUpdated(this.worldPosition, level.getBlockState(worldPosition),  level.getBlockState(worldPosition), 2);
            return true;
        }
        return false;
    }

    @Override
    public int getMaxMana() {
        return maxMana;
    }

    public boolean canAcceptMana(){ return this.getCurrentMana() < this.getMaxMana(); }

    public int manaCanAccept(IManaTile tile){return tile.getMaxMana() - tile.getCurrentMana();}

    public int transferMana(IManaTile from, IManaTile to){
        int transferRate = getTransferRate(from, to);
        from.removeMana(transferRate);
        to.addMana(transferRate);
        return transferRate;
    }

    public int getTransferRate(IManaTile from, IManaTile to){
        return Math.min(Math.min(from.getTransferRate(), from.getCurrentMana()), to.getMaxMana() - to.getCurrentMana());
    }

    public int transferMana(IManaTile from, IManaTile to, int fromTransferRate){
        int transferRate = getTransferRate(from, to, fromTransferRate);
        if(transferRate == 0)
            return 0;
        from.removeMana(transferRate);
        to.addMana(transferRate);
        return transferRate;
    }

    public int getTransferRate(IManaTile from, IManaTile to, int fromTransferRate){
        return Math.min(Math.min(fromTransferRate, from.getCurrentMana()), to.getMaxMana() - to.getCurrentMana());
    }
}

