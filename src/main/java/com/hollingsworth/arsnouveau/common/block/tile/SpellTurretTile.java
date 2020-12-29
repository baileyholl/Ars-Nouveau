package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import com.hollingsworth.arsnouveau.api.spell.IPlaceBlockResponder;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SpellTurretTile extends TileEntity  implements IPickupResponder, IPlaceBlockResponder, ITooltipProvider {
    public List<AbstractSpellPart> recipe;

    public SpellTurretTile() {
        super(BlockRegistry.SPELL_TURRET_TYPE);
    }

    @Override
    public ItemStack onPickup(ItemStack stack) {
        return BlockUtil.insertItemAdjacent(world, pos, stack);
    }

    @Override
    public ItemStack onPlaceBlock() {
        return BlockUtil.getItemAdjacent(world, pos, (stack) -> stack.getItem() instanceof BlockItem);
    }

    @Override
    public List<IItemHandler> getInventory() {
        return BlockUtil.getAdjacentInventories(world, pos);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        if(recipe != null)
            tag.putString("spell", SpellRecipeUtil.serializeForNBT(recipe));

        return super.write(tag);
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        this.recipe = SpellRecipeUtil.getSpellsFromTagString(tag.getString("spell"));
        super.read(state, tag);
    }

    @Override
    public List<String> getTooltip() {
        if(this.recipe == null || this.recipe.isEmpty())
            return new ArrayList<>();
        List<String> list = new ArrayList<>();
        list.add("Casting: " + SpellRecipeUtil.getDisplayString(recipe));
        return list;
    }
    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(world.getBlockState(pos), pkt.getNbtCompound());
    }
}
