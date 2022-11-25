package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.item.inv.ExtractedStack;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectInteract extends AbstractEffect {
    public static EffectInteract INSTANCE = new EffectInteract();

    private EffectInteract() {
        super(GlyphLib.EffectInteractID, "Interact");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity e = rayTraceResult.getEntity();
        Player player = getPlayer(shooter, (ServerLevel) world);
        if (!isRealPlayer(shooter)) {
            InventoryManager manager = spellContext.getCaster().getInvManager();
            player = setupFakeInventory(spellContext, world);
            player.interactOn(e, InteractionHand.MAIN_HAND);
            for(ItemStack i : player.inventory.items){
                manager.insertOrDrop(i, world, e.blockPosition());
            }
        }else {
            player.interactOn(e, InteractionHand.MAIN_HAND);
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos blockPos = rayTraceResult.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, blockPos))
            return;
        Player player = getPlayer(shooter, (ServerLevel) world);
        if(isRealPlayer(shooter)){
            blockState.use(world, player, InteractionHand.MAIN_HAND, rayTraceResult);
        }else{
            InventoryManager manager = spellContext.getCaster().getInvManager();
            player = setupFakeInventory(spellContext, world);
            blockState.use(world, player, InteractionHand.MAIN_HAND, rayTraceResult);
            for(ItemStack i : player.inventory.items){
                manager.insertOrDrop(i, world, rayTraceResult.getBlockPos());
            }
        }
    }

    public FakePlayer setupFakeInventory(SpellContext context, Level level){
        InventoryManager manager = context.getCaster().getInvManager();
        ANFakePlayer player = ANFakePlayer.getPlayer((ServerLevel) level);
        player.inventory.clearContent();
        player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        ExtractedStack stack = manager.extractItem(i -> !i.isEmpty(), 1);
        if(!stack.isEmpty()){
            player.setItemSlot(EquipmentSlot.MAINHAND, stack.getStack().copy());
        }
        return player;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Interacts with blocks or entities as it were a player. Useful for reaching levers, chests, or animals.";
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}