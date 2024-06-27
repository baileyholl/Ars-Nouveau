package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.IWrappedCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.client.renderer.item.ScryCasterRenderer;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.ScryerCrystal;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ScryCaster extends ModItem implements ICasterTool, GeoItem {

    public ScryCaster(Properties properties) {
        super(properties);
    }

    public ScryCaster() {
        super();
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        ItemStack stack = pContext.getItemInHand();
        ScryCaster.Data data = new Data(stack);
        if(pContext.getLevel().getBlockState(pos).getBlock() instanceof ScryerCrystal){
            if(!pContext.getLevel().isClientSide) {
                data.setScryPos(pos);
                PortUtil.sendMessage(pContext.getPlayer(), Component.translatable("ars_nouveau.dominion_wand.position_set"));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        ISpellCaster caster = getSpellCaster(stack);
        return caster.castSpell(pLevel, (LivingEntity) pPlayer, pUsedHand, Component.translatable("ars_nouveau.invalid_spell"));
    }

    @Override
    public ISpellCaster getSpellCaster(CompoundTag tag) {
        return new ScryCasterType(tag);
    }

    @Override
    public @NotNull ISpellCaster getSpellCaster(ItemStack stack) {
        return new ScryCasterType(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        getInformation(stack, context, tooltip2, flagIn);
        Data data = new Data(stack);
        if(data.scryPos == null){
            tooltip2.add(Component.translatable("ars_nouveau.scry_caster.no_pos"));
        }else{
            tooltip2.add(Component.translatable("ars_nouveau.scryer_scroll.bound", data.getScryPos().getX() + ", " + data.getScryPos().getY() + ", " + data.getScryPos().getZ()));
        }
        super.appendHoverText(stack, context, tooltip2, flagIn);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new ScryCasterRenderer();

            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public static class ScryCasterType extends SpellCaster{

        public ScryCasterType(ItemStack stack) {
            super(stack);
        }

        public ScryCasterType(CompoundTag itemTag) {
            super(itemTag);
        }

        @Override
        public InteractionResultHolder<ItemStack> castSpell(Level worldIn, LivingEntity entity, InteractionHand handIn, @org.jetbrains.annotations.Nullable Component invalidMessage, @NotNull Spell spell) {
            ItemStack stack = entity.getItemInHand(handIn);

            if (worldIn.isClientSide)
                return InteractionResultHolder.pass(entity.getItemInHand(handIn));
            spell = modifySpellBeforeCasting(worldIn, entity, handIn, spell);
            if (!spell.isValid() && invalidMessage != null) {
                PortUtil.sendMessageNoSpam(entity, invalidMessage);
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
            }
            Player player = entity instanceof Player thisPlayer ? thisPlayer : ANFakePlayer.getPlayer((ServerLevel) worldIn);
            IWrappedCaster wrappedCaster = entity instanceof Player pCaster ? new PlayerCaster(pCaster) : new LivingCaster(entity);
            SpellResolver resolver = getSpellResolver(new SpellContext(worldIn, spell, entity, wrappedCaster, stack), worldIn, player, handIn);
            ITurretBehavior behavior = BasicSpellTurret.TURRET_BEHAVIOR_MAP.get(spell.getCastMethod());
            if(behavior == null){
                PortUtil.sendMessage(entity, Component.translatable("ars_nouveau.scry_caster.invalid_behavior"));
                return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
            }

            ScryCaster.Data data = new Data(stack);
            boolean playerHoldingScroll = entity.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ScryerScroll;
            BlockPos scryPos = playerHoldingScroll ? new ScryerScroll.ScryerScrollData(player.getItemInHand(InteractionHand.OFF_HAND)).pos : data.getScryPos();
            if(scryPos == null){
                PortUtil.sendMessage(entity, Component.translatable("ars_nouveau.scry_caster.no_pos"));
                return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
            }
            if(!worldIn.isLoaded(scryPos)){
                PortUtil.sendMessage(entity, Component.translatable("ars_nouveau.camera.not_loaded"));
                return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
            }
            BlockState castingAtState = worldIn.getBlockState(scryPos);
            if(!(castingAtState.getBlock() instanceof ScryerCrystal)){
                PortUtil.sendMessage(entity, Component.translatable("ars_nouveau.scry_caster.not_crystal"));
                return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
            }

            if(!resolver.canCast(player)){
                return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
            }

            Position position;
            Direction direction = castingAtState.getValue(ScryerCrystal.FACING);
            // Target the block the crystal is facing if the spell is a touch spell.
            if(spell.getCastMethod() instanceof MethodTouch){
                position = BasicSpellTurret.getDispensePosition(scryPos, direction);
            }else{
                position = ScryerCrystal.getDispensePosition(scryPos, direction);
            }
            behavior.onCast(resolver, (ServerLevel) worldIn, scryPos,
                    player,
                    position,
                    direction);
            resolver.expendMana();
            playSound(entity.getOnPos(), worldIn, entity, getCurrentSound(), SoundSource.PLAYERS);
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }
    }


    public static class Data extends ItemstackData{
        private BlockPos scryPos;

        public Data(ItemStack stack) {
            super(stack);
            CompoundTag tag1 = getItemTag(stack);
            if (tag1 == null || tag1.isEmpty())
                return;
            if(tag1.contains("scryPos")){
                scryPos = BlockPos.of(tag1.getLong("scryPos"));
            }
        }

        public void setScryPos(BlockPos pos){
            this.scryPos = pos;
            writeItem();
        }

        public @Nullable BlockPos getScryPos(){
            return scryPos;
        }

        @Override
        public String getTagString() {
            return "an_scry_data";
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            if(scryPos != null){
                tag.putLong("scryPos", scryPos.asLong());
            }
        }
    }
}
