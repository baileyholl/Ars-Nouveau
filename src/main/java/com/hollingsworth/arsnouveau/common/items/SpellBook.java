package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.SecondaryIconPosition;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.client.jei.AliasProvider;
import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import com.hollingsworth.arsnouveau.client.renderer.item.SpellBookRenderer;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetCasterSlot;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class SpellBook extends ModItem implements GeoItem, ICasterTool, IRadialProvider, AliasProvider {

    public SpellTier tier;
    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    public SpellBook(SpellTier tier) {
        this(new Item.Properties().stacksTo(1)
                .component(DataComponents.BASE_COLOR, DyeColor.PURPLE)
                .component(DataComponentRegistry.SPELL_CASTER, new SpellCaster(10)), tier);
    }

    public SpellBook(Properties properties, SpellTier tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
//        var caster2 = SpellCasterRegistry.from(stack);
//        caster2.getSpell().particleTimeline().debugPrintHash(caster2.getSpell(), worldIn);
//        var timeline = caster2.getSpell().particleTimeline().get(ParticleTimelineRegistry.PROJECTILE_TIMELINE.get());
//        var element = IParticleTimeline.CODEC.encodeStart(JsonOps.INSTANCE, timeline);
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        System.out.println(gson.toJson(element.result().get()));
        if (playerIn instanceof ServerPlayer) {
            if (tier != SpellTier.CREATIVE) {
                var iMana = CapabilityRegistry.getMana(playerIn);
                if (iMana != null) {
                    boolean shouldSync = false;
                    if (iMana.getBookTier() < this.tier.value) {
                        iMana.setBookTier(this.tier.value);
                        shouldSync = true;
                    }
                    IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(playerIn);
                    if (iMana.getGlyphBonus() < cap.getKnownGlyphs().size()) {
                        iMana.setGlyphBonus(cap.getKnownGlyphs().size());
                        shouldSync = true;
                    }
                    if (shouldSync && playerIn instanceof ServerPlayer player) {
                        iMana.syncToClient(player);
                    }
                }
            }

            return InteractionResultHolder.pass(stack);
        }

        var caster = this.getSpellCaster(stack);
        if (caster == null) {
            return InteractionResultHolder.pass(stack);
        }
        caster.castOnServer(handIn, Component.translatable("ars_nouveau.invalid_spell"));

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean doesSneakBypassUse(@NotNull ItemStack stack, @NotNull LevelReader world, @NotNull BlockPos pos, @NotNull Player player) {
        return true;
    }

    @Override
    public void appendHoverText(final @NotNull ItemStack stack, final @NotNull TooltipContext world, final @NotNull List<Component> tooltip, final @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (ArsNouveau.proxy.isClientSide()) {
            tooltip.add(Component.translatable("ars_nouveau.spell_book.select", KeyMapping.createNameSupplier(ModKeyBindings.OPEN_RADIAL_HUD.getName()).get()));
            tooltip.add(Component.translatable("ars_nouveau.spell_book.craft", KeyMapping.createNameSupplier(ModKeyBindings.OPEN_BOOK.getName()).get()));
            tooltip.add(Component.translatable("tooltip.ars_nouveau.caster_level", getTier().value).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
        }
    }

    public SpellTier getTier() {
        return this.tier;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private final BlockEntityWithoutLevelRenderer renderer = new SpellBookRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                return renderer;
            }
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onOpenBookMenuKeyPressed(ItemStack stack, Player player) {
        InteractionHand hand = StackUtil.getBookHand(player);
        if (hand == null) {
            return;
        }
        if (player.level.isClientSide) {
            GuiSpellBook.open(hand);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onRadialKeyPressed(ItemStack stack, Player player) {
        if (ArsNouveau.proxy.isClientSide()) {
            ArsNouveau.proxy.getMinecraft().setScreen(new GuiRadialMenu<>(getRadialMenuProviderForSpellpart(stack)));
        }
    }

    public RadialMenu<AbstractSpellPart> getRadialMenuProviderForSpellpart(ItemStack itemStack) {
        return new RadialMenu<>((int slot) -> {
            AbstractCaster<?> caster = this.getSpellCaster(itemStack);
            if (caster != null) {
                caster.setCurrentSlot(slot).saveToStack(itemStack);
            }
            Networking.sendToServer(new PacketSetCasterSlot(slot));
        },
                getRadialMenuSlotsForSpellpart(itemStack),
                SecondaryIconPosition.NORTH,
                RenderUtils::drawSpellPart,
                0);
    }

    public List<RadialMenuSlot<AbstractSpellPart>> getRadialMenuSlotsForSpellpart(ItemStack itemStack) {
        AbstractCaster<?> spellCaster = SpellCasterRegistry.from(itemStack);
        List<RadialMenuSlot<AbstractSpellPart>> radialMenuSlots = new ArrayList<>();
        for (int i = 0; i < spellCaster.getMaxSlots(); i++) {
            Spell spell = spellCaster.getSpell(i);
            AbstractSpellPart primaryIcon = null;
            List<AbstractSpellPart> secondaryIcons = new ArrayList<>();
            for (AbstractSpellPart p : spell.recipe()) {
                if (p instanceof AbstractCastMethod) {
                    secondaryIcons.add(p);
                }

                if (p instanceof AbstractEffect) {
                    primaryIcon = p;
                    break;
                }
            }
            radialMenuSlots.add(new RadialMenuSlot<>(spellCaster.getSpellName(i), primaryIcon, secondaryIcons));
        }
        return radialMenuSlots;
    }

    @Override
    public boolean canQuickCast() {
        return true;
    }

    @Override
    public Collection<Alias> getAliases() {
        return List.of(
                new Alias("spellbook", "Spellbook")
        );
    }
}