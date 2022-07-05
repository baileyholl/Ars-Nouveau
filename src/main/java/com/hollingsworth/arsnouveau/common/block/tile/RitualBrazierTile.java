package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.ILightable;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.RitualBrazierBlock;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;
import java.util.Random;

public class RitualBrazierTile extends ModdedTile implements ITooltipProvider, IAnimatable, ILightable, ITickable {
    public AbstractRitual ritual;
    AnimationFactory manager = new AnimationFactory(this);
    public boolean isDecorative;
    int red;
    int blue;
    int green;
    public boolean isOff;

    public RitualBrazierTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public RitualBrazierTile(BlockPos p, BlockState s){
        super(BlockRegistry.RITUAL_TILE, p, s);
    }

    public void makeParticle(ParticleColor centerColor, ParticleColor outerColor, int intensity){
        Level world = getLevel();
        BlockPos pos = getBlockPos();
        Random rand = world.random;
        double xzOffset = 0.25;
        for(int i =0; i < intensity; i++){
            world.addParticle(
                    GlowParticleData.createData(centerColor),
                    pos.getX() +0.5 + ParticleUtil.inRange(-xzOffset/2, xzOffset/2)  , pos.getY() + 1 + ParticleUtil.inRange(-0.05, 0.2) , pos.getZ() +0.5 + ParticleUtil.inRange(-xzOffset/2, xzOffset/2),
                    0, ParticleUtil.inRange(0.0, 0.05f),0);
        }
        for(int i =0; i < intensity; i++){
            world.addParticle(
                    GlowParticleData.createData(outerColor),
                    pos.getX() +0.5 + ParticleUtil.inRange(-xzOffset, xzOffset)  , pos.getY() +1 + ParticleUtil.inRange(0, 0.7) , pos.getZ() +0.5 + ParticleUtil.inRange(-xzOffset, xzOffset),
                    0,ParticleUtil.inRange(0.0, 0.05f),0);
        }
    }

    @Override
    public void tick() {
        if(isDecorative && level.isClientSide){
            makeParticle(ParticleColor.makeRandomColor(red, green, blue, level.random), ParticleColor.makeRandomColor(red, green, blue, level.random), 50);
            return;
        }


        if(level.isClientSide && ritual != null){
            makeParticle(ritual.getCenterColor(), ritual.getOuterColor(), ritual.getParticleIntensity());
        }
        if(isOff)
            return;
        if(ritual != null){

            if(ritual.getContext().isDone){
                ritual.onEnd();
                ritual = null;
                getLevel().playSound(null, getBlockPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0f, 1.0f);
                getLevel().setBlock(getBlockPos(), getLevel().getBlockState(getBlockPos()).setValue(RitualBrazierBlock.LIT, false), 3);
                return;
            }
            if(!ritual.isRunning() && !level.isClientSide){
                level.getEntitiesOfClass(ItemEntity.class, new AABB(getBlockPos()).inflate(1)).forEach(i ->{
                    if(ritual.canConsumeItem(i.getItem())){
                        ritual.onItemConsumed(i.getItem());
                        ParticleUtil.spawnPoof((ServerLevel) level, i.blockPosition());
                    }
                });
            }
            if(ritual.consumesMana() && ritual.needsManaNow()){
                int cost = ritual.getManaCost();
                if(SourceUtil.takeSourceNearbyWithParticles(getBlockPos(), getLevel(), 6, cost) != null){
                    ritual.setNeedsMana(false);
                    setChanged();
                }else{
                    return;
                }
            }
            ritual.tryTick();
        }
    }

    public boolean isRitualDone(){
        return ritual != null && ritual.getContext().isDone;
    }


    public boolean canRitualStart(){
        return ritual.canStart();
    }


    public void startRitual(){
        if(ritual == null || !ritual.canStart() || ritual.isRunning())
            return;
        getLevel().playSound(null, getBlockPos(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.NEUTRAL, 1.0f, 1.0f);
        ritual.onStart();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        String ritualID = tag.getString("ritualID");
        if(!ritualID.isEmpty()){
            ritual = ArsNouveauAPI.getInstance().getRitual(ritualID);
            if(ritual != null) {
                ritual.read(tag);
                ritual.tile = this;
            }
        }else{
            ritual = null;
        }
        this.red = tag.getInt("red");
        this.red = red > 0 ? red : 255;
        this.green = tag.getInt("green");
        green = this.green > 0 ? green : 125;
        this.blue = tag.getInt("blue");
        blue = this.blue > 0 ? blue : 255;
        isDecorative = tag.getBoolean("decorative");
        isOff = tag.getBoolean("off");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        if(ritual != null){
            tag.putString("ritualID", ritual.getID());
            ritual.write(tag);
        }else{
            tag.remove("ritualID");
        }
        tag.putInt("red", red);
        tag.putInt("green", green);
        tag.putInt("blue", blue);
        tag.putBoolean("decorative", isDecorative);
        tag.putBoolean("off", isOff);
    }

    public boolean canTakeAnotherRitual(){
        return this.ritual == null || this.ritual.isRunning();
    }

    public void setRitual(String selectedRitual) {
        this.ritual = ArsNouveauAPI.getInstance().getRitual(selectedRitual);
        if(ritual != null){
            this.ritual.tile = this;
            Level world = getLevel();
            BlockState state = world.getBlockState(getBlockPos());
            world.setBlock(getBlockPos(), state.setValue(RitualBrazierBlock.LIT, true), 3);
        }
        this.isDecorative = false;
        level.playSound(null, getBlockPos(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.NEUTRAL, 1.0f, 1.0f);
    }

    @Override
    public void getTooltip(List<Component> tooltips) {
        if(ritual != null){
            tooltips.add(new TextComponent(ritual.getName()));
            if(isOff) {
                tooltips.add(new TranslatableComponent("ars_nouveau.tooltip.turned_off"));
                return;
            }
            if(!ritual.isRunning()){
                if(!ritual.canStart()){
                    tooltips.add(new TranslatableComponent("ars_nouveau.tooltip.conditions_unmet"));
                }else
                    tooltips.add(new TranslatableComponent("ars_nouveau.tooltip.waiting"));
            }else{

                tooltips.add(new TranslatableComponent("ars_nouveau.tooltip.running"));
            }
            if(ritual.getConsumedItems().size() != 0) {
                tooltips.add(new TranslatableComponent("ars_nouveau.tooltip.consumed"));
                for (ItemStack i : ritual.getConsumedItems()) {
                    tooltips.add(i.getHoverName());
                }
            }
            if(ritual.needsManaNow())
                tooltips.add(new TranslatableComponent("ars_nouveau.wixie.need_mana"));
        }
    }


    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "idle", 0, this::idlePredicate));
    }

    private <P extends IAnimatable> PlayState idlePredicate(AnimationEvent<P> pAnimationEvent) {
        pAnimationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("gem_float", true));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    @Override
    public void onLight(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats stats, SpellContext spellContext) {
        this.red = spellContext.colors.r;
        this.green = spellContext.colors.g;
        this.blue = spellContext.colors.b;
        this.isDecorative = true;
        BlockState state = world.getBlockState(getBlockPos());
        world.setBlock(getBlockPos(), state.setValue(RitualBrazierBlock.LIT, true), 3);
    }
}
