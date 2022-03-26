package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.SourceJar;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PotionJarTile extends ModdedTile implements ITickable, ITooltipProvider, IWandable {

    private int amount;
    private Potion potion = Potions.EMPTY;
    public boolean isLocked;
    private List<MobEffectInstance> customEffects = new ArrayList<>();

    public PotionJarTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public PotionJarTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.POTION_JAR_TYPE, pos, state);
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
                level.setBlock(worldPosition, state.setValue(SourceJar.fill, fillState),3);
            }
        }



        level.setBlock(worldPosition, state.setValue(SourceJar.fill, fillState),3);
    }

    public boolean canAcceptNewPotion(){
        return this.amount <= 0 && !this.isLocked || (this.potion == Potions.EMPTY && !this.isLocked);
    }

    @Override
    public void onWanded(Player playerEntity) {
        if(!isLocked){
            this.isLocked = true;
            playerEntity.sendMessage(new TranslatableComponent("ars_nouveau.locked"), Util.NIL_UUID);
        }else{
            this.isLocked = false;
            playerEntity.sendMessage(new TranslatableComponent("ars_nouveau.unlocked"), Util.NIL_UUID);
        }

        BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
    }

    public void setPotion(Potion potion, List<MobEffectInstance> effectInstances){
        this.potion = potion == null ? Potions.EMPTY : potion;
        customEffects = new ArrayList<>();
        for (MobEffectInstance e : effectInstances) {
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
        if(getAmount() <= 0 && !this.isLocked)
            this.potion = Potions.EMPTY;
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if(this.potion != null && this.potion != Potions.EMPTY) {
            ItemStack potionStack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(potionStack, potion);
            tooltip.add(potionStack.getHoverName());
            PotionUtils.setCustomEffects(potionStack, customEffects);

            PotionUtils.addPotionTooltip(potionStack, tooltip, 1.0F);
        }
        tooltip.add(new TranslatableComponent("ars_nouveau.source_jar.fullness", (getCurrentFill()*100) / this.getMaxFill()));
        if(isLocked)
            tooltip.add(new TranslatableComponent("ars_nouveau.locked"));
    }

    public void appendEffect(List<MobEffectInstance> effects){
        this.customEffects.addAll(effects);
    }

    public void setCustomEffects(List<MobEffectInstance> effects){
        this.customEffects.clear();
        this.customEffects.addAll(effects);
    }

    public List<MobEffectInstance> getFullEffects(){
        List<MobEffectInstance> thisEffects = getCustomEffects();
        thisEffects.addAll(potion.getEffects());
        return thisEffects;
    }

    public List<MobEffectInstance> getCustomEffects(){
        return new ArrayList<>(customEffects);
    }

    //If the effect list of jars or flasks are equal
    public boolean isMixEqual(List<MobEffectInstance> effects){

        List<MobEffectInstance> thisEffects = new ArrayList<>(customEffects);
        thisEffects.addAll(potion.getEffects());
        effects = new ArrayList<>(effects);
        if(thisEffects.size() != effects.size())
            return false;
        effects.sort(Comparator.comparing(MobEffectInstance::toString));
        thisEffects.sort(Comparator.comparing(MobEffectInstance::toString));
        return thisEffects.equals(effects);
    }

    //If the effect list of jars or flasks are equal
    public boolean isMixEqual(Potion potion){
        if(potion.getEffects().isEmpty() && this.potion.getEffects().isEmpty()){
            return potion == this.potion;
        }
        return isMixEqual(potion.getEffects());
    }

    public boolean isMixEqual(ItemStack stack){
        // Checking for same effect sets is not sufficient for potions that have no effects, like water and awkward.
        if(PotionUtils.getMobEffects(stack).isEmpty() && potion.getEffects().isEmpty()){
            return PotionUtils.getPotion(stack) == potion;
        }
        return isMixEqual(PotionUtils.getMobEffects(stack));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.amount = tag.getInt("amount");
        this.potion = PotionUtils.getPotion(tag);
        this.customEffects = new ArrayList<>();
        this.customEffects.addAll(PotionUtils.getCustomEffects(tag));
        this.isLocked = tag.getBoolean("locked");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        ResourceLocation resourcelocation = Registry.POTION.getKey(potion);
        tag.putInt("amount", this.getAmount());
        tag.putString("Potion", resourcelocation.toString());
        tag.putBoolean("locked", isLocked);
        if(!customEffects.isEmpty()) {
            ListTag listnbt = new ListTag();

            for (MobEffectInstance effectinstance : customEffects) {
                listnbt.add(effectinstance.save(new CompoundTag()));
            }

            tag.put("CustomPotionEffects", listnbt);
        }
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

