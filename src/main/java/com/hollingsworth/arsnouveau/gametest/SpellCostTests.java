package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectSummonWolves;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class SpellCostTests {

    @GameTest(template = "empty10")
    public static void spellCostUsesAugmentIdsForOverrides(GameTestHelper helper) {
        var form = MethodSelf.INSTANCE;
        var effect = EffectSummonWolves.INSTANCE;
        var extendTime = AugmentExtendTime.INSTANCE;
        var durationDown = AugmentDurationDown.INSTANCE;
        Spell extendTimeSpell = new Spell(form, effect, extendTime);
        Spell durationDownSpell = new Spell(form, effect, durationDown);
        int baseSpellCost = form.getCastingCost() + effect.getCastingCost();
        ModConfigSpec.ConfigValue<List<? extends String>> overrideConfig = effect.CONFIG.getValues().get(List.of("general", "augment_cost_overrides"));
        List<? extends String> originalOverrides = List.copyOf(overrideConfig.get());
        try {
            overrideConfig.set(List.of(
                    extendTime.getRegistryName() + "=500",
                    durationDown.getRegistryName() + "=250"
            ));

            helper.assertTrue(extendTimeSpell.getCost() == baseSpellCost + 500, "Expected Extend Time spell to use its 500 mana override");
            helper.assertTrue(durationDownSpell.getCost() == baseSpellCost + 250, "Expected Duration Down spell to use its 250 mana override");

            overrideConfig.set(List.of(extendTime.getRegistryName() + "=500"));
            helper.assertTrue(durationDownSpell.getCost() == baseSpellCost + durationDown.getCastingCost(), "Expected Duration Down spell to use its configured augment cost");
            helper.succeed();
        } finally {
            overrideConfig.set(originalOverrides);
        }
    }
}
