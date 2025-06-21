package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.api.nbt.AbstractData;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PiglinBehavior extends JarBehavior<Piglin> {

    @Override
    public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, MobJarTile tile) {
        if (world.isClientSide)
            return;
        ItemStack heldStack = player.getItemInHand(handIn);
        Piglin piglin = entityFromJar(tile);
        boolean hasCurrency = piglin.getOffhandItem().isPiglinCurrency();
        if (heldStack.isPiglinCurrency() && !hasCurrency && piglin.isAdult()) {
            piglin.setItemInHand(InteractionHand.OFF_HAND, player.getItemInHand(handIn).split(1));
            tile.updateBlock();
        }
    }

    @Override
    public void tick(MobJarTile tile) {
        if (tile.getLevel().isClientSide) {
            return;
        }
        ExtraData data = new ExtraData(tile.getExtraDataTag());
        Piglin piglin = entityFromJar(tile);
        boolean hasCurrency = piglin.getOffhandItem().isPiglinCurrency();
        if (!hasCurrency && piglin.isAdult() && tile.getLevel().getRandom().nextInt(20) == 0) {
            List<ItemEntity> itemEntities = piglin.level.getEntitiesOfClass(ItemEntity.class, new AABB(tile.getBlockPos()).inflate(3), ItemEntity::isAlive);
            if (!itemEntities.isEmpty()) {
                ItemEntity itemEntity = itemEntities.stream().filter(item -> item.getItem().isPiglinCurrency()).findFirst().orElse(null);
                if (itemEntity != null) {
                    piglin.setItemInHand(InteractionHand.OFF_HAND, itemEntity.getItem().split(1));
                    tile.updateBlock();
                }
            }
        }

        boolean isAdmiring = piglin.isAdult() && piglin.getOffhandItem().isPiglinCurrency();
        if (isAdmiring) {
            data.ticksWithCurrency++;
            if (data.ticksWithCurrency >= 60) {
                data.ticksWithCurrency = 0;
                throwItems(piglin, getBarterResponseItems(piglin));
                piglin.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                tile.updateBlock();
            }
            CompoundTag updateTag = new CompoundTag();
            data.writeToNBT(updateTag);
            tile.setExtraDataTag(updateTag);
        }
    }

    private static List<ItemStack> getBarterResponseItems(Piglin pPiglin) {
        LootTable loottable = pPiglin.level().getServer().reloadableRegistries().getLootTable(BuiltInLootTables.PIGLIN_BARTERING);
        List<ItemStack> list = loottable.getRandomItems((new LootParams.Builder((ServerLevel) pPiglin.level())).withParameter(LootContextParams.THIS_ENTITY, pPiglin).create(LootContextParamSets.PIGLIN_BARTER));
        return list;
    }

    private static void throwItems(Piglin pPilgin, List<ItemStack> pStacks) {
        List<Player> players = pPilgin.level.getEntitiesOfClass(Player.class, pPilgin.getBoundingBox().inflate(3));
        if (!players.isEmpty()) {
            throwItemsTowardPlayer(pPilgin, players.get(0), pStacks);
        } else {
            throwItemsTowardRandomPos(pPilgin, pStacks);
        }

    }

    private static void throwItemsTowardRandomPos(Piglin pPiglin, List<ItemStack> pStacks) {
        throwItemsTowardPos(pPiglin, pStacks, getRandomNearbyPos(pPiglin));
    }

    private static void throwItemsTowardPlayer(Piglin pPiglin, Player pPlayer, List<ItemStack> pStacks) {
        throwItemsTowardPos(pPiglin, pStacks, pPlayer.position());
    }

    private static Vec3 getRandomNearbyPos(Piglin pPiglin) {
        Vec3 vec3 = LandRandomPos.getPos(pPiglin, 4, 2);
        return vec3 == null ? pPiglin.position() : vec3;
    }


    private static void throwItemsTowardPos(Piglin pPiglin, List<ItemStack> pStacks, Vec3 pPos) {
        if (!pStacks.isEmpty()) {
            pPiglin.swing(InteractionHand.OFF_HAND);

            for (ItemStack itemstack : pStacks) {
                BehaviorUtils.throwItem(pPiglin, itemstack, pPos.add(0.0D, 0.0D, 0.0D));
            }
        }

    }


    public static class ExtraData extends AbstractData {
        boolean hasCurrency;
        int ticksWithCurrency;

        public ExtraData(CompoundTag tag) {
            super(tag);
            hasCurrency = tag.getBoolean("hasCurrency");
            ticksWithCurrency = tag.getInt("ticksWithCurrency");
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            tag.putBoolean("hasCurrency", hasCurrency);
            tag.putInt("ticksWithCurrency", ticksWithCurrency);
        }
    }
}
