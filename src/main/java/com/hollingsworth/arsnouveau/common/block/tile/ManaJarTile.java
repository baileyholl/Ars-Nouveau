package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.ManaJar;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;

public class ManaJarTile extends AbstractManaTile implements TickableBlockEntity, ITooltipProvider {

    public ManaJarTile() {
        super(BlockRegistry.MANA_JAR_TILE);
    }

    public ManaJarTile(BlockEntityType<? extends ManaJarTile> tileTileEntityType){
        super(tileTileEntityType);
    }

    @Override
    public int getMaxMana() {
        return 10000;
    }

    @Override
    public void tick() {
        if(level.isClientSide) {
            // world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
            return;
        }
        BlockState state = level.getBlockState(worldPosition);
        int fillState = 0;
        if(this.getCurrentMana() > 0 && this.getCurrentMana() < 1000)
            fillState = 1;
        else if(this.getCurrentMana() != 0){
            fillState = (this.getCurrentMana() / 1000) + 1;
        }

        level.setBlock(worldPosition, state.setValue(ManaJar.fill, fillState),3);
    }


    @Override
    public int getTransferRate() {
        return getMaxMana();
    }

    @Override
    public List<String> getTooltip() {
        List<String> list = new ArrayList<>();
        list.add(new TranslatableComponent("ars_nouveau.mana_jar.fullness", (getCurrentMana()*100) / this.getMaxMana()).getString());
        return list;
    }
}
