package com.hollingsworth.arsnouveau.common.mixin.jar;

import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
@Mixin(DispenserBlock.class)
public abstract class DispenserMixin {
// todo: reenable jar dispenser mixin
//
//    @Shadow
//    protected abstract DispenseItemBehavior getDispenseMethod(Level pLevel, ItemStack p_52667_);
//
//    @Inject(
//            method = "dispenseFrom",
//            at = @At(
//                    value = "INVOKE_ASSIGN",
//                    target = "Lnet/minecraft/world/level/block/DispenserBlock;getDispenseMethod(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;"
//            ),
//            locals = LocalCapture.CAPTURE_FAILHARD,
//            cancellable = true
//    )
//    public void onDispenseFromInject(ServerLevel level, BlockState state, BlockPos pos, CallbackInfo ci) {
//        BlockState inFront = level.getBlockState(pos.relative(state.getValue(DispenserBlock.FACING)));
//        if (inFront.is(BlockRegistry.MOB_JAR.get()) && stack.getItem() instanceof ShearsItem) {
//            BlockPos relativePos = pos.relative(source.getBlockState().getValue(DispenserBlock.FACING));
//            ANFakePlayer fakePlayer = ANFakePlayer.getPlayer(level);
//            fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
//            BlockRegistry.MOB_JAR.get().use(inFront, level, relativePos, fakePlayer, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(relativePos.getX(), relativePos.getY(), relativePos.getZ()), source.getBlockState().getValue(DispenserBlock.FACING), relativePos, false));
//            dispenser.setItem(slot, stack);
//            ci.cancel();
//        }
//    }
}
