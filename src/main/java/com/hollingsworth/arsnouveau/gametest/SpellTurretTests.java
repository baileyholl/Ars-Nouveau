package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class SpellTurretTests {

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void breaksBlocks(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectBreak.INSTANCE, EffectPickup.INSTANCE), true, (supp, sequence) -> {
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

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void placesPhantomBlock(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectPhantomBlock.INSTANCE), true, (supp, sequence) -> {
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

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void placesBlock(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        for (int i = 0; i <= 2; i++) {
            int sensitive = i;
            assertTurretWithAllForms(seq, helper, new Spell(EffectPlaceBlock.INSTANCE).add(AugmentSensitive.INSTANCE, sensitive), true, (supp, sequence) -> {
                sequence.thenExecute(() -> {
                    var setup = supp.get();
                    setup.chest.setItem(0, Items.PISTON.getDefaultInstance());
                    setup.tile.shootSpell();
                }).thenWaitUntil(() -> {
                    var setup = supp.get();
                    Direction expected = sensitive == 0 ? Direction.SOUTH : sensitive == 1 ? setup.direction : setup.direction.getOpposite();
                    helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state.is(Blocks.PISTON) && state.getValue(BlockStateProperties.FACING) == expected.getOpposite(), () -> "Piston was not placed in direction " + expected + " " + setup.direction + " " + setup.spell.toString());
                    helper.assertContainerEmpty(setup.posBehindTurret);
                });
            });
        }
        seq.thenSucceed();
    }

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void exchangesBlock(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectExchange.INSTANCE, EffectPickup.INSTANCE), true, (supp, sequence) -> {
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

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void placesMagelight(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectLight.INSTANCE), true, (supp, sequence) -> {
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

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void placesRedstone(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectRedstone.INSTANCE), true, (supp, sequence) -> {
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

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void placesWater(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectConjureWater.INSTANCE), true, (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state.getBlock() == Blocks.WATER, () -> "Water block was not placed " + setup.spell.toString());
                helper.assertBlockState(setup.turretPos, (state) -> !state.getValue(BlockStateProperties.WATERLOGGED), () -> "Turret was accidentally waterlogged " + setup.spell.toString());
            });
        });

        assertTurretWithAllForms(seq, helper, new Spell(EffectConjureWater.INSTANCE), true, (supp, sequence) -> {
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

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void placesPrestidigitation(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectPrestidigitation.INSTANCE), true, (supp, sequence) -> {
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

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void placesFire(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectIgnite.INSTANCE), true, (supp, sequence) -> {
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
            assertTurretWithAllForms(seq, helper, spell, true, (supp, sequence) -> {
                sequence.thenExecute(() -> {
                    var setup = supp.get();
                    if (setup.posInFrontOfTurret.getY() <= setup.turretPos.getY()) {
                        helper.setBlock(setup.posInFrontOfTurret.below(), Blocks.GRASS_BLOCK);
                    }
                    setup.tile.shootSpell();
                }).thenWaitUntil(() -> {
                    var setup = supp.get();
                    if (setup.posInFrontOfTurret.getY() > setup.turretPos.getY()) {
                        helper.assertEntityNotPresent(ModEntities.SPELL_PROJ.get());
                        helper.assertBlockState(setup.posInFrontOfTurret, BlockBehaviour.BlockStateBase::isAir, () -> "Fire was placed without support " + setup.spell.toString());
                        return;
                    }
                    helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state.getBlock() == fire, () -> "Fire block was not placed on grass " + setup.direction + " " + setup.spell.toString());
                    helper.setBlock(setup.posInFrontOfTurret.below(), Blocks.AIR);
                });
            });
        }

        assertTurretWithAllForms(seq, helper, new Spell(EffectIgnite.INSTANCE), true, (supp, sequence) -> {
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

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void shootsFirework(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectFirework.INSTANCE), false, (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                var spell = setup.spell;
                var fireworks = helper.getEntities(EntityType.FIREWORK_ROCKET);
                if (spell.getCastMethod() == MethodTouch.INSTANCE) {
                    helper.assertTrue(!fireworks.isEmpty() && Direction.getNearest(fireworks.getFirst().getDeltaMovement()) == setup.direction, "Firework was not shot in the correct direction " + setup.direction + " " + spell);
                } else {
                    helper.assertTrue(!fireworks.isEmpty() && Direction.getNearest(fireworks.getFirst().getDeltaMovement()) == Direction.UP, "Firework was not shot in the correct direction " + setup.direction + " " + spell);
                }
            }).thenExecute(() -> {
                for (FireworkRocketEntity rocketEntity : helper.getEntities(EntityType.FIREWORK_ROCKET)) {
                    rocketEntity.remove(Entity.RemovalReason.DISCARDED);
                }
            });
        });
        seq.thenSucceed();
    }

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void interactsWithWaterBucket(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        // Interact with air in front
        assertTurretWithAllForms(seq, helper, new Spell(EffectInteract.INSTANCE), true, (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                setup.chest.setItem(0, Items.WATER_BUCKET.getDefaultInstance());
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertBlockState(setup.posInFrontOfTurret, (state) -> state == Blocks.WATER.defaultBlockState(), () -> "Interact did not place water block " + setup.spell.toString());
                helper.assertContainerContains(setup.posBehindTurret, Items.BUCKET);
            }).thenExecute(() -> {
                var setup = supp.get();
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertBlockState(setup.posInFrontOfTurret, BlockBehaviour.BlockStateBase::isAir, () -> "Interact did not pick up water block " + setup.spell.toString());
                helper.assertContainerContains(setup.posBehindTurret, Items.WATER_BUCKET);
            });
        });
        seq.thenSucceed();
    }

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void tossesIntoInventory(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectToss.INSTANCE), true, (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                helper.setBlock(setup.posInFrontOfTurret, Blocks.CHEST);
                setup.chest.setItem(0, Items.STONE.getDefaultInstance());
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                helper.assertContainerContains(setup.posInFrontOfTurret, Items.STONE);
                helper.assertContainerEmpty(setup.posBehindTurret);
            }).thenExecute(() -> {
                var setup = supp.get();
                ChestBlockEntity target = helper.getBlockEntity(setup.posInFrontOfTurret);
                target.clearContent();
            });
        });
        seq.thenSucceed();
    }

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void growsPlants(GameTestHelper helper) {
        helper.setBlock(new BlockPos(0, 3, 0), BlockRegistry.CREATIVE_SOURCE_JAR.defaultBlockState());
        Spell spell = new Spell(MethodTouch.INSTANCE, EffectGrow.INSTANCE);
        BlockState turret = BlockRegistry.BASIC_SPELL_TURRET.defaultBlockState().setValue(BasicSpellTurret.FACING, Direction.EAST);

        BlockPos cropPos = new BlockPos(7, 2, 7);
        helper.setBlock(cropPos.below(), Blocks.FARMLAND);
        helper.setBlock(cropPos, Blocks.WHEAT);
        helper.setBlock(cropPos.west(), turret);
        BasicSpellTurretTile cropTurret = helper.getBlockEntity(cropPos.west());
        cropTurret.setSpell(spell);
        cropTurret.shootSpell();
        helper.assertBlockState(cropPos, (state) -> state.is(Blocks.WHEAT) && state.getValue(BlockStateProperties.AGE_7) > 0, () -> "Wheat was not grown");

        for (int x = 0; x <= 4; x++) {
            for (int z = 0; z <= 4; z++) {
                helper.setBlock(new BlockPos(x, 1, z), Blocks.DIRT);
                helper.setBlock(new BlockPos(x, 2, z), x == 0 || z == 0 || x == 4 || z == 4 ? Blocks.GLASS : Blocks.WATER);
            }
        }
        BlockPos waterPos = new BlockPos(2, 2, 2);
        helper.setBlock(waterPos.west(), turret.setValue(BlockStateProperties.WATERLOGGED, true));
        BasicSpellTurretTile waterTurret = helper.getBlockEntity(waterPos.west());
        waterTurret.setSpell(spell);
        waterTurret.shootSpell();
        helper.assertBlockState(waterPos, (state) -> state.is(Blocks.SEAGRASS) || state.is(Blocks.TALL_SEAGRASS), () -> "Seagrass was not grown in the water in front of the turret");
        helper.succeed();
    }

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void sensitiveProjectileGrowsCrop(GameTestHelper helper) {
        helper.setBlock(new BlockPos(0, 3, 0), BlockRegistry.CREATIVE_SOURCE_JAR.defaultBlockState());
        BlockPos cropPos = new BlockPos(7, 2, 7);
        BlockPos turretPos = cropPos.west(3);
        int startAge = 3;
        helper.setBlock(cropPos.below(), Blocks.FARMLAND);
        helper.setBlock(cropPos, Blocks.WHEAT.defaultBlockState().setValue(BlockStateProperties.AGE_7, startAge));
        helper.setBlock(turretPos, BlockRegistry.BASIC_SPELL_TURRET.defaultBlockState().setValue(BasicSpellTurret.FACING, Direction.EAST));
        BasicSpellTurretTile tile = helper.getBlockEntity(turretPos);
        tile.setSpell(new Spell(MethodProjectile.INSTANCE, AugmentSensitive.INSTANCE, EffectGrow.INSTANCE));
        tile.shootSpell();
        helper.succeedWhen(() -> helper.assertBlockState(cropPos, (state) -> state.is(Blocks.WHEAT) && state.getValue(BlockStateProperties.AGE_7) > startAge, () -> "Wheat was not grown by the sensitive projectile"));
    }

    @GameTest(template = "empty10", timeoutTicks = 500)
    public static void placesRune(GameTestHelper helper) {
        GameTestSequence seq = helper.startSequence();
        assertTurretWithAllForms(seq, helper, new Spell(EffectRune.INSTANCE), true, (supp, sequence) -> {
            sequence.thenExecute(() -> {
                var setup = supp.get();
                setup.tile.shootSpell();
            }).thenWaitUntil(() -> {
                var setup = supp.get();
                boolean touch = setup.spell.getCastMethod() == MethodTouch.INSTANCE;
                helper.assertBlockState(setup.posInFrontOfTurret, (state) -> {
                    if (state.getBlock() != BlockRegistry.RUNE_BLOCK.get()) {
                        return false;
                    }
                    Direction facing = state.getValue(RuneBlock.FACING);
                    return touch ? facing == Direction.UP : !helper.getBlockState(setup.posInFrontOfTurret.relative(facing.getOpposite())).isAir();
                }, () -> "Rune block was not placed " + (touch ? "facing up " : "against the block it hit ") + setup.direction + " " + setup.spell.toString());
            });
        });
        seq.thenSucceed();
    }


    public static void assertTurretWithAllForms(GameTestSequence sequence, GameTestHelper helper, Spell partialSpell, boolean diagonals, BiConsumer<Supplier<TurretTest>, GameTestSequence> onTurretPlaced) {
        var touchSpell = new Spell(MethodTouch.INSTANCE);
        var projSpell = new Spell(MethodProjectile.INSTANCE);
        for (var part : partialSpell.recipe()) {
            touchSpell = touchSpell.add(part);
            projSpell = projSpell.add(part);
        }
        assertTurretInAllDirections(sequence, helper, touchSpell, diagonals, onTurretPlaced);
        assertTurretInAllDirections(sequence, helper, projSpell, diagonals, onTurretPlaced);
    }


    public static void assertTurretInAllDirections(GameTestSequence sequence, GameTestHelper helper, Spell spell, boolean diagonals, BiConsumer<Supplier<TurretTest>, GameTestSequence> onTurretPlaced) {
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
        FakePlayer aimer = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "turret_aimer"));
        aimer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemsRegistry.DOMINION_ROD.get()));
        Map<BlockPos, TurretTest> rotatingSetupMap = new HashMap<>();
        for (BlockPos pos : BlockPos.betweenClosed(offset.offset(-1, -1, -1), offset.offset(1, 1, 1))) {
            if (pos.equals(offset) || !diagonals && pos.distManhattan(offset) > 1) {
                continue;
            }
            BlockPos target = pos.immutable();
            BlockPos aim = target.subtract(offset);
            List<BlockPos> backstops = new ArrayList<>();
            for (Direction side : Direction.values()) {
                if (side.getStepX() * aim.getX() + side.getStepY() * aim.getY() + side.getStepZ() * aim.getZ() > 0) {
                    backstops.add(target.relative(side));
                }
            }
            sequence.thenExecute(() -> {
                helper.setBlock(offset, BlockRegistry.ROTATING_TURRET.get());
                for (BlockPos clicked : List.of(offset, target)) {
                    BlockPos absolute = helper.absolutePos(clicked);
                    aimer.gameMode.useItemOn(aimer, helper.getLevel(), aimer.getMainHandItem(), InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false));
                }
                RotatingTurretTile tile = helper.getBlockEntity(offset);
                tile.setRotationX(tile.neededRotationX);
                tile.setRotationY(tile.neededRotationY);
                tile.setSpell(spell);
                Direction direction = Direction.getNearest(tile.getShootAngle());
                for (BlockPos backstop : backstops) {
                    helper.setBlock(backstop, Blocks.DIRT);
                }
                BlockPos chestPos = offset.relative(direction.getOpposite());
                helper.setBlock(chestPos, Blocks.CHEST);
                rotatingSetupMap.put(target, new TurretTest(offset, target, chestPos, tile, helper.getBlockEntity(chestPos), spell, direction));
            });
            onTurretPlaced.accept(() -> rotatingSetupMap.get(target), sequence);
            sequence.thenExecute(() -> {
                var testSetup = rotatingSetupMap.get(target);
                testSetup.chest.clearContent();
                helper.assertBlock(offset, (block) -> block == BlockRegistry.ROTATING_TURRET.get(), () -> "rotating turret was unintentionally destroyed during test aiming at " + aim);
                helper.getLevel().getBlockTicks().clearArea(new BoundingBox(helper.absolutePos(offset)));
                helper.setBlock(offset, Blocks.AIR);
                helper.setBlock(target, Blocks.AIR);
                helper.setBlock(testSetup.posBehindTurret, Blocks.AIR);
                for (BlockPos backstop : backstops) {
                    helper.setBlock(backstop, Blocks.AIR);
                }
            });
        }
    }

    public record TurretTest(BlockPos turretPos, BlockPos posInFrontOfTurret, BlockPos posBehindTurret,
                             BasicSpellTurretTile tile, ChestBlockEntity chest, Spell spell, Direction direction) {

    }

}
