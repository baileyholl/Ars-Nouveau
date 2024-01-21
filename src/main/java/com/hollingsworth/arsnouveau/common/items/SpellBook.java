package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import com.hollingsworth.arsnouveau.client.renderer.item.SpellBookRenderer;
import com.hollingsworth.arsnouveau.common.capability.ANPlayerDataCap;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IDyeable;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetBookMode;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpellBook extends ModItem implements IAnimatable, ISyncable, ICasterTool, IDyeable, IRadialProvider {

    public SpellTier tier;

    @Override
    public String getSyncKey() {
        return ISyncable.super.getSyncKey() + '_' + tier.value;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.sameItem(newStack);
    }

    AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public SpellBook(SpellTier tier) {
        this(new Item.Properties().stacksTo(1).tab(ArsNouveau.itemGroup), tier);
    }

    public SpellBook(Properties properties, SpellTier tier) {
        super(properties);
        this.tier = tier;
        GeckoLibNetwork.registerSyncable(this);
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (this != ItemsRegistry.CREATIVE_SPELLBOOK.get()) {
            CapabilityRegistry.getMana(playerIn).ifPresent(iMana -> {
                if (iMana.getBookTier() < this.tier.value) {
                    iMana.setBookTier(this.tier.value);
                }
                IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(playerIn).orElse(new ANPlayerDataCap());
                if (iMana.getGlyphBonus() < cap.getKnownGlyphs().size()) {
                    iMana.setGlyphBonus(cap.getKnownGlyphs().size());
                }
            });
        }
        ISpellCaster caster = getSpellCaster(stack);

        return caster.castSpell(worldIn, (LivingEntity) playerIn, handIn, Component.translatable("ars_nouveau.invalid_spell"));
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(final ItemStack stack, @Nullable final Level world, final List<Component> tooltip, final TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(Component.translatable("ars_nouveau.spell_book.select", KeyMapping.createNameSupplier(ModKeyBindings.OPEN_RADIAL_HUD.getName()).get()));
        tooltip.add(Component.translatable("ars_nouveau.spell_book.craft", KeyMapping.createNameSupplier(ModKeyBindings.OPEN_BOOK.getName()).get()));
        tooltip.add(Component.translatable("tooltip.ars_nouveau.caster_level", getTier().value).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
    }

    public SpellTier getTier() {
        return this.tier;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, (a) -> PlayState.CONTINUE));
    }

    @Override
    public void onAnimationSync(int id, int state) {
        if (state == 0 || state == 1) {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, id, "controller");
            if (controller.getAnimationState() == AnimationState.Stopped) {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation(state == 1 ? "flip_page_right" : "flip_page_left", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            }
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @NotNull
    @Override
    public ISpellCaster getSpellCaster(ItemStack stack) {
        return new BookCaster(stack);
    }

    @Override
    public ISpellCaster getSpellCaster() {
        return new BookCaster(new CompoundTag());
    }

    @Override
    public ISpellCaster getSpellCaster(CompoundTag tag) {
        return new BookCaster(tag);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new SpellBookRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onOpenBookMenuKeyPressed(ItemStack stack, Player player) {
        InteractionHand hand = StackUtil.getBookHand(player);
        if (hand == null) {
            return;
        }
        Minecraft.getInstance().setScreen(new GuiSpellBook(hand));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onRadialKeyPressed(ItemStack stack, Player player) {
        Minecraft.getInstance().setScreen(new GuiRadialMenu<>(getRadialMenuProviderForSpellpart(stack)));
    }

    public RadialMenu<AbstractSpellPart> getRadialMenuProviderForSpellpart(ItemStack itemStack) {
        return new RadialMenu<>((int slot) -> {
            BookCaster caster = new BookCaster(itemStack);
            caster.setCurrentSlot(slot);
            Networking.INSTANCE.sendToServer(new PacketSetBookMode(itemStack.getTag()));
        },
                getRadialMenuSlotsForSpellpart(itemStack),
                RenderUtils::drawSpellPart,
                0);
    }

    public List<RadialMenuSlot<AbstractSpellPart>> getRadialMenuSlotsForSpellpart(ItemStack itemStack) {
        BookCaster spellCaster = new BookCaster(itemStack);
        List<RadialMenuSlot<AbstractSpellPart>> radialMenuSlots = new ArrayList<>();
        for (int i = 0; i < spellCaster.getMaxSlots(); i++) {
            Spell spell = spellCaster.getSpell(i);
            AbstractSpellPart primaryIcon = null;
            List<AbstractSpellPart> secondaryIcons = new ArrayList<>();
            for (AbstractSpellPart p : spell.recipe) {
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

    public static class BookCaster extends SpellCaster {

        public BookCaster(ItemStack stack) {
            super(stack);
        }

        public BookCaster(CompoundTag tag) {
            super(tag);
        }

        @Override
        public int getMaxSlots() {
            return 10;
        }
    }
}
