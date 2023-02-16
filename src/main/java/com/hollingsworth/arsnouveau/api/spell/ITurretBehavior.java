package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;

public interface ITurretBehavior {
    /**
     * Called when the turret is fired and source has been expended.
     * @deprecated Use {@link #onCast(SpellResolver, ServerLevel, BlockPos, Player, Position, Direction)} instead.
     * You can fetch the tile entity from the world.
     * This interface will now reflect general block casting, not just turrets.
     */
    @Deprecated(forRemoval = true)
    default void onCast(SpellResolver resolver, BasicSpellTurretTile tile, ServerLevel serverLevel, BlockPos pos, FakePlayer fakePlayer, Position dispensePosition, Direction direction){
        onCast(resolver, serverLevel, pos, fakePlayer, dispensePosition, direction);
    }

    default void onCast(SpellResolver resolver, ServerLevel serverLevel, BlockPos pos, Player fakePlayer, Position dispensePosition, Direction direction){

    }
}
