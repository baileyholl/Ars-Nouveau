package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class JabberwogFamiliar extends AbstractFamiliarHolder {

    public JabberwogFamiliar(){
        super("jabberwog", (e) -> e instanceof Starbuncle);
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
