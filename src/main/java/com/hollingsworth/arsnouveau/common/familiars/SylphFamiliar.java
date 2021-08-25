package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.EntitySylph;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarSylph;
import net.minecraft.world.World;

public class SylphFamiliar extends AbstractFamiliarHolder {
    public SylphFamiliar() {
        super("sylph", (e) -> e instanceof EntitySylph);
    }

    @Override
    public IFamiliar getSummonEntity(World world) {
        return new FamiliarSylph(ModEntities.ENTITY_FAMILIAR_SYLPH, world);
    }

    @Override
    public String getBookName() {
        return "Sylph";
    }

    @Override
    public String getBookDescription() {
        return "Sylphs will reduce the cost of Elemental Earth glyphs by half, and grants bonus saturation when consuming food.";
    }
}
