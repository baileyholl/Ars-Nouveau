package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectSmelt;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.GenericRecipeCache;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.lang.reflect.Field;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class RecipeCacheTests {

    @GameTest(template = "empty10")
    public static void cacheCapacityStaysBoundedWithRepeatedMisses(GameTestHelper helper) {
        BlockPos floor = new BlockPos(1, 1, 1);
        helper.setBlock(floor, Blocks.STONE);

        Player caster = helper.makeMockPlayer(GameType.SURVIVAL);
        caster.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemsRegistry.NOVICE_SPELLBOOK.get()));
        Spell spell = new Spell(MethodTouch.INSTANCE, EffectSmelt.INSTANCE, AugmentSensitive.INSTANCE);
        BlockPos target = helper.absolutePos(floor);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(target), Direction.UP, target, false);
        int maxCapacity = 64;
        int ticks = 64;
        int limit = Math.max(maxCapacity, capacity(GenericRecipeCache.SMELTING_CACHE));

        for (int tick = 0; tick < ticks; tick++) {
            int id = tick;
            helper.runAfterDelay(tick, () -> {
                ItemEntity stick = helper.spawnItem(Items.STICK, 1.5f, 2.0f, 1.5f);
                stick.getItem().set(DataComponents.CUSTOM_NAME, Component.literal("unsmeltable " + id));
                SpellContext context = new SpellContext(helper.getLevel(), spell, caster, LivingCaster.from(caster));
                new SpellResolver(context).onResolveEffect(helper.getLevel(), hit);
            });
        }

        helper.runAfterDelay(ticks + 2, () -> {
            int capacity = capacity(GenericRecipeCache.SMELTING_CACHE);
            helper.assertTrue(capacity <= limit, "Smelting cache grew to " + capacity + " slots for " + ticks + " distinct inputs, limit " + limit);
            helper.succeed();
        });
    }

    private static int capacity(GenericRecipeCache<?, ?> cache) {
        try {
            Field size = GenericRecipeCache.class.getDeclaredField("size");
            size.setAccessible(true);
            return size.getInt(cache);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
