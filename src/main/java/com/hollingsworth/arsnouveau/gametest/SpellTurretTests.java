package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class SpellTurretTests {

    @GameTest(template = "empty10")
    public static void breaksBlocks(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectBreak.INSTANCE, EffectPickup.INSTANCE), (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                helper.setBlock(setup.posInFrontOfTurret, Blocks.DIRT.defaultBlockState());
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertBlockState(setup.posInFrontOfTurret, BlockBehaviour.BlockStateBase::isAir, () -> "Block was not broken " + setup.spell.toString());
            });
        });
        seq.thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void placesPhantomBlock(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectPhantomBlock.INSTANCE), (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state.getBlock() == BlockRegistry.MAGE_BLOCK.get(), () -> "Mageblock was not placed " + setup.spell.toString());
            });
        });
        seq.thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void exchangesBlock(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectExchange.INSTANCE, EffectPickup.INSTANCE), (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                helper.setBlock(setup.posInFrontOfTurret, Blocks.DIRT.defaultBlockState());
                setup.chest.setItem(0, Items.STONE.getDefaultInstance());
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state.getBlock() == Blocks.STONE, () -> "Stone was not exchanged " + setup.spell.toString());
                helper.assertContainerContains(setup.posBehindTurret, Blocks.DIRT.asItem());
            });
        });
        seq.thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void placesMagelight(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectLight.INSTANCE), (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state.getBlock() == BlockRegistry.LIGHT_BLOCK.get(), () -> "Mage light was not placed " + setup.spell.toString());
            });
        });
        seq.thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void placesRedstone(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectRedstone.INSTANCE), (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state.getBlock() == BlockRegistry.TEMPORARY_BLOCK.get(), () -> "Temporary redstone block was not placed " + setup.spell.toString());
            });
        });
        seq.thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void placesWater(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectConjureWater.INSTANCE), (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state.getBlock() == Blocks.WATER, () -> "Water block was not placed " + setup.spell.toString());
                helper.assertBlockState(setup.turretPos, (state) -> !state.getValue(BlockStateProperties.WATERLOGGED), () -> "Turret was accidentally waterlogged " + setup.spell.toString());
            });
        });

        assertTurretWithAllForms(seq, helper, new Spell(EffectConjureWater.INSTANCE), (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                helper.setBlock(setup.posInFrontOfTurret, Blocks.OAK_FENCE.defaultBlockState());
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state.is(Blocks.OAK_FENCE) && state.getValue(BlockStateProperties.WATERLOGGED), () -> "Fence was not waterlogged" + setup.spell.toString());
                helper.assertBlockState(setup.turretPos, (state) -> !state.getValue(BlockStateProperties.WATERLOGGED), () -> "Turret was accidentally waterlogged " + setup.spell.toString());
            });
        });
        seq.thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void placesPrestidigitation(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectPrestidigitation.INSTANCE), (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state.getBlock() == BlockRegistry.PARTICLE_BLOCK.get(), () -> "Prestidigitation block was not placed " + setup.spell.toString());
            });
        });
        seq.thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void placesFire(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectIgnite.INSTANCE), (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                helper.setBlock(setup.posInFrontOfTurret.relative(setup.direction), Blocks.OAK_PLANKS);
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state.getBlock() == Blocks.FIRE, () -> "Fire block was not placed against planks " + setup.direction + " " + setup.spell.toString());
            });
        });

        for (Block fire : List.of(Blocks.FIRE, BlockRegistry.MAGIC_FIRE.get())) {
            Spell spell = fire == Blocks.FIRE ? new Spell(EffectIgnite.INSTANCE) : new Spell(EffectIgnite.INSTANCE, AugmentSensitive.INSTANCE);
            assertTurretWithAllForms(seq, helper, spell, (supp, sequence) -> {
                sequence.thenExecute(() -> {
                    var setup = supp.get();
                    if (setup.direction != Direction.UP) {
                        helper.setBlock(setup.posInFrontOfTurret.below(), Blocks.GRASS_BLOCK);
                    }
                    setup.tile.shootSpell();
                }).thenWaitUntil(() -> {
                    var setup = supp.get();
                    if (setup.direction == Direction.UP) {
                        helper.assertEntityNotPresent(ModEntities.SPELL_PROJ.get());
                        helper.assertBlockState(setup.posInFrontOfTurret, BlockBehaviour.BlockStateBase::isAir, () -> "Fire was placed without support " + setup.spell.toString());
                        return;
                    }
                    helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state.getBlock() == fire, () -> "Fire block was not placed on grass " + setup.direction + " " + setup.spell.toString());
                    helper.setBlock(setup.posInFrontOfTurret.below(), Blocks.AIR);
                });
            });
        }

        assertTurretWithAllForms(seq, helper, new Spell(EffectIgnite.INSTANCE), (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                helper.setBlock(setup.posInFrontOfTurret, Blocks.CANDLE);
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state == Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.LIT, true), () -> "Ignite did not light candle block " + setup.spell.toString());
            });
        });
        seq.thenSucceed();
    }


    public static void assertTurretWithAllForms(GameTestSequence sequence, GameTestHelper helper, Spell partialSpell, BiConsumer<Supplier<TurretTest>, GameTestSequence> onTurretPlaced) {
        var touchSpell = new Spell(MethodTouch.INSTANCE);
        var projSpell = new Spell(MethodProjectile.INSTANCE);
        for (var part : partialSpell.recipe()) {
            touchSpell = touchSpell.add(part);
            projSpell = projSpell.add(part);
        }
        assertTurretInAllDirections(sequence, helper, touchSpell, onTurretPlaced);
        assertTurretInAllDirections(sequence, helper, projSpell, onTurretPlaced);
    }


    public static void assertTurretInAllDirections(GameTestSequence sequence, GameTestHelper helper, Spell spell, BiConsumer<Supplier<TurretTest>, GameTestSequence> onTurretPlaced) {
        helper.setBlock(BlockPos.ZERO, BlockRegistry.CREATIVE_SOURCE_JAR.defaultBlockState());

        BlockPos offset = new BlockPos(1, 4, 1);
        Map<Direction, TurretTest> setupMap = new HashMap<>();
        for (Direction direction : Direction.values()) {
            sequence.thenExecute(() -> {
                BlockPos frontOfTurret = offset.relative(direction);
                BlockPos twoFrontOfTurret = frontOfTurret.relative(direction);
                BlockPos behindTurret = offset.relative(direction.getOpposite());
                // Lets projectiles resolve one block further out if needed, i.e. for placing blocks
                helper.setBlock(twoFrontOfTurret, Blocks.DIRT.defaultBlockState());
                helper.setBlock(offset, BlockRegistry.BASIC_SPELL_TURRET.defaultBlockState().setValue(BasicSpellTurret.FACING, direction));
                helper.setBlock(behindTurret, Blocks.CHEST);
                setupMap.put(direction, new TurretTest(offset, frontOfTurret, behindTurret, helper.getBlockEntity(offset), helper.getBlockEntity(behindTurret), spell, direction));
                setupMap.get(direction).tile.setSpell(spell);

            });
            onTurretPlaced.accept(() -> setupMap.get(direction), sequence);
            sequence.thenExecute(() -> {
                var testSetup = setupMap.get(direction);
                testSetup.chest.clearContent();
                BlockPos twoFrontOfTurret = testSetup.posInFrontOfTurret.relative(direction);
                helper.assertBlock(testSetup.turretPos, (state) -> state == BlockRegistry.BASIC_SPELL_TURRET.get(), () -> "turret was unintentionally destroyed during test");
                helper.setBlock(testSetup.turretPos, Blocks.AIR.defaultBlockState());
                helper.setBlock(twoFrontOfTurret, Blocks.AIR.defaultBlockState());
                helper.setBlock(testSetup.posInFrontOfTurret, Blocks.AIR.defaultBlockState());
                helper.setBlock(testSetup.posBehindTurret, Blocks.AIR.defaultBlockState());
            });
        }
    }

    public record TurretTest(BlockPos turretPos, BlockPos posInFrontOfTurret, BlockPos posBehindTurret,
                             BasicSpellTurretTile tile, ChestBlockEntity chest, Spell spell, Direction direction) {

    }

}
