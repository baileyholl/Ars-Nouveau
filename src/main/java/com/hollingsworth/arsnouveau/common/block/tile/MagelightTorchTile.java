package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.MagelightTorch;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagelightTorchTile extends SconceTile {
    private boolean horizontalFire;

    public MagelightTorchTile(BlockPos pPos, BlockState pBlockState) {
        super(BlockRegistry.MAGELIGHT_TORCH_TILE.get(), pPos, pBlockState);
    }

    @Override
    public void onLight(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats stats, SpellContext spellContext) {
        super.onLight(rayTraceResult, world, shooter, stats, spellContext);
    }

    @Override
    public void tick() {
        if (!level.isClientSide() || !lit)
            return;
        BlockPos pos = getBlockPos();
        double xzOffset = 0.0375;
        BlockState state = this.getBlockState();
        boolean onFloor = state.hasProperty(MagelightTorch.FLOOR) && state.getValue(MagelightTorch.FLOOR);
        boolean onRoof = state.hasProperty(MagelightTorch.ROOF) && state.getValue(MagelightTorch.ROOF);
        double yOffset = onFloor ? 0.4 : 0.5;
        yOffset = onRoof ? 0.5 : yOffset;
        double xOffset = ParticleUtil.inRange(-xzOffset, xzOffset) + 0.5;
        double zOffset = ParticleUtil.inRange(-xzOffset, xzOffset) + 0.5;
        double centerX = pos.getX() + xOffset;
        double centerZ = pos.getZ() + zOffset;
        Direction facing = state.getValue(BlockStateProperties.FACING);
        int xRot = 0;
        int yRot = facing.get2DDataValue() * 90;
        switch (facing) {
            case NORTH:
                xRot = 90;
                yRot = 0;
                break;
            case SOUTH:
                xRot = -90;
                break;
            case EAST:
                xRot = 90;
                break;
            case WEST:
                xRot = 90;
                yRot = 90;
                break;
            default:
                break;
        }
        if (onRoof) {
            xRot = 180;
            yRot = 180;
        } else if (onFloor) {
            xRot = 0;
            yRot = 0;
        }
        if (particleEmitter != null) {
            particleEmitter.setPosition(new Vec3(centerX, pos.getY() + yOffset + ParticleUtil.inRange(-0.00, 0.1), centerZ));
            particleEmitter.setRotationOffset(xRot, yRot);
            particleEmitter.tick(level);
        }

    }

    public void setHorizontalFire(boolean horizontalFire) {
        this.horizontalFire = horizontalFire;
        updateBlock();
    }

    public boolean isHorizontalFire() {
        return horizontalFire;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putBoolean("horizontalFire", horizontalFire);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        horizontalFire = compound.getBoolean("horizontalFire");
    }
}
