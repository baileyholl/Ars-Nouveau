package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class RuneTile extends AnimatedTile implements IPickupResponder, IAnimatable, ITickable, ITooltipProvider {
    public Spell spell = Spell.EMPTY;
    public boolean isTemporary;
    public boolean disabled;
    public boolean isCharged;
    public int ticksUntilCharge;
    public UUID uuid;
    public ParticleColor color = ParticleUtil.defaultParticleColor();
    public Entity touchedEntity;

    public RuneTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.RUNE_TILE, pos, state);
        isCharged = true;
        isTemporary = false;
        disabled = false;
        ticksUntilCharge = 0;
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
    }

    public void castSpell(Entity entity){
        if(entity == null)
            return;
        if(!this.isCharged || spell.isEmpty() || !(level instanceof ServerLevel) || !(spell.recipe.get(0) instanceof MethodTouch))
            return;
        if (!this.isTemporary && this.disabled) return;
        try {

            Player playerEntity = uuid != null ? level.getPlayerByUUID(uuid) : ANFakePlayer.getPlayer((ServerLevel) level);
            playerEntity = playerEntity == null ?  ANFakePlayer.getPlayer((ServerLevel) level) : playerEntity;
            EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(spell, playerEntity).withCastingTile(this).withType(SpellContext.CasterType.RUNE).withColors(this.color.toWrapper()));
            resolver.onCastOnEntity(ItemStack.EMPTY, playerEntity, entity, InteractionHand.MAIN_HAND);
            if (this.isTemporary) {
                level.destroyBlock(worldPosition, false);
                return;
            }
            this.isCharged = false;

            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).cycle(RuneBlock.POWERED));
            ticksUntilCharge = 20 * 2;
        }catch (Exception e){
            PortUtil.sendMessage(entity, new TranslatableComponent("ars_nouveau.rune.error"));
            e.printStackTrace();
            level.destroyBlock(worldPosition, false);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("spell", spell.serialize());
        tag.putBoolean("charged", isCharged);
        tag.putBoolean("redstone", disabled);
        tag.putBoolean("temp", isTemporary);
        tag.putInt("cooldown", ticksUntilCharge);
        if(uuid != null)
            tag.putUUID("uuid", uuid);
        if(color != null)
            tag.putString("color", color.toWrapper().serialize());
    }

    @Override
    public void load(CompoundTag tag) {
        this.spell = Spell.deserialize(tag.getString("spell"));
        this.isCharged = tag.getBoolean("charged");
        this.disabled = tag.getBoolean("redstone");
        this.isTemporary = tag.getBoolean("temp");
        this.ticksUntilCharge = tag.getInt("cooldown");
        if(tag.contains("uuid"))
            this.uuid = tag.getUUID("uuid");
        this.color = ParticleColor.IntWrapper.deserialize(tag.getString("color")).toParticleColor();
        super.load(tag);
    }

    @Override
    public void tick() {

        if(level == null)
            return;
        if(!level.isClientSide) {
            if (ticksUntilCharge > 0) {
                ticksUntilCharge -= 1;
                return;
            }
        }
        if(this.isCharged)
            return;
        if(!level.isClientSide && this.isTemporary){
            level.destroyBlock(this.worldPosition, false);
        }
        if(!level.isClientSide){
            BlockPos fromPos = SourceUtil.takeSourceNearbyWithParticles(worldPosition, level, 10, 100);
            if(fromPos != null) {
                this.isCharged = true;
                level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).cycle(RuneBlock.POWERED));
            }else
                ticksUntilCharge = 20 * 3;
        }
    }

    @Override
    public List<IItemHandler> getInventory() {
        return BlockUtil.getAdjacentInventories(level, worldPosition);
    }

    @Override
    public @Nonnull ItemStack onPickup(ItemStack stack) {
        return BlockUtil.insertItemAdjacent(level, worldPosition, stack);
    }

    @Override
    public void registerControllers(AnimationData data) {

    }
    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if(ArsNouveau.proxy.getPlayer().hasEffect(ModPotions.MAGIC_FIND_EFFECT)){
            tooltip.add(new TextComponent(spell.getDisplayString()));
        }
    }
}