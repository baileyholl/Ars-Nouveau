package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.api.ritual.CompoundScryer;
import com.hollingsworth.arsnouveau.api.ritual.TagScryer;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import com.hollingsworth.arsnouveau.common.ritual.ScryingRitual;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

import java.util.Arrays;

import static com.hollingsworth.arsnouveau.common.entity.Starbuncle.COLOR;

public class FamiliarStarbuncle extends FamiliarEntity {

    public Starbuncle.StarbuncleData starbuncleData;

    public FamiliarStarbuncle(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide && level.getGameTime() % 60 == 0 && getOwner() != null){
            getOwner().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1, false, false, true));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1, false, false, true));
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(!player.level.isClientSide && player.equals(getOwner())){
            ItemStack stack = player.getItemInHand(hand);
            if(stack.is(Tags.Items.NUGGETS_GOLD)){
                stack.shrink(1);
                ScryingRitual.grantScrying((ServerPlayer) player, 3 * 20 * 60, new CompoundScryer(new TagScryer(Tags.Blocks.ORES_GOLD), new TagScryer(BlockTags.GOLD_ORES)));
                return InteractionResult.SUCCESS;
            }
            if (player.getMainHandItem().is(Tags.Items.DYES)) {
                DyeColor color = DyeColor.getColor(stack);
                if(color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(Starbuncle.carbyColors).contains(color.getName()))
                    return InteractionResult.SUCCESS;
                setColor(color);
                syncTag();
                return InteractionResult.SUCCESS;
            }

        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void setCustomName(@Nullable Component pName) {
        super.setCustomName(pName);
        starbuncleData.name = pName;
        syncTag();
    }

    public void syncTag(){
        IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(getOwner()).orElse(null);
        if(cap != null){
            cap.getFamiliarData(LibEntityNames.STARBUNCLE).entityTag.put("starbuncleData", starbuncleData.toTag());
        }
    }

    public String getColor(){
        return this.entityData.get(COLOR);
    }

    public void setColor(DyeColor color){
        this.entityData.set(COLOR, color.getName());
        this.starbuncleData.color = color.getName();
    }

    @Override
    public PlayState walkPredicate(AnimationEvent event) {
        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("run"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(COLOR, Starbuncle.COLORS.ORANGE.name());
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_STARBUNCLE;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.starbuncleData = new Starbuncle.StarbuncleData(tag.getCompound("starbuncleData"));
        this.entityData.set(COLOR, starbuncleData.color);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if(this.starbuncleData != null)
            tag.put("starbuncleData", this.starbuncleData.toTag());
    }

    @Override
    public void setTagData(@Nullable CompoundTag tag) {
        super.setTagData(tag);
        if(tag != null && tag.contains("starbuncleData")) {
            this.starbuncleData = new Starbuncle.StarbuncleData(tag.getCompound("starbuncleData"));
            syncFromData();
        }
    }

    public void syncFromData(){
        this.entityData.set(COLOR, starbuncleData.color);
        this.setCustomName(starbuncleData.name);
    }
}
