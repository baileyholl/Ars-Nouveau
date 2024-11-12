package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.common.entity.AbstractWilden;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.PatrolSpawner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.jetbrains.annotations.NotNull;

public class WildenPatrolSpawner extends PatrolSpawner {
    private int nextTick;

    public WildenPatrolSpawner() {
        super();
    }

    public int tick(@NotNull ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies) {
        if (!spawnEnemies) {
            return 0;
        } else if (!level.getGameRules().getBoolean(GameRules.RULE_DO_PATROL_SPAWNING)) {
            return 0;
        } else {
            RandomSource randomSource = level.random;
            --this.nextTick;
            if (this.nextTick > 0) {
                return 0;
            } else {
                this.nextTick += 120 + randomSource.nextInt(120);
                long daysElapsed = level.getDayTime() / 24000L;
                if (daysElapsed >= 5L && level.isNight()) {
                    if (randomSource.nextInt(5) != 0) {
                        return 0;
                    } else {
                        int playerCount = level.players().size();
                        if (playerCount < 1) {
                            return 0;
                        } else {
                            Player randomPlayer = level.players().get(randomSource.nextInt(playerCount));
                            if (randomPlayer.isSpectator()) {
                                return 0;
                            } else if (level.isCloseToVillage(randomPlayer.blockPosition(), 2)) {
                                return 0;
                            } else {
                                int xOffset = (24 + randomSource.nextInt(24)) * (randomSource.nextBoolean() ? -1 : 1);
                                int zOffset = (24 + randomSource.nextInt(24)) * (randomSource.nextBoolean() ? -1 : 1);
                                BlockPos.MutableBlockPos spawnPosition = randomPlayer.blockPosition().mutable().move(xOffset, 0, zOffset);
                                if (!level.hasChunksAt(spawnPosition.getX() - 10, spawnPosition.getZ() - 10, spawnPosition.getX() + 10, spawnPosition.getZ() + 10)) {
                                    return 0;
                                } else {
                                    Holder<Biome> biome = level.getBiome(spawnPosition);
                                    if (biome.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                                        return 0;
                                    } else {
                                        int spawnedMembers = 0;
                                        int maxMembers = (int) Math.ceil(level.getCurrentDifficultyAt(spawnPosition).getEffectiveDifficulty()) + 1;

                                        // roll for wilden type
                                        EntityType<? extends PatrollingMonster> wildenType = AbstractWilden.getRandomWildenType(biome, randomSource);
                                        for (int i = 0; i < maxMembers; ++i) {
                                            ++spawnedMembers;
                                            spawnPosition.setY(level.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, spawnPosition).getY());
                                            if (i == 0) {
                                                if (!this.spawnPatrolMember(level, spawnPosition, randomSource, true, wildenType)) {
                                                    break;
                                                }
                                            } else {
                                                this.spawnPatrolMember(level, spawnPosition, randomSource, false, wildenType);
                                            }

                                            spawnPosition.setX(spawnPosition.getX() + randomSource.nextInt(5) - randomSource.nextInt(5));
                                            spawnPosition.setZ(spawnPosition.getZ() + randomSource.nextInt(5) - randomSource.nextInt(5));
                                        }

                                        return spawnedMembers;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean spawnPatrolMember(ServerLevel level, BlockPos position, RandomSource randomSource, boolean isLeader, EntityType<? extends PatrollingMonster> wildenType) {
        BlockState blockState = level.getBlockState(position);
        if (!NaturalSpawner.isValidEmptySpawnBlock(level, position, blockState, blockState.getFluidState(), wildenType)) {
            return false;
        } else if (!AbstractWilden.checkPatrollingMonsterSpawnRules(wildenType, level, MobSpawnType.PATROL, position, randomSource)) {
            return false;
        } else {
            PatrollingMonster pillager = wildenType.create(level);
            if (pillager != null) {
                if (isLeader) {
                    pillager.setPatrolLeader(true);
                    pillager.findPatrolTarget();
                }

                pillager.setPos(position.getX(), position.getY(), position.getZ());
                pillager.finalizeSpawn(level, level.getCurrentDifficultyAt(position), MobSpawnType.PATROL, null, null);
                level.addFreshEntityWithPassengers(pillager);
                return true;
            } else {
                return false;
            }
        }
    }


}
