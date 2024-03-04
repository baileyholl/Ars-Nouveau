package com.hollingsworth.arsnouveau.common.mixin.jar;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
@Mixin(DispenserBlock.class)
public abstract class DispenserMixin {


    @Shadow
    protected abstract DispenseItemBehavior getDispenseMethod(ItemStack p_52667_);

    @Inject(
            method = "dispenseFrom",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/DispenserBlock;getDispenseMethod(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    public void arsNouveau$onDispenseFromInject(ServerLevel level, BlockPos pos, CallbackInfo ci, BlockSourceImpl source, DispenserBlockEntity dispenser, int slot, ItemStack stack) {
        BlockState inFront = level.getBlockState(pos.relative(source.getBlockState().getValue(DispenserBlock.FACING)));
        if (inFront.is(BlockRegistry.MOB_JAR.get()) && stack.getItem() instanceof ShearsItem) {
            BlockPos relativePos = pos.relative(source.getBlockState().getValue(DispenserBlock.FACING));
            ANFakePlayer fakePlayer = ANFakePlayer.getPlayer(level);
            fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
            BlockRegistry.MOB_JAR.get().use(inFront, level, relativePos, fakePlayer, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(relativePos.getX(), relativePos.getY(), relativePos.getZ()), source.getBlockState().getValue(DispenserBlock.FACING), relativePos, false));
            dispenser.setItem(slot, stack);
            ci.cancel();
        }
    }
}
