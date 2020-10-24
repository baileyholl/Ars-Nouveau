package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import com.hollingsworth.arsnouveau.api.spell.IPlaceBlockResponder;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

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
        for(IInventory i : inventories()){
            if(stack == ItemStack.EMPTY || stack == null)
                break;
            stack = HopperTileEntity.putStackInInventoryAllSlots(null, i, stack, null);
        }
        return stack;
    }

    @Override
    public ItemStack onPlaceBlock() {
        for(IInventory inv : inventories()){
            for(int i = 0; i < inv.getSizeInventory(); ++i) {
                if(inv.getStackInSlot(i).getItem() instanceof BlockItem)
                    return inv.getStackInSlot(i);
            }
        }
        return null;
    }

    public List<IInventory> inventories(){
        if(world == null)return new ArrayList<>();
        ArrayList<IInventory> iInventories = new ArrayList<>();
        for(Direction d : Direction.values()){
            IInventory iInventory =  HopperTileEntity.getInventoryAtPosition(world, pos.offset(d));
            if(iInventory != null)
                iInventories.add(iInventory);
        }

        return iInventories;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        if(recipe != null)
            tag.putString("spell", SpellRecipeUtil.serializeForNBT(recipe));

        return super.write(tag);
    }

    @Override
    public void read(CompoundNBT tag) {
        this.recipe = SpellRecipeUtil.getSpellsFromTagString(tag.getString("spell"));
        super.read(tag);
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
        handleUpdateTag(pkt.getNbtCompound());
    }
}
