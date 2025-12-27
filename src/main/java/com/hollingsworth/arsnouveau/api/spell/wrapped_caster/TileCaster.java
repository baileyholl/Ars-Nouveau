package com.hollingsworth.arsnouveau.api.spell.wrapped_caster;

import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TileCaster implements IWrappedCaster {
    protected SpellContext.CasterType casterType;
    protected BlockEntity tile;

    public TileCaster(BlockEntity tile, SpellContext.CasterType casterType) {
        this.tile = tile;
        this.casterType = casterType;
    }

    @Override
    public @NotNull List<FilterableItemHandler> getInventory() {
        return new ArrayList<>(InvUtil.adjacentInventories(tile.getLevel(), tile.getBlockPos()));
    }

    @Override
    public SpellContext.CasterType getCasterType() {
        return casterType;
    }

    public BlockEntity getTile() {
        return tile;
    }

    @Override
    public Direction getFacingDirection() {
        return tile.getBlockState().getOptionalValue(BlockStateProperties.FACING).orElse(Direction.NORTH);
    }

    @Override
    public BlockEntity getNearbyBlockEntity(Predicate<BlockEntity> predicate) {
        for (Direction dir : Direction.values()) {
            BlockEntity tile = this.tile.getLevel().getBlockEntity(this.tile.getBlockPos().relative(dir));
            if (tile != null && predicate.test(tile)) {
                return tile;
            }
        }
        return null;
    }

    @Override
    public Vec3 getPosition() {
        return new Vec3(tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ());
    }

    @Override
    public boolean enoughMana(int totalCost) {
        if (tile instanceof BasicSpellTurretTile spellTurretTile) {
            var level = spellTurretTile.getLevel();
            var sp = spellTurretTile.getLinkedSourceProvider();
            if (level instanceof ServerLevel serverLevel && sp != null) {
                var cap = level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, sp.first(), sp.second().orElse(null));
                if (cap == null) {
                    spellTurretTile.setLinkedSourceProvider(null);
                    return false;
                }

                return cap.extractSource(totalCost, true) >= totalCost;
            }

            return SourceUtil.hasSourceNearby(tile.getBlockPos(), tile.getLevel(), 10, spellTurretTile.getManaCost());
        }

        return false;
    }

    @Override
    public void expendMana(int totalCost) {
        if (tile instanceof BasicSpellTurretTile spellTurretTile) {
            var manaCost = spellTurretTile.getManaCost();
            if (manaCost <= 0) {
                return;
            }

            var level = spellTurretTile.getLevel();
            var sp = spellTurretTile.getLinkedSourceProvider();
            if (level instanceof ServerLevel serverLevel && sp != null) {
                var cap = level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, sp.first(), sp.second().orElse(null));
                if (cap == null) {
                    spellTurretTile.setLinkedSourceProvider(null);
                    return;
                }

                cap.extractSource(manaCost, false);
                EntityFollowProjectile.spawn(serverLevel, sp.first(), spellTurretTile.getBlockPos());
                return;
            }

            SourceUtil.takeSourceMultipleWithParticles(tile.getBlockPos(), level, 10, manaCost);
        }
    }
}
