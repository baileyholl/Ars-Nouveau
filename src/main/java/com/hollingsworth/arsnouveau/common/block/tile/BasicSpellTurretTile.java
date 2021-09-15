package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import com.hollingsworth.arsnouveau.api.spell.IPlaceBlockResponder;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.IItemHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BasicSpellTurretTile extends TileEntity implements IPickupResponder, IPlaceBlockResponder, ITooltipProvider, IAnimatable, IAnimationListener {
    public @Nonnull Spell spell = Spell.EMPTY;
    boolean playRecoil;
    public BasicSpellTurretTile(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    public BasicSpellTurretTile(){
        super(BlockRegistry.BASIC_SPELL_TURRET_TILE);
    }

    public int getManaCost(){
        return this.spell.getCastingCost();
    }

    @Override
    public @Nonnull ItemStack onPickup(ItemStack stack) {
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
        tag.putString("spell", spell.serialize());
        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        this.spell = Spell.deserialize(tag.getString("spell"));
        super.load(state, tag);
    }

    @Override
    public List<String> getTooltip() {
        List<String> list = new ArrayList<>();
        list.add(new TranslationTextComponent("ars_nouveau.spell_turret.casting").getString() + spell.getDisplayString());
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

    public PlayState walkPredicate(AnimationEvent event) {
        if(playRecoil){
            event.getController().clearAnimationCache();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("recoil", false));
            playRecoil = false;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        AnimationController controller = new AnimationController<>(this, "castController", 0, this::walkPredicate);
        data.addAnimationController(controller);
    }

    AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void startAnimation(int arg) {
        this.playRecoil = true;
    }

    public boolean update(){
        if(this.worldPosition != null && this.level != null){
            level.sendBlockUpdated(this.worldPosition, level.getBlockState(worldPosition),  level.getBlockState(worldPosition), 2);
            return true;
        }
        return false;
    }

}
