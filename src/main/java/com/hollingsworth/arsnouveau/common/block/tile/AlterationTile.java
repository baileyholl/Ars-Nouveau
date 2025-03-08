package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.block.AlterationTable;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.ThreePartBlock;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.*;

public class AlterationTile extends ModdedTile implements GeoBlockEntity, ITickable {

    public ItemStack armorStack = ItemStack.EMPTY;
    public ItemEntity renderEntity;
    public List<ItemStack> perkList = new ArrayList<>();

    public int newPerkTimer = 0;

    public AlterationTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public AlterationTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARMOR_TILE, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {}

    public AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public @Nullable AlterationTile getLogicTile() {
        AlterationTile tile = this;
        if (!isMasterTile()) {
            BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(AlterationTable.getConnectedDirection(getBlockState())));
            tile = tileEntity instanceof AlterationTile alterationTile ? alterationTile : null;
        }
        return tile;
    }

    public boolean isMasterTile() {
        return getBlockState().getValue(AlterationTable.PART) == ThreePartBlock.HEAD;
    }

    public void setArmorStack(ItemStack stack, Player player){
        ArmorPerkHolder holder = stack.getOrDefault(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder(null, List.of(), 0, new HashMap<>()));
        this.perkList = new ArrayList<>(PerkUtil.getPerksAsItems(stack).stream().map(Item::getDefaultInstance).toList());
        stack.set(DataComponentRegistry.ARMOR_PERKS, holder.setPerks(new ArrayList<>()));
        this.armorStack = stack.copy();
        if (!player.hasInfiniteMaterials()) {
            stack.shrink(1);
        }
        this.newPerkTimer = 0;
        updateBlock();

    }

    public void removePerk(Player player) {
        if(!perkList.isEmpty()){
            ItemStack stack = perkList.get(0);
            if(!player.addItem(stack.copy())){
                level.addFreshEntity(new ItemEntity(level, player.position().x(), player.position().y(), player.position().z(), stack.copy()));
            }
            perkList.remove(0);
        }
        updateBlock();
    }

    public void removeArmorStack(Player player){
        ArmorPerkHolder perkHolder = PerkUtil.getPerkHolder(armorStack);
        Map<IPerk, CompoundTag> perkTags = new HashMap<>();
        var newHolder = perkHolder.setPerks(new ArrayList<>(perkList.stream().map(i ->{
            if(i.getItem() instanceof PerkItem perkItem){
                var perk = perkItem.perk;
                CompoundTag initTag = perk.getInitTag();
                if(initTag != null && perkHolder.getTagForPerk(perk) == null){
                    perkTags.put(perk, initTag);
                }
                return perk;
            }
            return null;
        }).filter(Objects::nonNull).toList()));
        var copyStack = armorStack.copy();
        copyStack.set(DataComponentRegistry.ARMOR_PERKS, newHolder.setPerkTags(perkTags));
        if(!player.addItem(copyStack)){
            level.addFreshEntity(new ItemEntity(level, player.position().x(), player.position().y(), player.position().z(), copyStack));
        }
        this.armorStack = ItemStack.EMPTY;
        this.perkList = new ArrayList<>();
        updateBlock();
    }

    public void addPerkStack(ItemStack stack, Player player){
        ArmorPerkHolder perkHolder = PerkUtil.getPerkHolder(armorStack);
        if (perkHolder == null) {
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.perk.set_armor"));
            return;
        }
        if(this.perkList.size() >= 3 || this.perkList.size() >= perkHolder.getSlotsForTier(armorStack).size()){
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.perk.max_perks"));
            return;
        }
        PerkSlot foundSlot = getAvailableSlot(perkHolder);
        if(stack.getItem() instanceof PerkItem perkItem) {
            IPerk perk = perkItem.perk;
            if (foundSlot != null && perk.validForSlot(foundSlot, armorStack, player)) {
                this.perkList.add(stack.split(1));
                if (newPerkTimer <= 0) {
                    newPerkTimer = 40;
                }
                updateBlock();
            }
        }
    }

    private PerkSlot getAvailableSlot(ArmorPerkHolder perkHolder){
        if(this.perkList.size() >= perkHolder.getSlotsForTier(armorStack).size()){
            return null;
        }else{
            return perkHolder.getSlotsForTier(armorStack).get(this.perkList.size());
        }
    }

    public void dropItems(){
        if(!armorStack.isEmpty()){
            level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), armorStack.copy()));
        }
        for(ItemStack stack : perkList){
            level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack.copy()));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        if(!armorStack.isEmpty()) {
            Tag armorTag = armorStack.save(pRegistries);
            tag.put("armorStack", armorTag);
        }
        tag.putInt("numPerks", perkList.size());
        int count = 0;
        for(ItemStack i : perkList){
            Tag perkTag = i.save(pRegistries);
            tag.put("perk" + count, perkTag);
            count++;
        }
        tag.putInt("newPerkTimer", newPerkTimer);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        this.armorStack = ItemStack.parseOptional(pRegistries, compound.getCompound("armorStack"));
        int count = compound.getInt("numPerks");
        perkList = new ArrayList<>();
        for(int i = 0; i < count; i++){
            CompoundTag perkTag = compound.getCompound("perk" + i);
            ItemStack perk = ItemStack.parseOptional(pRegistries, perkTag);
            perkList.add(perk);
        }
        this.newPerkTimer = compound.getInt("newPerkTimer");
    }

    @Override
    public void tick(Level level, BlockState state, BlockPos pos) {
        if (level.isClientSide) {
            if (newPerkTimer >= 0) {
                newPerkTimer--;
            }
        }
    }
}
