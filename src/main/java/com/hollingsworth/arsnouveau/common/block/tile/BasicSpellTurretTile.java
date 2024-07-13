package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class BasicSpellTurretTile extends ModdedTile implements ITooltipProvider, GeoBlockEntity, IAnimationListener, ITickable,  ICapabilityProvider<BasicSpellTurretTile, Void, SpellCaster> {

    boolean playRecoil;
    public SpellCaster spellCaster = new SpellCaster(0, null, false, null, 1);

    public BasicSpellTurretTile(BlockEntityType<?> p_i48289_1_, BlockPos pos, BlockState state) {
        super(p_i48289_1_, pos, state);
    }

    public BasicSpellTurretTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.BASIC_SPELL_TURRET_TILE, pos, state);
    }

    public int getManaCost() {
        return this.spellCaster.getSpell().getCost();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("spell_caster", ANCodecs.encode(SpellCaster.CODEC.codec(), spellCaster));
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.spellCaster = ANCodecs.decode(SpellCaster.CODEC.codec(), pTag.get("spell_caster"));
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("ars_nouveau.spell_turret.casting"));
        if (!spellCaster.getSpellName().isEmpty()) tooltip.add(Component.literal(spellCaster.getSpellName()));
        tooltip.add(Component.literal(spellCaster.getSpell().getDisplayString()));
    }

    public PlayState walkPredicate(AnimationState<?> event) {
        if (playRecoil) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(RawAnimation.begin().thenPlay("recoil"));
            playRecoil = false;
        }
        return PlayState.CONTINUE;
    }

    AnimationController castController;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        castController = new AnimationController<>(this, "castController", 0, this::walkPredicate);
        data.add(castController);
    }

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void startAnimation(int arg) {
        this.playRecoil = true;
    }

    @Override
    public @Nullable SpellCaster getCapability(BasicSpellTurretTile object, Void context) {
        return spellCaster;
    }
}
