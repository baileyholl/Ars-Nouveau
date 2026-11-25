package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectPlaceBlock;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.spell.method.MethodUnderfoot;
import net.minecraft.commands.arguments.EntityAnchorArgument;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class EffectPlaceBlockTests {
    @GameTest(template = "empty10")
    public static void placesInPlayerDirection(GameTestHelper helper) {
        BlockPos floorPos = new BlockPos(1, 1, 1);
        helper.setBlock(floorPos, Blocks.STONE);
        BlockPos pos = helper.absolutePos(floorPos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        Player caster = helper.makeMockPlayer(GameType.SURVIVAL);

        for (Direction direction : Direction.values()) {
            caster.lookAt(EntityAnchorArgument.Anchor.EYES, caster.getEyePosition().relative(direction, 1));
            for (int sensitive = 0; sensitive <= 2; sensitive++) {
                helper.setBlock(floorPos.above(), Blocks.AIR);
                caster.setItemInHand(InteractionHand.MAIN_HAND, Items.PISTON.getDefaultInstance());
                Spell spell = new Spell(MethodTouch.INSTANCE, EffectPlaceBlock.INSTANCE).add(AugmentSensitive.INSTANCE, sensitive);
                SpellContext context = new SpellContext(helper.getLevel(), spell, caster, LivingCaster.from(caster));
                new SpellResolver(context).onResolveEffect(helper.getLevel(), hit);

                Direction expected = sensitive == 0 ? Direction.SOUTH : sensitive == 1 ? direction : direction.getOpposite();
                helper.assertBlockProperty(floorPos.above(), BlockStateProperties.FACING, expected.getOpposite());
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void placesUnderfoot(GameTestHelper helper) {
        BlockPos floorPos = new BlockPos(1, 1, 1);
        Player caster = helper.makeMockPlayer(GameType.CREATIVE);
        caster.setPos(helper.absolutePos(floorPos.above()).getBottomCenter());

        for (Direction direction : Direction.values()) {
            caster.lookAt(EntityAnchorArgument.Anchor.EYES, caster.getEyePosition().relative(direction, 1));
            for (int sensitive = 0; sensitive <= 2; sensitive++) {
                helper.setBlock(floorPos, Blocks.AIR);
                caster.setItemInHand(InteractionHand.MAIN_HAND, Items.PISTON.getDefaultInstance());
                Spell spell = new Spell(MethodUnderfoot.INSTANCE, EffectPlaceBlock.INSTANCE).add(AugmentSensitive.INSTANCE, sensitive);
                SpellContext context = new SpellContext(helper.getLevel(), spell, caster, LivingCaster.from(caster));
                new SpellResolver(context).onCast(ItemStack.EMPTY, helper.getLevel());

                Direction expected = sensitive == 0 ? Direction.SOUTH : sensitive == 1 ? direction : direction.getOpposite();
                helper.assertBlockProperty(floorPos, BlockStateProperties.FACING, expected.getOpposite());
            }
        }
        helper.succeed();
    }
}
