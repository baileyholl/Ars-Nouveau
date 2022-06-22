package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class JabberwogFamiliarHolder extends AbstractFamiliarHolder {

    public JabberwogFamiliarHolder(){
        super(LibEntityNames.FAMILIAR_JABBERWOG, (e) -> e instanceof Starbuncle);
    }

    @Override
    public IFamiliar getSummonEntity(Level world, CompoundTag tag) {
        return null;
    }

    @Override
    public String getBookName() {
        return "Jabberwog";
    }

    @Override
    public String getBookDescription() {
        return "";
    }
}
