package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.common.block.ManaJar;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class ManaJarTile extends AbstractManaTile implements ITickableTileEntity, ITooltipProvider {

    public ManaJarTile() {
        super(BlockRegistry.MANA_JAR_TILE);
    }

    public ManaJarTile(TileEntityType<? extends ManaJarTile> tileTileEntityType){
        super(tileTileEntityType);
    }

    @Override
    public int getMaxMana() {
        return 10000;
    }

    @Override
    public void tick() {
        if(world.isRemote) {
            // world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
            return;
        }
        BlockState state = world.getBlockState(pos);
        int fillState = 0;
        if(this.getCurrentMana() > 0 && this.getCurrentMana() < 1000)
            fillState = 1;
        else if(this.getCurrentMana() != 0){
            fillState = (this.getCurrentMana() / 1000) + 1;
        }

        world.setBlockState(pos, state.with(ManaJar.fill, fillState),3);
    }


    @Override
    public int getTransferRate() {
        return getMaxMana();
    }

    @Override
    public List<String> getTooltip() {
        List<String> list = new ArrayList<>();
        list.add(new TranslationTextComponent("ars_nouveau.mana_jar.fullness", (getCurrentMana()*100) / this.getMaxMana()).getString());
        return list;
    }
}
