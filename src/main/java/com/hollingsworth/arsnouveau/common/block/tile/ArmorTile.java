package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkProvider;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ArmorTile extends SingleItemTile implements IAnimatable, ITickable, Container {


    public boolean isCrafting;

    public ArmorTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public ArmorTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARMOR_TILE, pos, state);

    }

    public void addPerks(Player player, ItemStack stack){
        IPerkProvider<ItemStack> holder = ArsNouveauAPI.getInstance().getPerkProvider(stack.getItem());

        if(holder == null){
            return;
        }
        IPerkHolder<ItemStack> perkHolder = holder.getPerkHolder(stack);

        if(!perkHolder.isEmpty()){
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.perk.must_be_empty"));
            return;
        }
        List<BlockPos> pedestals = new ArrayList<>();
        for(BlockPos p : BlockPos.withinManhattan(getBlockPos(), 4, 4, 3)){
            if(level.getBlockEntity(p) instanceof ArcanePedestalTile pedestalTile){
                if(pedestalTile.getStack().getItem() instanceof PerkItem)
                    pedestals.add(p.immutable());
            }
        }
        int perkLevels = 0;
        for(BlockPos p : pedestals){
            if(level.getBlockEntity(p) instanceof ArcanePedestalTile pedestalTile){
                PerkItem perkItem = (PerkItem)pedestalTile.getStack().getItem();
                perkLevels += perkItem.perk.getSlotCost();
            }
        }
        if(perkLevels > perkHolder.getMaxSlots()){
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.perk.not_enough_slots", perkLevels, perkHolder.getMaxSlots()));
            return;
        }
        for(BlockPos pos : pedestals){
            if(level.getBlockEntity(pos) instanceof ArcanePedestalTile pedestalTile){
                PerkItem perkItem = (PerkItem)pedestalTile.getStack().getItem();
                perkHolder.getPerkSet().addPerk(perkItem.perk, 1);
                pedestalTile.setStack(ItemStack.EMPTY);
            }
        }
    }

    public void removePerks(ItemStack stack){
        IPerkProvider<ItemStack> holder = ArsNouveauAPI.getInstance().getPerkProvider(stack.getItem());

        if(holder == null){
            return;
        }
        IPerkHolder<ItemStack> perkHolder = holder.getPerkHolder(stack);
        ArsNouveauAPI api = ArsNouveauAPI.getInstance();
        for(Map.Entry<IPerk, Integer> perk : perkHolder.getPerkSet().getPerkMap().entrySet()){
            for(int i = 0; i < perk.getValue(); i++) {
                ItemStack perkStack = new ItemStack(api.getPerkItemMap().get(perk.getKey().getRegistryName()));
                Optional<BlockPos> pedestalPos = BlockPos.findClosestMatch(getBlockPos(), 4, 4, (p) -> level.getBlockEntity(p) instanceof ArcanePedestalTile tile && tile.getStack().isEmpty());
                if (pedestalPos.isEmpty()) {
                    BlockPos pos = getBlockPos();
                    ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, perkStack);
                    level.addFreshEntity(itemEntity);
                    continue;
                }
                pedestalPos.ifPresent(pos -> {
                    ArcanePedestalTile tile = (ArcanePedestalTile) level.getBlockEntity(pos);
                    tile.setStack(perkStack.split(1));
                });
            }
        }

    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    public AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public ItemStack getItem(int index) {
        if (isCrafting)
            return ItemStack.EMPTY;
        return super.getItem(index);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack newStack) {
        if (isCrafting || newStack.isEmpty())
            return false;
        return this.stack.isEmpty();
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if (isCrafting)
            return ItemStack.EMPTY;
        return super.removeItem(index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if (isCrafting)
            return ItemStack.EMPTY;
        return super.removeItemNoUpdate(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (isCrafting)
            return;
        super.setItem(index, stack);
    }
}
