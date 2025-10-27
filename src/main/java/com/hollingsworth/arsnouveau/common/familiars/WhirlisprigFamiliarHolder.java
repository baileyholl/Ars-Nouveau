package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.Whirlisprig;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarWhirlisprig;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class WhirlisprigFamiliarHolder extends AbstractFamiliarHolder {
    public WhirlisprigFamiliarHolder() {
        super(LibEntityNames.FAMILIAR_WHIRLISPRIG, (e) -> e instanceof Whirlisprig);
    }

    @Override
    public IFamiliar getSummonEntity(Level world, CompoundTag tag) {
        FamiliarWhirlisprig whirlisprig = new FamiliarWhirlisprig(ModEntities.ENTITY_FAMILIAR_SYLPH.get(), world);
        whirlisprig.setTagData(tag);
        return whirlisprig;
    }

    @Override
    public String getBookName() {
        return "Whirlisprig";
    }

    @Override
    public String getBookDescription() {
        return "Whirlisprigs will reduce the cost of Elemental Earth glyphs by half, and grants bonus saturation when consuming food. Obtained by performing the Ritual of Binding near a Whirlisprig.";
    }
}
