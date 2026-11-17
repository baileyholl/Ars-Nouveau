package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class ArsNouveauGameTests {

    @GameTest(template = "empty10")
    public static void loadsModNamespace(GameTestHelper helper) {
        helper.succeed();
    }
}




