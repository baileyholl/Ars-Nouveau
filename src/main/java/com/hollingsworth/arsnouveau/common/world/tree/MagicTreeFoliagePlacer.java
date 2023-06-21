package com.hollingsworth.arsnouveau.common.world.tree;

import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class MagicTreeFoliagePlacer extends FoliagePlacer {
    public MagicTreeFoliagePlacer(UniformInt p_i241999_1_, UniformInt p_i241999_2_) {
        super(p_i241999_1_, p_i241999_2_);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return FoliagePlacerType.BLOB_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(LevelSimulatedReader p_225613_, FoliageSetter p_273598_, RandomSource p_225615_, TreeConfiguration p_225616_, int p_225617_, FoliageAttachment p_225618_, int p_225619_, int p_225620_, int p_225621_) {

    }


    @Override
    public int foliageHeight(RandomSource p_230374_1_, int p_230374_2_, TreeConfiguration p_230374_3_) {
        return 0;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource p_230373_1_, int p_230373_2_, int p_230373_3_, int p_230373_4_, int p_230373_5_, boolean p_230373_6_) {
        return false;
    }
}
