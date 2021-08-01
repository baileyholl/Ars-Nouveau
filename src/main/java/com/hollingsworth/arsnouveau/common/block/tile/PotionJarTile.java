package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.common.block.ManaJar;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PotionJarTile extends TileEntity implements ITickableTileEntity, ITooltipProvider, IWandable {

    private int amount;
    private Potion potion = Potions.EMPTY;
    public boolean isLocked;
    private List<EffectInstance> customEffects = new ArrayList<>();
    public PotionJarTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public PotionJarTile() {
        super(BlockRegistry.POTION_JAR_TYPE);
    }

    @Override
    public void tick() {
        if(level.isClientSide) {
            // world.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
            return;
        }
        BlockState state = level.getBlockState(worldPosition);
        int fillState = 0;
        if(this.getCurrentFill() > 0 && this.getCurrentFill() < 1000)
            fillState = 1;
        else if(this.getCurrentFill() != 0){
            fillState = (this.getCurrentFill() / 1000) + 1;
        }


        if(level.getGameTime() % 20 == 0){
            if(this.getAmount() <= 0 && this.potion != Potions.EMPTY && !this.isLocked) {
                this.potion = Potions.EMPTY;
                this.customEffects = new ArrayList<>();
                level.setBlock(worldPosition, state.setValue(ManaJar.fill, fillState),3);
            }
        }



        level.setBlock(worldPosition, state.setValue(ManaJar.fill, fillState),3);
    }

    public boolean canAcceptNewPotion(){
        return this.amount <= 0 && !this.isLocked || this.potion == Potions.EMPTY;
    }

    @Override
    public void onWanded(PlayerEntity playerEntity) {
        if(!isLocked){
            this.isLocked = true;
            playerEntity.sendMessage(new TranslationTextComponent("ars_nouveau.locked"), Util.NIL_UUID);
        }else{
            this.isLocked = false;
            playerEntity.sendMessage(new TranslationTextComponent("ars_nouveau.unlocked"), Util.NIL_UUID);
        }

        BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
    }

    public void setPotion(Potion potion, List<EffectInstance> effectInstances){
        this.potion = potion == null ? Potions.EMPTY : potion;
        customEffects = new ArrayList<>();
        for (EffectInstance e : effectInstances) {
            if (!potion.getEffects().contains(e))
                customEffects.add(e);
        }
    }

    public void setPotion(ItemStack stack){
        setPotion(PotionUtils.getPotion(stack), PotionUtils.getMobEffects(stack));
    }

    private void setPotion(Potion potion){
        this.potion = potion == null ? Potions.EMPTY : potion;
    }

    public @Nonnull Potion getPotion(){
        return potion == null ? Potions.EMPTY : potion;
    }

    public int getColor(){
        return potion == null ? 16253176 : PotionUtils.getColor(getFullEffects());
    }

    public int getCurrentFill(){
        return getAmount();
    }

    public void setFill(int fill){
        setAmount(fill);
    }

    public void addAmount(Potion potion, int fill){
        setPotion(potion);
        addAmount(fill);
    }

    public void addAmount(int fill){
        setAmount(Math.min(getMaxFill(), getAmount() + fill));
        if(getAmount() <= 0)
            this.potion = Potions.EMPTY;
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
    }

    @Override
    public List<String> getTooltip() {
        List<String> list = new ArrayList<>();
        if(this.potion != null && this.potion != Potions.EMPTY) {
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(potionStack, potion);
            list.add(potionStack.getHoverName().getString());
            PotionUtils.setCustomEffects(potionStack, customEffects);
            List<ITextComponent> tooltip = new ArrayList<>();
            PotionUtils.addPotionTooltip(potionStack, tooltip, 1.0F);
            for(ITextComponent i : tooltip){
                list.add(i.getString());
            }

        }
        list.add(new TranslationTextComponent("ars_nouveau.mana_jar.fullness", (getCurrentFill()*100) / this.getMaxFill()).getString());
        if(isLocked)
            list.add(new TranslationTextComponent("ars_nouveau.locked").getString());

        return list;
    }

    public void appendEffect(List<EffectInstance> effects){
        this.customEffects.addAll(effects);
    }

    public void setCustomEffects(List<EffectInstance> effects){
        this.customEffects.clear();
        this.customEffects.addAll(effects);
    }

    public List<EffectInstance> getFullEffects(){
        List<EffectInstance> thisEffects = getCustomEffects();
        thisEffects.addAll(potion.getEffects());
        return thisEffects;
    }

    public List<EffectInstance> getCustomEffects(){
        List<EffectInstance> thisEffects = new ArrayList<>(customEffects);
        return thisEffects;
    }

    //If the effect list of jars or flasks are equal
    public boolean isMixEqual(List<EffectInstance> effects){

        List<EffectInstance> thisEffects = new ArrayList<>(customEffects);
        thisEffects.addAll(potion.getEffects());
        effects = new ArrayList<>(effects);
        if(thisEffects.size() != effects.size())
            return false;
        effects.sort(Comparator.comparing(EffectInstance::toString));
        thisEffects.sort(Comparator.comparing(EffectInstance::toString));
        return thisEffects.equals(effects);
    }

    //If the effect list of jars or flasks are equal
    public boolean isMixEqual(Potion potion){
        return isMixEqual(potion.getEffects());
    }

    public boolean isMixEqual(ItemStack stack){
        return isMixEqual(PotionUtils.getMobEffects(stack));
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.amount = tag.getInt("amount");
        this.potion = PotionUtils.getPotion(tag);
        this.customEffects = new ArrayList<>();
        this.customEffects.addAll(PotionUtils.getCustomEffects(tag));
        this.isLocked = tag.getBoolean("locked");
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        ResourceLocation resourcelocation = Registry.POTION.getKey(potion);
        tag.putInt("amount", this.getAmount());
        tag.putString("Potion", resourcelocation.toString());
        tag.putBoolean("locked", isLocked);
        if(!customEffects.isEmpty()) {
            ListNBT listnbt = new ListNBT();

            for (EffectInstance effectinstance : customEffects) {
                listnbt.add(effectinstance.save(new CompoundNBT()));
            }

            tag.put("CustomPotionEffects", listnbt);
        }
        return super.save(tag);
    }

    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(level.getBlockState(worldPosition),pkt.getTag());
    }

    public int getMaxFill() {
        return 10000;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
    }
}

