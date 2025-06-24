package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public interface ITurretBehavior {

    default void onCast(SpellResolver resolver, ServerLevel serverLevel, BlockPos pos, Player fakePlayer, Position dispensePosition, Direction direction) {

    }
}
