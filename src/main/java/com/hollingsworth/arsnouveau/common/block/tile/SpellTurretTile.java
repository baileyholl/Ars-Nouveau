package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import com.hollingsworth.arsnouveau.api.spell.IPlaceBlockResponder;
import com.hollingsworth.arsnouveau.api.spell.Spell;
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
import net.minecraft.util.text.TranslationTextComponent;
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
        return BlockUtil.insertItemAdjacent(level, worldPosition, stack);
    }

    @Override
    public ItemStack onPlaceBlock() {
        return BlockUtil.getItemAdjacent(level, worldPosition, (stack) -> stack.getItem() instanceof BlockItem);
    }

    @Override
    public List<IItemHandler> getInventory() {
        return BlockUtil.getAdjacentInventories(level, worldPosition);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        if(recipe != null)
            tag.putString("spell", SpellRecipeUtil.serializeForNBT(recipe));

        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        this.recipe = SpellRecipeUtil.getSpellsFromTagString(tag.getString("spell"));
        super.load(state, tag);
    }

    @Override
    public List<String> getTooltip() {
        if(this.recipe == null || this.recipe.isEmpty())
            return new ArrayList<>();
        List<String> list = new ArrayList<>();
        list.add(new TranslationTextComponent("ars_nouveau.spell_turret.casting").getString() + new Spell(recipe).getDisplayString());
        return list;
    }
    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(level.getBlockState(worldPosition), pkt.getTag());
    }
}
