package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.SpellTurretTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class SpellTurret extends BasicSpellTurret {

    public SpellTurret(Properties properties, String registry) {
        super(properties, registry);
    }

    public SpellTurret() {
        super(defaultProperties().noOcclusion(), LibBlockNames.SPELL_TURRET);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SpellTurretTile();
    }

}
