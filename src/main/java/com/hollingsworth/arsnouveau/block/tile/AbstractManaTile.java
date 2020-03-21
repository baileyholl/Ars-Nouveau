package com.hollingsworth.arsnouveau.block.tile;

import com.hollingsworth.arsnouveau.api.mana.IManaBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

import static com.hollingsworth.arsnouveau.api.NbtTags.MANA_TAG;
import static com.hollingsworth.arsnouveau.api.NbtTags.MAX_MANA_TAG;

public abstract class AbstractManaTile extends AnimatedTile  implements IManaBlock {
    private int mana = 0;
    private int maxMana = 0;
    public AbstractManaTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void read(CompoundNBT tag) {
        mana = tag.getInt(MANA_TAG);
        maxMana = tag.getInt(MAX_MANA_TAG);
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putInt(MANA_TAG, mana);
        tag.putInt(MAX_MANA_TAG, maxMana);
        return super.write(tag);
    }

    @Override
    public int setMana(int mana) {
        this.mana = mana;
        if(this.mana > this.getMaxMana())
            this.mana = this.getMaxMana();
        if(this.mana < 0)
            this.mana = 0;
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
        return this.setMana(this.getCurrentMana() - manaToRemove);
    }

    @Override
    public void setMaxMana(int max) {
        this.maxMana = max;
    }

    @Override
    public int getMaxMana() {
        return maxMana;
    }

    public boolean canAcceptMana(){ return this.getCurrentMana() < this.getMaxMana(); }

    public void transferMana(IManaBlock from, IManaBlock to){
        if(from.getCurrentMana() >= from.getTransferRate()){
            from.removeMana(from.getTransferRate());
            to.addMana(from.getTransferRate());
        }
    }
}
