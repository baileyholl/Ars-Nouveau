/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.multiblock.matcher;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.level.block.Blocks;

public class Matchers {
    public static final AnyMatcher ANY = new AnyMatcher();

    public static final PredicateMatcher AIR = new PredicateMatcher(Blocks.AIR.defaultBlockState(), ArsNouveau.loc("air"));

}
