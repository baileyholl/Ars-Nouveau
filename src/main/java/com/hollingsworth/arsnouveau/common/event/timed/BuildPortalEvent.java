package com.hollingsworth.arsnouveau.common.event.timed;

import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.PortalBlock;
import com.hollingsworth.arsnouveau.common.block.tile.PortalTile;
import com.hollingsworth.arsnouveau.common.block.tile.TemporaryTile;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildPortalEvent implements ITimedEvent {

    Level level;
    BlockPos targetPos;
    Direction direction;
    List<BlockPos> framePos = new ArrayList<>();
    List<BlockPos> portalPos = new ArrayList<>();
    int ticks;
    List<BlockPos> placedBlocks = new ArrayList<>();
    WarpScroll.WarpScrollData warpScrollData;

    public BuildPortalEvent(Level level, BlockPos targetPos, Direction direction, WarpScroll.WarpScrollData warpScrollData) {
        this.level = level;
        this.targetPos = targetPos;
        this.direction = direction;
        ticks = 1;
        this.warpScrollData = warpScrollData;
        BlockPos aboveTarget = targetPos.above();
        int width = 2;
        BlockPos leftBotTarget = aboveTarget.offset(width * direction.getStepX(), 0, width * direction.getStepZ());
        BlockPos rightBotTarget = aboveTarget.offset(width * -direction.getStepX(), 0, width * -direction.getStepZ());
        // bot
        for(BlockPos pos1 : BlockPos.betweenClosed(leftBotTarget, rightBotTarget)){
            framePos.add(pos1.immutable());
        }
        // side
        for(BlockPos pos1 : BlockPos.betweenClosed(rightBotTarget.above(), rightBotTarget.above(3))){
            framePos.add(pos1.immutable());
        }

        // top
        for(BlockPos pos1 : BlockPos.betweenClosed(leftBotTarget.above(4), rightBotTarget.above(4))){
            framePos.add(pos1.immutable());
        }

        for(BlockPos pos1 : BlockPos.betweenClosed(leftBotTarget.above(), leftBotTarget.above(3))){
            framePos.add(pos1.immutable());
        }

        BlockPos leftPortalOffset = aboveTarget.offset(direction.getStepX(), 1, direction.getStepZ());
        BlockPos rightPortalOffset = aboveTarget.offset(-direction.getStepX(), 3, -direction.getStepZ());
        for(BlockPos pos1 : BlockPos.betweenClosed(leftPortalOffset, rightPortalOffset)){
            portalPos.add(pos1.immutable());
        }
        Collections.shuffle(portalPos);
    }

    @Override
    public void tick(boolean serverSide) {
        ticks++;
        if(ticks < 5)
            return;
        if(!serverSide || level.getGameTime() % 3 != 0)
            return;

        boolean destroyPortal = false;
        boolean placingFrame = !framePos.isEmpty();
        if(placingFrame){
            BlockPos pos = framePos.get(0);
            framePos.remove(pos);
            BlockState bs = level.getBlockState(pos);
            if (bs.is(BlockTagProvider.DECORATIVE_AN)) {
                tick(true);
                return;
            }
            if(bs.canBeReplaced()) {
                level.setBlock(pos, BlockRegistry.TEMPORARY_BLOCK.get().defaultBlockState(), 3);
                if(level.getBlockEntity(pos) instanceof TemporaryTile tile){
                    tile.mimicState = BlockRegistry.getBlock(LibBlockNames.SOURCESTONE).defaultBlockState();
                    tile.tickDuration = 20 * 60;
                    tile.gameTime = level.getGameTime();
                    tile.updateBlock();
                }
                level.playSound(null, pos, BlockRegistry.getBlock(LibBlockNames.SOURCESTONE).getSoundType(level.getBlockState(pos)).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                ParticleUtil.spawnTouchPacket(level, pos, ParticleColor.makeRandomColor(255, 255, 255, level.random));
                placedBlocks.add(pos);
                return;
            }else{
                destroyPortal = true;
                ServerLevel serverLevel = (ServerLevel) level;
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
            }
        }
        boolean placingPortal = !portalPos.isEmpty() && framePos.isEmpty() && !destroyPortal;
        if(placingPortal){
            for(BlockPos pos : portalPos) {
                if (level.getBlockState(pos).canBeReplaced()) {
                    level.setBlock(pos, BlockRegistry.PORTAL_BLOCK.defaultBlockState().setValue(PortalBlock.AXIS, direction.getAxis()), 2);
                    level.playSound(null, pos, BlockRegistry.PORTAL_BLOCK.get().getSoundType(level.getBlockState(pos)).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    placedBlocks.add(pos);
                } else {
                    destroyPortal = true;
                    ServerLevel serverLevel = (ServerLevel) level;
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
                    break;
                }
            }
            portalPos.clear();
        }
        if(destroyPortal){
            for(BlockPos pos : placedBlocks){
                level.destroyBlock(pos, false);
            }
            level.playSound(null, targetPos, SoundRegistry.GAIA_SPELL_SOUND.getSoundEvent(), SoundSource.BLOCKS, 1.0F, 1.0F);
            portalPos.clear();
            framePos.clear();
            return;
        }
        if(portalPos.isEmpty()){
            level.playSound(null, targetPos.above(2), SoundRegistry.TEMPESTRY_SPELL_SOUND.getSoundEvent(), SoundSource.BLOCKS, 1.0F, 1.0F);
            for(BlockPos pos : placedBlocks){
                if(level.getBlockEntity(pos) instanceof PortalTile portalTile){
                    portalTile.setFromScroll(warpScrollData);
                }
            }
        }
    }

    @Override
    public boolean isExpired() {
        return portalPos.isEmpty() && framePos.isEmpty();
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        return ITimedEvent.super.serialize(tag);
    }
}
