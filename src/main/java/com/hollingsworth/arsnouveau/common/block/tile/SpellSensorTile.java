package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.util.LevelPosMap;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SpellSensorTile extends ModdedTile implements ITickable {

    public static LevelPosMap SENSOR_MAP = new LevelPosMap((level, pos) -> !(level.getBlockEntity(pos) instanceof SpellSensorTile));

    public int outputDuration;
    public int outputStrength;
    public int listenRange = 20;

    public SpellSensorTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public SpellSensorTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SPELL_SENSOR_TILE.get(), pos, state);
    }

    public static void onSpellCast(SpellCastEvent event){
        SENSOR_MAP.applyForRange(event.getWorld(), new BlockPos(event.context.getCaster().getPosition()), 100, (pos) ->{
            if(event.getWorld().getBlockEntity(pos) instanceof SpellSensorTile){
                SpellSensorTile tile = (SpellSensorTile) event.getWorld().getBlockEntity(pos);
                int oldStrength = tile.outputStrength;
                int oldDuration = tile.outputDuration;
                tile.outputDuration = 20;
                tile.outputStrength = 15;
                tile.updateBlock();
                if(oldDuration <= 0 || oldStrength != tile.outputStrength){
                    tile.level.updateNeighborsAt(tile.worldPosition, tile.getBlockState().getBlock());
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void tick() {
        if(level.isClientSide){
            return;
        }
        if(outputDuration > 0){
            outputDuration--;
            if(outputDuration <= 0){
                outputStrength = 0;
                updateBlock();
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            }
        }
        if(level.getGameTime() % 20 == 0){
            SENSOR_MAP.addPosition(level, worldPosition);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("outputDuration", outputDuration);
        tag.putInt("outputStrength", outputStrength);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.outputDuration = pTag.getInt("outputDuration");
        this.outputStrength = pTag.getInt("outputStrength");
    }
}
