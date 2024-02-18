package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.perk.StackPerkHolder;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.block.AlterationTable;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.ThreePartBlock;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        IPerkHolder<ItemStack> holder = PerkUtil.getPerkHolder(stack);
        if (holder instanceof StackPerkHolder armorPerkHolder) {
            this.perkList = new ArrayList<>(PerkUtil.getPerksAsItems(stack).stream().map(Item::getDefaultInstance).toList());
            armorPerkHolder.setPerks(new ArrayList<>());
            this.armorStack = stack.copy();
            stack.shrink(1);
            this.newPerkTimer = 0;
            updateBlock();
        }
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
        IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(armorStack);
        if (perkHolder instanceof StackPerkHolder armorPerkHolder) {
            armorPerkHolder.setPerks(perkList.stream().map(i ->{
                if(i.getItem() instanceof PerkItem perkItem){
                    return perkItem.perk;
                }
                return null;
            }).filter(Objects::nonNull).toList());
        }
        if(!player.addItem(armorStack.copy())){
            level.addFreshEntity(new ItemEntity(level, player.position().x(), player.position().y(), player.position().z(), armorStack.copy()));
        }
        this.armorStack = ItemStack.EMPTY;
        this.perkList = new ArrayList<>();
        updateBlock();
    }

    public void addPerkStack(ItemStack stack, Player player){
        IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(armorStack);
        if (!(perkHolder instanceof StackPerkHolder armorPerkHolder)) {
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.perk.set_armor"));
            return;
        }
        if(this.perkList.size() >= 3 || this.perkList.size() >= armorPerkHolder.getSlotsForTier().size()){
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

    private PerkSlot getAvailableSlot(IPerkHolder<ItemStack> perkHolder){
        if(this.perkList.size() >= perkHolder.getSlotsForTier().size()){
            return null;
        }else{
            return perkHolder.getSlotsForTier().get(this.perkList.size());
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
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag armorTag = new CompoundTag();
        armorStack.save(armorTag);
        tag.put("armorStack", armorTag);
        tag.putInt("numPerks", perkList.size());
        int count = 0;
        for(ItemStack i : perkList){
            CompoundTag perkTag = new CompoundTag();
            i.save(perkTag);
            tag.put("perk" + count, perkTag);
            count++;
        }
        tag.putInt("newPerkTimer", newPerkTimer);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.armorStack = ItemStack.of(compound.getCompound("armorStack"));
        int count = compound.getInt("numPerks");
        perkList = new ArrayList<>();
        for(int i = 0; i < count; i++){
            CompoundTag perkTag = compound.getCompound("perk" + i);
            ItemStack perk = ItemStack.of(perkTag);
            perkList.add(perk);
        }
        this.newPerkTimer = compound.getInt("newPerkTimer");
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(2);
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
