package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class BasicSpellTurretTile extends ModdedTile implements ITooltipProvider, GeoBlockEntity, IAnimationListener, ITickable,  ICapabilityProvider<BasicSpellTurretTile, Void, SpellCaster> {

    boolean playRecoil;
    protected SpellCaster spellCaster = new SpellCaster(0, null, false, null, 1);
    @Nullable UUID uuid = null;

    public BasicSpellTurretTile(BlockEntityType<?> p_i48289_1_, BlockPos pos, BlockState state) {
        super(p_i48289_1_, pos, state);
    }

    public BasicSpellTurretTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.BASIC_SPELL_TURRET_TILE, pos, state);
    }

    public int getManaCost() {
        return this.spellCaster.getSpell().getCost();
    }

    public void setPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public void setSpell(Spell spell){
        this.spellCaster = this.spellCaster.setSpell(spell, 0);
    }

    public void shootSpell(){
        BlockPos pos = this.getBlockPos();

        if (spellCaster.getSpell().isEmpty() || !(this.level instanceof ServerLevel level))
            return;
        int manaCost = getManaCost();
        if (manaCost > 0 && SourceUtil.takeSourceWithParticles(pos, level, 10, manaCost) == null)
            return;
        Networking.sendToNearbyClient(level, pos, new PacketOneShotAnimation(pos));
        Position iposition = BasicSpellTurret.getDispensePosition(pos, level.getBlockState(pos).getValue(BasicSpellTurret.FACING));
        Direction direction = level.getBlockState(pos).getValue(BasicSpellTurret.FACING);
        FakePlayer fakePlayer = uuid != null
                ? FakePlayerFactory.get(level, new GameProfile(uuid, ""))
                : ANFakePlayer.getPlayer(level);
        fakePlayer.setPos(pos.getX(), pos.getY(), pos.getZ());
        EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(level, spellCaster.getSpell(), fakePlayer, new TileCaster(this, SpellContext.CasterType.TURRET)));
        if (resolver.castType != null && BasicSpellTurret.TURRET_BEHAVIOR_MAP.containsKey(resolver.castType)) {
            BasicSpellTurret.TURRET_BEHAVIOR_MAP.get(resolver.castType).onCast(resolver, level, pos, fakePlayer, iposition, direction);
            spellCaster.playSound(pos, level, null, spellCaster.getCurrentSound(), SoundSource.BLOCKS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("spell_caster", ANCodecs.encode(SpellCaster.CODEC.codec(), spellCaster));
        if (uuid != null) pTag.putUUID("uuid", uuid);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.spellCaster = ANCodecs.decode(SpellCaster.CODEC.codec(), pTag.get("spell_caster"));
        if (pTag.contains("uuid")) uuid = pTag.getUUID("uuid");
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
