package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.stream.Collectors;

public class DroplessMobsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-dropless").executes(c -> {
            // The root command is accessible to everyone as it isn't too expensive to run.
            c.getSource().sendSystemMessage(Component.literal(listDroplessEntities(c.getSource().getServer().reloadableRegistries(), c.getSource().getLevel(), 0).stream().map(e -> BuiltInRegistries.ENTITY_TYPE.getKey(e).toString()).collect(Collectors.joining(","))));
            c.getSource().sendSystemMessage(Component.translatable("ars_nouveau.command.dropless.naive"));

            return 1;
        }).then(Commands.literal("simulate").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)).then(Commands.argument("times", IntegerArgumentType.integer(1)).executes(c -> {
            // This subcommand requires op as with enough simulations and entity types, it can get quite slow.
            int times = IntegerArgumentType.getInteger(c, "times");
            c.getSource().sendSystemMessage(Component.literal(listDroplessEntities(c.getSource().getServer().reloadableRegistries(), c.getSource().getLevel(), times).stream().map(e -> BuiltInRegistries.ENTITY_TYPE.getKey(e).toString()).collect(Collectors.joining(","))));
            c.getSource().sendSystemMessage(Component.translatable("ars_nouveau.command.dropless.simulated", times));

            return 1;
        }))));
    }

    public static ObjectArrayList<EntityType<?>> listDroplessEntities(ReloadableServerRegistries.Holder registries, ServerLevel level, int simulations) {
        ANFakePlayer fakePlayer = simulations <= 0 ? null : ANFakePlayer.getPlayer(level);
        DamageSource damageSource = simulations <= 0 ? null : level.damageSources().playerAttack(fakePlayer);
        ObjectArrayList<ItemStack> stacks = simulations <= 0 ? null : new ObjectArrayList<>();

        ObjectArrayList<EntityType<?>> types = new ObjectArrayList<>();
        outer:
        for (EntityType<?> ty : BuiltInRegistries.ENTITY_TYPE) {
            Entity e;
            try {
                e = ty.create(level, EntitySpawnReason.SPAWNER);
                if (e == null) {
                    continue;
                }

                if (!(e instanceof LivingEntity)) {
                    e.discard();
                    continue;
                }
            } catch (Throwable ignored) {
                continue;
            }

            LootTable table = e.getType().getDefaultLootTable().map(registries::getLootTable).orElse(LootTable.EMPTY);
            if (!table.pools.isEmpty()) {
                e.discard();
                continue;
            }

            if (simulations <= 0) {
                types.add(e.getType());
                continue;
            }

            LootParams.Builder ctx = (new LootParams.Builder(level))
                    .withParameter(LootContextParams.THIS_ENTITY, e).withParameter(LootContextParams.ORIGIN, e.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                    .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, fakePlayer)
                    .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity());
            ctx = ctx.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, fakePlayer)
                    .withLuck(fakePlayer.getLuck());

            for (int i = 0; i < simulations; i++) {
                stacks.clear();
                stacks.addAll(table.getRandomItems(ctx.create(LootContextParamSets.ENTITY)));
                if (!stacks.isEmpty()) {
                    e.discard();
                    continue outer;
                }
            }

            types.add(e.getType());
        }

        return types;
    }
}
