package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkProvider;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.*;
import java.util.stream.Collectors;

public class AlterationTile extends SingleItemTile implements IAnimatable, ITickable, Container, IAnimationListener {


    public boolean isCrafting;
    int ticksCrafting;
    boolean isAddPerk;
    boolean playCrunch;
    List<BlockPos> destinationList = new ArrayList<>();

    public AlterationTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public AlterationTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARMOR_TILE, pos, state);
    }

    @Override
    public void tick() {
        if(!isCrafting || level.isClientSide){
            return;
        }
        ticksCrafting++;
        IPerkHolder<ItemStack> holder = PerkUtil.getPerkHolder(stack);
        if(holder == null || (isAddPerk && isAddingDone()) || (!isAddPerk && isRemovingDone(holder))){
            isCrafting = false;
            updateBlock();
            return;
        }
        if(ticksCrafting % 20 == 0){
            if(isAddPerk){
                addOnePerk(holder);
            }else{
                removeOnePerk(holder);
            }
        }
    }

    private boolean isAddingDone(){
        return destinationList.isEmpty();
    }

    private boolean isRemovingDone(IPerkHolder<ItemStack> holder){
        return holder.isEmpty();
    }

    private void addOnePerk(IPerkHolder<ItemStack> perkHolder){
        BlockPos pos = destinationList.get(0);
        if(level.getBlockEntity(pos) instanceof ArcanePedestalTile pedestalTile){
            Networking.sendToNearby(level, getBlockPos(), new PacketOneShotAnimation(getBlockPos(), 0));
            PerkItem perkItem = (PerkItem)pedestalTile.getStack().getItem();
            perkHolder.getPerkSet().addPerk(perkItem.perk, 1);
            pedestalTile.setStack(ItemStack.EMPTY);
            if(level instanceof ServerLevel serverLevel) {
                ParticleUtil.spawnPoof(serverLevel, pos);
            }
        }
        destinationList.remove(0);
    }

    private void removeOnePerk(IPerkHolder<ItemStack> perkHolder){
        ArsNouveauAPI api = ArsNouveauAPI.getInstance();
        Set<Map.Entry<IPerk, Integer>> perks = perkHolder.getPerkSet().getPerkMap().entrySet();
        if(perks.isEmpty())
            return;
        IPerk perk = perks.stream().collect(Collectors.toList()).get(0).getKey();
        ItemStack perkStack = new ItemStack(api.getPerkItemMap().get(perk.getRegistryName()));
        Optional<BlockPos> pedestalPos = BlockPos.findClosestMatch(getBlockPos(), 4, 4, (p) -> level.getBlockEntity(p) instanceof ArcanePedestalTile tile && tile.getStack().isEmpty());
        if (pedestalPos.isEmpty()) {
            BlockPos pos = getBlockPos();
            ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, perkStack);
            level.addFreshEntity(itemEntity);
            perkHolder.getPerkSet().shrinkPerk(perk);
            if(level instanceof ServerLevel level){
                ParticleUtil.spawnPoof(level, pos);
            }
            Networking.sendToNearby(level, getBlockPos(), new PacketOneShotAnimation(getBlockPos(), 0));
            return;
        }
        pedestalPos.ifPresent(pos -> {
            ArcanePedestalTile tile = (ArcanePedestalTile) level.getBlockEntity(pos);
            tile.setStack(perkStack.split(1));
            perkHolder.getPerkSet().shrinkPerk(perk);
            Networking.sendToNearby(level, getBlockPos(), new PacketOneShotAnimation(getBlockPos(), 0));
            if(level instanceof ServerLevel level){
                ParticleUtil.spawnPoof(level, pos);
            }
        });

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
        isCrafting = true;
        destinationList = pedestals;
        isAddPerk = true;
        setStack(stack.split(1));
        updateBlock();
    }

    public void removePerks(Player player, ItemStack stack){
        IPerkHolder<ItemStack> holder = PerkUtil.getPerkHolder(stack);
        if(holder == null){
            return;
        }
        ArsNouveauAPI api = ArsNouveauAPI.getInstance();
        Set<Map.Entry<IPerk, Integer>> perks = holder.getPerkSet().getPerkMap().entrySet();
        if(perks.isEmpty()){
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.perk.no_perks"));
            return;
        }
        setStack(stack.split(1));
        isCrafting = true;
        isAddPerk = false;
        updateBlock();
    }
    AnimationController craftController;
    AnimationController idleController;
    AnimationController spinController;
    AnimationController fastSpinController;
    @Override
    public void registerControllers(AnimationData animationData) {
        idleController = new AnimationController(this, "controller", 0, this::idlePredicate);
        spinController = new AnimationController(this, "spinController", 10, this::spinPredicate);
        craftController = new AnimationController(this, "craft_controller", 0, this::craftPredicate);
        fastSpinController = new AnimationController(this, "fastSpinController", 10, this::fastSpinPredicate);
        animationData.addAnimationController(idleController);
        animationData.addAnimationController(spinController);
        animationData.addAnimationController(craftController);
        animationData.addAnimationController(fastSpinController);
        animationData.setResetSpeedInTicks(0.0);
    }

    private <E extends BlockEntity & IAnimatable> PlayState idlePredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("float", true));
        return PlayState.CONTINUE;
    }

    private <E extends BlockEntity & IAnimatable> PlayState spinPredicate(AnimationEvent<E> event) {
        if(!isCrafting){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("spin", true));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <E extends BlockEntity & IAnimatable> PlayState fastSpinPredicate(AnimationEvent<E> event) {
        if(isCrafting){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("spin_fast", true));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <E extends BlockEntity & IAnimatable> PlayState craftPredicate(AnimationEvent<E> event) {
        if (playCrunch) {
            event.getController().clearAnimationCache();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("cap_crunch", false));
            playCrunch = false;
        }
        return PlayState.CONTINUE;
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

    @Override
    public void startAnimation(int arg) {
        this.playCrunch = true;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isCrafting", isCrafting);
        tag.putInt("ticksCrafting", ticksCrafting);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        isCrafting = compound.getBoolean("isCrafting");
        ticksCrafting = compound.getInt("ticksCrafting");
    }
}
