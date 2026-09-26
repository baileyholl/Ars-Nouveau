package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBreak;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class EffectBreakTests {
    @GameTest(template = "empty10")
    public static void fallsbackToPickaxe(GameTestHelper helper) {
        BlockPos lowerPos = new BlockPos(1, 1, 1);
        BlockPos upperPos = lowerPos.above();
        BlockState door = Blocks.IRON_DOOR.defaultBlockState();
        helper.setBlock(lowerPos, door.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        helper.setBlock(upperPos, door.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));

        Player caster = helper.makeMockPlayer(GameType.SURVIVAL);
        caster.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemsRegistry.NOVICE_SPELLBOOK.get()));

        Spell spell = new Spell(MethodTouch.INSTANCE, EffectBreak.INSTANCE);
        SpellContext context = new SpellContext(helper.getLevel(), spell, caster, LivingCaster.from(caster));
        BlockPos pos = helper.absolutePos(upperPos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, false);
        SpellResolver resolver = new SpellResolver(context);
        resolver.onResolveEffect(helper.getLevel(), hit);

        helper.assertBlockNotPresent(Blocks.IRON_DOOR, lowerPos);
        helper.assertBlockNotPresent(Blocks.IRON_DOOR, upperPos);
        helper.assertItemEntityCountIs(Items.IRON_DOOR, lowerPos, 2.0, 1);
        helper.succeed();
    }
}
