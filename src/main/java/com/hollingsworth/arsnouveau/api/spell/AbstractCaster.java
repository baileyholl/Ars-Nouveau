package com.hollingsworth.arsnouveau.api.spell;

import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineMap;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.IWrappedCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.network.PacketCastSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.mojang.datafixers.util.Function6;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractCaster<T extends AbstractCaster<T>> implements TooltipProvider {

    public static <T extends AbstractCaster<T>> MapCodec<T> createCodec(Function6<Integer, String, Boolean, String, Integer, SpellSlotMap, T> constructor) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.optionalFieldOf("current_slot", 0).forGetter(s -> s.slot),
                Codec.STRING.optionalFieldOf("flavor_text", "").forGetter(s -> s.flavorText),
                Codec.BOOL.optionalFieldOf("is_hidden", false).forGetter(s -> s.isHidden),
                Codec.STRING.optionalFieldOf("hidden_text", "").forGetter(s -> s.hiddenText),
                Codec.INT.optionalFieldOf("max_slots", 1).forGetter(s -> s.maxSlots),
                SpellSlotMap.CODEC.optionalFieldOf("spells", new SpellSlotMap(ImmutableMap.of())).forGetter(s -> s.spells)
        ).apply(instance, constructor));
    }

    public static <T extends AbstractCaster<T>> StreamCodec<RegistryFriendlyByteBuf, T> createStream(Function6<Integer, String, Boolean, String, Integer, SpellSlotMap, T> constructor) {
        return StreamCodec.composite(ByteBufCodecs.INT, s -> s.slot, ByteBufCodecs.STRING_UTF8, s -> s.flavorText,
                ByteBufCodecs.BOOL, s -> s.isHidden, ByteBufCodecs.STRING_UTF8, s -> s.hiddenText, ByteBufCodecs.INT, s -> s.maxSlots, SpellSlotMap.STREAM, s -> s.spells,
                constructor);
    }

    public abstract MapCodec<T> codec();

    public abstract StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    protected final SpellSlotMap spells;
    protected final int slot;
    protected final String flavorText;
    protected final boolean isHidden;
    protected final String hiddenText;
    protected final int maxSlots;

    public AbstractCaster(int slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots, SpellSlotMap spells) {
        this.slot = slot;
        this.flavorText = flavorText == null ? "" : flavorText;
        this.isHidden = isHidden;
        this.hiddenText = hiddenText == null ? "" : hiddenText;
        this.maxSlots = maxSlots;
        this.spells = spells;
    }

    public AbstractCaster() {
        this(0, "", false, "", 1);
    }

    public AbstractCaster(int maxSlots) {
        this(0, "", false, "", maxSlots);
    }

    public AbstractCaster(Integer slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots) {
        this(slot, flavorText, isHidden, hiddenText, maxSlots, new SpellSlotMap(ImmutableMap.of()));
    }


    public T setCurrentSlot(int slot) {
        return build(slot, flavorText, isHidden, hiddenText, maxSlots, spells);
    }


    public T setSpell(Spell spell, int slot) {
        return build(this.slot, flavorText, isHidden, hiddenText, maxSlots, spells.put(slot, spell));
    }


    public T setFlavorText(String str) {
        return build(slot, str, isHidden, hiddenText, maxSlots, spells);
    }


    public T setSpellName(String name, int slot) {
        var spell = this.getSpell(slot);
        return build(this.slot, flavorText, isHidden, hiddenText, maxSlots, spells.put(slot, new Spell(name, spell.color(), spell.sound(), new ArrayList<>(spell.unsafeList()))));
    }


    public T setHidden(boolean hidden) {
        return build(slot, flavorText, hidden, hiddenText, maxSlots, spells);
    }


    public T setHiddenRecipe(String recipe) {
        return build(slot, flavorText, isHidden, recipe, maxSlots, spells);
    }


    public T setMaxSlots(int slots) {
        return build(slot, flavorText, isHidden, hiddenText, slots, spells);
    }

    public T setSound(ConfiguredSpellSound sound, int slot) {
        var spell = this.getSpell(slot);
        return build(this.slot, flavorText, isHidden, hiddenText, maxSlots, this.spells.put(slot, new Spell(spell.name(), spell.color(), sound, new ArrayList<>(spell.unsafeList()))));
    }

    public T setColor(ParticleColor color, int slot) {
        var spell = this.getSpell(slot);
        return build(this.slot, flavorText, isHidden, hiddenText, maxSlots, this.spells.put(slot, new Spell(spell.name(), color, spell.sound(), new ArrayList<>(spell.unsafeList()))));
    }

    public T setParticles(TimelineMap timeline, int slot) {
        var spell = this.getSpell(slot);
        return build(this.slot, flavorText, isHidden, hiddenText, maxSlots, this.spells.put(slot, new Spell(spell.name(), spell.color(), spell.sound(), new ArrayList<>(spell.unsafeList()), timeline)));
    }

    public T setParticles(TimelineMap timeline) {
        return setParticles(timeline, getCurrentSlot());
    }

    public TimelineMap getParticles() {
        return getParticles(getCurrentSlot());
    }

    public TimelineMap getParticles(int slot) {
        return this.getSpell(slot).particleTimeline();
    }

    @NotNull
    public Spell getSpell() {
        return spells.getOrDefault(getCurrentSlot(), new Spell());
    }

    public @NotNull Spell getSpell(int slot) {
        return spells.getOrDefault(slot, new Spell());
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public int getCurrentSlot() {
        return slot;
    }

    public ParticleColor getColor(int slot) {
        return this.getSpell(slot).color();
    }

    public String getSpellName(int slot) {
        return this.getSpell(slot).name();
    }

    public boolean isSpellHidden() {
        return isHidden;
    }


    public String getHiddenRecipe() {
        return hiddenText;
    }

    public String getFlavorText() {
        return flavorText == null ? "" : flavorText;
    }


    @NotNull
    public ConfiguredSpellSound getSound(int slot) {
        return this.getSpell(slot).sound();
    }

    public SpellSlotMap getSpells() {
        return spells;
    }

    public T setNextSlot() {
        int slot = getCurrentSlot() + 1;
        if (slot >= getMaxSlots()) {
            slot = 0;
        }
        return setCurrentSlot(slot);
    }

    public T setPreviousSlot() {
        int slot = getCurrentSlot() - 1;
        if (slot < 0)
            slot = getMaxSlots() - 1;
        return setCurrentSlot(slot);
    }

    public T setSpell(Spell spell) {
        return setSpell(spell, getCurrentSlot());
    }


    @NotNull
    public ParticleColor getColor() {
        return getColor(getCurrentSlot());
    }

    public T setColor(ParticleColor color) {
        return setColor(color, getCurrentSlot());
    }


    public T setSound(ConfiguredSpellSound sound) {
        return setSound(sound, getCurrentSlot());
    }

    public ConfiguredSpellSound getCurrentSound() {
        return getSound(getCurrentSlot());
    }

    public String getSpellName() {
        return getSpellName(getCurrentSlot());
    }

    public T setSpellName(String name) {
        return setSpellName(name, getCurrentSlot());
    }

    public int getBonusGlyphSlots() {
        return 0;
    }

    @NotNull
    public Spell getSpell(Level world, LivingEntity playerEntity, InteractionHand hand, AbstractCaster caster) {
        return caster.getSpell();
    }

    public Spell modifySpellBeforeCasting(ServerLevel worldIn, @Nullable Entity playerIn, @Nullable InteractionHand handIn, Spell spell) {
        return spell;
    }

    public InteractionResultHolder<ItemStack> castSpell(Level worldIn, LivingEntity entity, InteractionHand handIn, @Nullable Component invalidMessage, @NotNull Spell spell) {
        ItemStack stack = entity.getItemInHand(handIn);

        if (!(worldIn instanceof ServerLevel serverLevel))
            return InteractionResultHolder.pass(entity.getItemInHand(handIn));
        spell = modifySpellBeforeCasting(serverLevel, entity, handIn, spell);
        if (!spell.isValid() && invalidMessage != null) {
            PortUtil.sendMessageNoSpam(entity, invalidMessage);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        Player player = ANFakePlayer.getOrFakePlayer(serverLevel, entity);
        IWrappedCaster wrappedCaster = entity instanceof Player pCaster ? new PlayerCaster(pCaster) : new LivingCaster(entity);
        SpellResolver resolver = getSpellResolver(new SpellContext(worldIn, spell, entity, wrappedCaster, stack), worldIn, player, handIn);
        boolean isSensitive = resolver.spell.getBuffsAtIndex(0, entity, AugmentSensitive.INSTANCE) > 0;
        HitResult result = SpellUtil.rayTrace(entity, 0.5 + player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue(), 1, isSensitive);
        if (result instanceof BlockHitResult blockHit) {
            BlockEntity tile = worldIn.getBlockEntity(blockHit.getBlockPos());
            if (tile instanceof ScribesTile)
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

            if (!entity.isShiftKeyDown() && tile != null && !(worldIn.getBlockState(blockHit.getBlockPos()).is(BlockTagProvider.IGNORE_TILE))) {
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
            }
        }

        if (result instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity) {
            if (resolver.onCastOnEntity(stack, entityHitResult.getEntity(), handIn))
                playSound(entity.getOnPos(), worldIn, entity, getCurrentSound(), SoundSource.PLAYERS);
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }

        if (result instanceof BlockHitResult blockHitResult && (result.getType() == HitResult.Type.BLOCK || isSensitive)) {
            if (entity instanceof Player) {
                UseOnContext context = new UseOnContext(player, handIn, (BlockHitResult) result);
                if (resolver.onCastOnBlock(context))
                    playSound(entity.getOnPos(), worldIn, entity, getCurrentSound(), SoundSource.PLAYERS);
            } else if (resolver.onCastOnBlock(blockHitResult)) {
                playSound(entity.getOnPos(), worldIn, entity, getCurrentSound(), SoundSource.NEUTRAL);
            }
            return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
        }

        if (resolver.onCast(stack, worldIn))
            playSound(entity.getOnPos(), worldIn, entity, getCurrentSound(), SoundSource.PLAYERS);
        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
    }

    public InteractionResultHolder<ItemStack> castSpell(Level worldIn, LivingEntity playerIn, InteractionHand handIn, Component invalidMessage) {
        return castSpell(worldIn, playerIn, handIn, invalidMessage, getSpell(worldIn, playerIn, handIn, this));
    }

    public void castOnServer(InteractionHand handIn, Component invalidMessage) {
        PacketDistributor.sendToServer(new PacketCastSpell(this, handIn, invalidMessage));
    }

    @SuppressWarnings("unchecked")
    public void saveToStack(ItemStack stack) {
        stack.set(this.getComponentType(), this);
    }

    @SuppressWarnings("rawtypes")
    public abstract DataComponentType getComponentType();

    public SpellResolver getSpellResolver(SpellContext context, Level worldIn, LivingEntity playerIn, InteractionHand handIn) {
        return new SpellResolver(context);
    }

    public void playSound(BlockPos pos, Level worldIn, @Nullable Entity playerIn, ConfiguredSpellSound configuredSound, SoundSource source) {
        if (configuredSound == null || configuredSound.getSound() == null || configuredSound.getSound().getSoundEvent() == null || configuredSound.equals(ConfiguredSpellSound.EMPTY))
            return;
        worldIn.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, configuredSound.getSound().getSoundEvent(), source, configuredSound.getVolume(), configuredSound.getPitch());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCaster caster = (AbstractCaster) o;
        return slot == caster.slot && isHidden == caster.isHidden
                && maxSlots == caster.maxSlots
                && Objects.equals(spells, caster.spells)
                && Objects.equals(flavorText, caster.flavorText)
                && Objects.equals(hiddenText, caster.hiddenText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spells, slot, flavorText, isHidden, hiddenText, maxSlots);
    }

    protected abstract T build(int slot, String flavorText, Boolean isHidden, String hiddenText, int maxSlots, SpellSlotMap spells);

    @Override
    public void addToTooltip(Item.@NotNull TooltipContext pContext, @NotNull Consumer<Component> pTooltipAdder, @NotNull TooltipFlag pTooltipFlag) {
        if (getSpell().isEmpty()) {
            pTooltipAdder.accept(Component.translatable("ars_nouveau.tooltip.can_inscribe"));
            return;
        }
        if (!getSpellName().isEmpty()) {
            pTooltipAdder.accept(Component.literal(getSpellName()));
        }
        if (isSpellHidden()) {
            pTooltipAdder.accept(Component.literal(getHiddenRecipe()).withStyle(Style.EMPTY.withFont(ResourceLocation.fromNamespaceAndPath("minecraft", "alt")).withColor(ChatFormatting.GOLD)));
        } else {
            Spell spell = getSpell();
            pTooltipAdder.accept(Component.literal(spell.getDisplayString()));
        }
        if (!getFlavorText().isEmpty())
            pTooltipAdder.accept(Component.literal(getFlavorText()).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.BLUE)));
    }
}
