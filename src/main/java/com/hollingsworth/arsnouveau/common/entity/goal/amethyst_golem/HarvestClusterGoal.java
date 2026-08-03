package com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem;

import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.*;
import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider.CLUSTER_BLOCKS;

public class HarvestClusterGoal extends Goal {

    public AmethystGolem golem;
    public Supplier<Boolean> canUse;
    int tickTime;
    boolean isDone;

    List<BlockPos> harvestableList = new ArrayList<>();

    public HarvestClusterGoal(AmethystGolem golem, Supplier<Boolean> canUse) {
        this.golem = golem;
        this.canUse = canUse;
    }

    @Override
    public void tick() {
        super.tick();
        tickTime--;

        golem.getNavigation().stop();
        if (tickTime % 40 == 0) {
            tryDropAmethyst();
        }
        if (tickTime <= 0 || harvestableList.isEmpty()) {
            isDone = true;
            golem.setStomping(false);
            golem.harvestCooldown = 20 * 60;
        }
    }

    public void tryDropAmethyst() {
        BlockPos harvested = null;
        for (BlockPos p : harvestableList) {
            if (harvest(p)) {
                harvested = p;
                break;
            }
        }

        if (harvested != null) {
            harvestableList.remove(harvested);
        }
    }

    // The tool we use to simulate breaking the clusters with.
    public static final ItemStack TOOL = new ItemStack(Items.DIAMOND_PICKAXE);
    static {
        TOOL.set(DataComponents.UNBREAKABLE, new Unbreakable(false));
    }

    // Reusable list to store drops so that we don't have to keep allocating one for every face or budding block.
    private final List<ItemStack> drops = new ArrayList<>();

    public boolean harvest(BlockPos p) {
        if (!(golem.level instanceof ServerLevel level)) return false;

        LootParams.Builder lootBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.TOOL, TOOL);

        boolean hasUsableHome = golem.getHome() == null || !golem.canBreak(golem.getHome());

        boolean harvestedAny = false;
        for (Direction d : Direction.values()) {
            BlockPos pos = p.relative(d);
            if (!golem.canBreak(pos)) {
                continue;
            }

            BlockState state = level.getBlockState(pos);
            if (state.is(CLUSTER_BLOCKS)) {
                Vec3 center = Vec3.atCenterOf(pos);
                Block block = state.getBlock();

                // Clear our reusable list to store new drops.
                drops.clear();

                // Get the loot table of the block
                ResourceKey<LootTable> tableKey = block.getLootTable();
                if (tableKey != BuiltInLootTables.EMPTY) {
                    LootParams params = lootBuilder
                            .withParameter(LootContextParams.ORIGIN, center)
                            .withParameter(LootContextParams.BLOCK_STATE, state)
                            .create(LootContextParamSets.BLOCK);

                    LootTable table = level.getServer().reloadableRegistries().getLootTable(tableKey);
                    drops.addAll(table.getRandomItems(params));
                }

                // Handle drops
                if (hasUsableHome) {
                    for (ItemStack drop : drops) {
                        level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), drop));
                    }
                } else {
                    IItemHandler iItemHandler = golem.level.getCapability(Capabilities.ItemHandler.BLOCK, golem.getHome(), null);
                    if (iItemHandler == null) {
                        // If the golem's home is not available as an ItemHandler, just drop them as item entities.
                        for (ItemStack drop : drops) {
                            level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), drop));
                        }
                    } else {
                        var noSpaceLeft = false;
                        // Otherwise, try to insert directly.
                        for (ItemStack drop : drops) {
                            // Simulate insertion to ensure we have sufficient space for the drops.
                            ItemStack simLeft = ItemHandlerHelper.insertItemStacked(iItemHandler, drop.copy(), true);
                            if (!simLeft.isEmpty()) {
                                // No space left in inventory, don't harvest for this face.
                                // Other faces may have different clusters or drop a different
                                // amount of items that can fit, so we continue.
                                noSpaceLeft = true;
                                break;
                            }

                            // Attempt real insertion.
                            ItemStack left = ItemHandlerHelper.insertItemStacked(iItemHandler, drop.copy(), false);
                            if (!left.isEmpty()) {
                                // Since we already simulated, this should not be possible; however, we still want to preserve any drops if it does occur.
                                level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), left));
                            }
                        }

                        if (noSpaceLeft) {
                            continue;
                        }
                    }
                }

                // Destroy the cluster without spawning its drops as items as we already handled those.
                // setBlock should always return true once we reach here.
                if (level.setBlock(pos, state.getFluidState().createLegacyBlock(), 3, 512)) {
                    level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(null, state));
                }
                harvestedAny = true;
            }
        }

        return harvestedAny;
    }


    public boolean hasCluster(BlockPos p) {
        for (Direction d : Direction.values()) {
            if (golem.level.getBlockState(p.relative(d)).is(CLUSTER_BLOCKS)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        super.start();
        golem.setStomping(true);
        golem.getNavigation().stop();
        isDone = false;
        harvestableList = new ArrayList<>(golem.buddingBlocks);
        Collections.shuffle(harvestableList);
        tickTime = 130;
        golem.goalState = AmethystGolem.AmethystGolemGoalState.HARVEST;
    }

    @Override
    public void stop() {
        golem.setStomping(false);
        golem.goalState = AmethystGolem.AmethystGolemGoalState.NONE;
    }

    @Override
    public boolean canContinueToUse() {
        return !isDone;
    }

    @Override
    public boolean canUse() {
        return canUse.get() && !golem.buddingBlocks.isEmpty();
    }
}
