package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.ritual.RitualContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import com.hollingsworth.arsnouveau.common.entity.EntityRitualProjectile;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;

import static com.hollingsworth.arsnouveau.api.util.BlockUtil.destroyBlockSafely;

public class RitualDig extends AbstractRitual {

    public RitualDig(){
        super();
    }

    public RitualDig(RitualTile tile, RitualContext context) {
        super(tile, context);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(tile == null)
            return;
        EntityRitualProjectile ritualProjectile = new EntityRitualProjectile(getWorld(), getPos().above());
        ritualProjectile.setPos(ritualProjectile.getX() + 0.5, ritualProjectile.getY(), ritualProjectile.getZ() +0.5);
        ritualProjectile.tilePos = getPos();
        getWorld().addFreshEntity(ritualProjectile);
    }

    public boolean canBlockBeHarvested(BlockPos pos){
        return getWorld().getBlockState(pos).getDestroySpeed(getWorld(), pos) >= 0 && 5 >= getWorld().getBlockState(pos).getHarvestLevel();
    }

    public void breakBlock(BlockPos pos){
        if(!canBlockBeHarvested(pos) || !BlockUtil.destroyRespectsClaim( FakePlayerFactory.getMinecraft((ServerWorld) getWorld()), getWorld(), pos)){
            return;
        }
        BlockState state = getWorld().getBlockState(pos);
        ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
        state.getBlock().playerDestroy(getWorld(), FakePlayerFactory.getMinecraft((ServerWorld) getWorld()), pos, getWorld().getBlockState(pos), getWorld().getBlockEntity(pos), stack);
        destroyBlockSafely(getWorld(), pos, false,  FakePlayerFactory.getMinecraft((ServerWorld) getWorld()));
    }

    @Override
    public void tick() {
        World world = tile.getLevel();
        if(world.getGameTime() % 20 == 0 && !world.isClientSide){
            BlockPos pos = tile.getBlockPos().north().below(getContext().progress);
            if(pos.getY() < 1){
                onEnd();
                return;
            }
            breakBlock(pos);
            breakBlock(pos.south().south());
            breakBlock(pos.south().east());
            breakBlock(pos.south().west());
            getContext().progress++;

        }
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(
                rand.nextInt(50),
                rand.nextInt(255),
                rand.nextInt(20));
    }

    @Override
    public String getID() {
        return RitualLib.DIG;
    }

    @Override
    public String getLangDescription() {
        return "Digs four adjacent holes to bedrock, dropping any blocks.";
    }

    @Override
    public String getLangName() {
        return "Burrowing";
    }
}
