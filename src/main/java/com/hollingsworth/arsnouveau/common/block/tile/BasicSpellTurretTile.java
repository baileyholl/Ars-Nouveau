package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import java.util.List;

public class BasicSpellTurretTile extends ModdedTile implements IPickupResponder, IPlaceBlockResponder, ITooltipProvider, IAnimatable, IAnimationListener, ITickable, ISpellCasterProvider {

    boolean playRecoil;
    public TurretSpellCaster spellCaster = new TurretSpellCaster(new CompoundTag());

    public BasicSpellTurretTile(BlockEntityType<?> p_i48289_1_, BlockPos pos, BlockState state) {
        super(p_i48289_1_, pos, state);
    }

    public BasicSpellTurretTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.BASIC_SPELL_TURRET_TILE, pos, state);
    }

    public int getManaCost() {
        return this.spellCaster.getSpell().getDiscountedCost();
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
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        spellCaster.serializeOnTag(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        this.spellCaster = new TurretSpellCaster(tag);
        super.load(tag);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        tooltip.add(Component.literal(Component.translatable("ars_nouveau.spell_turret.casting").getString() + spellCaster.getSpell().getDisplayString()));
    }

    public PlayState walkPredicate(AnimationEvent event) {
        if (playRecoil) {
            event.getController().clearAnimationCache();
            event.getController().setAnimation(new AnimationBuilder().addAnimation("recoil", false));
            playRecoil = false;
        }
        return PlayState.CONTINUE;
    }

    AnimationController castController;

    @Override
    public void registerControllers(AnimationData data) {
        castController = new AnimationController<>(this, "castController", 0, this::walkPredicate);
        data.addAnimationController(castController);
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

    @Override
    public ISpellCaster getSpellCaster() {
        return spellCaster;
    }

    @Override
    public ISpellCaster getSpellCaster(CompoundTag tag) {
        return spellCaster;
    }
}
