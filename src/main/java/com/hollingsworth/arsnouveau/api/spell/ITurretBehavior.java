package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;

public interface ITurretBehavior {
    /**
     * Called when the turret is fired and source has been expended.
     */
    void onCast(SpellResolver resolver, BasicSpellTurretTile tile, ServerLevel serverLevel, BlockPos pos, FakePlayer fakePlayer, Position dispensePosition, Direction direction);
}
