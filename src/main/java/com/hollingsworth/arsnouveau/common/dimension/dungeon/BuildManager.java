package com.hollingsworth.arsnouveau.common.dimension.dungeon;

import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildManager {
    ServerWorld world;
    DungeonEvent event;
    public List<BlockPos> rewardLoc = new ArrayList<>();
    List<BlockPos> currentBuild = new ArrayList<>();
    private String currentTemplate = "";
    private Template.BlockInfo centeredInfo;
    int buildIndex;
    boolean setBuild;

    public BuildManager(ServerWorld world, DungeonEvent event){

    }


    public void tick(){
        TemplateManager templatemanager = world.getStructureManager();
        if(!currentBuild.isEmpty() && !setBuild){
            for(int i = 0; i < Math.max(1,currentBuild.size() / 50); i++) {
                if (buildIndex < currentBuild.size()) {
                    world.setBlock(currentBuild.get(buildIndex), Blocks.AIR.defaultBlockState(), 2);
                } else {
                    currentBuild = new ArrayList<>();
                }
                buildIndex++;
            }
            return;
        }
        if(!setBuild) {
            for(BlockPos p : rewardLoc){
                world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
            }
            rewardLoc.clear();
            this.currentTemplate = this.currentTemplate.equals("ars_nouveau:test") ? "ars_nouveau:dirt" : "ars_nouveau:test";
            Template template = templatemanager.getOrCreate(new ResourceLocation(this.currentTemplate));
            if (template.palettes.isEmpty())
                return;
            Template.Palette palette = template.palettes.get(0);
            List<Template.BlockInfo> start = palette.blocks(Blocks.COBBLESTONE);
            this.centeredInfo = start.get(0);
            this.buildIndex = 0;
            setBuild = true;
        }
        Template template = templatemanager.getOrCreate(new ResourceLocation(this.currentTemplate));
        List<Template.BlockInfo> pallette = template.palettes.get(0).blocks();
        rewardLoc.add(new BlockPos(0, 102, 0));
        for(int i = 0; i < Math.max(1,pallette.size() / 100); i++){
            boolean foundNonAir = false;
            while(!foundNonAir){
                if(buildIndex < pallette.size()){
                    Template.BlockInfo blockInfo = pallette.get(buildIndex);
                    if(blockInfo.state.isAir()){
                        buildIndex++;
                        continue;
                    }
                    BlockPos translatedPos = DungeonManager.HOME_POS.offset(blockInfo.pos.getX(), blockInfo.pos.getY(), blockInfo.pos.getZ())
                            .offset(-centeredInfo.pos.getX(), -centeredInfo.pos.getY(), -centeredInfo.pos.getZ());
                    world.setBlock(translatedPos, blockInfo.state, 2);
                    currentBuild.add(translatedPos);
                    buildIndex++;
                    foundNonAir = true;
                }else{
                   endBuilding();
                   return;
                }
            }
        }
    }

    public void endBuilding(){
        this.buildIndex = 0;
        this.setBuild = false;
        Collections.reverse(currentBuild);
        this.event.switchState(DungeonEvent.State.COMBAT);
    }

    public BuildManager(CompoundNBT tag, ServerWorld world, DungeonEvent event){
        this.world = world;
        this.event = event;
        this.currentTemplate = tag.getString("template");
        this.setBuild = tag.getBoolean("setBuild");
        this.buildIndex = tag.getInt("buildIndex");
        int counter = 0;
        while(NBTUtil.hasBlockPos(tag, "current_b" + counter)){
            this.currentBuild.add(NBTUtil.getBlockPos(tag, "current_b" + counter));
            counter++;
        }
        counter = 0;
        while(NBTUtil.hasBlockPos(tag, "reward_loc_" + counter)){
            this.rewardLoc.add(NBTUtil.getBlockPos(tag, "reward_loc_" + counter));
            counter++;
        }
    }

    public CompoundNBT serialize(){
        CompoundNBT tag = new CompoundNBT();
        tag.putString("template", currentTemplate);
        tag.putBoolean("setBuild", setBuild);
        tag.putInt("buildIndex", buildIndex);
        for(int i = 0; i < currentBuild.size(); i++){
            NBTUtil.storeBlockPos(tag, "current_b" + i, currentBuild.get(i));
        }
        for(int i = 0; i < rewardLoc.size(); i++){
            NBTUtil.storeBlockPos(tag, "reward_loc_" + i, rewardLoc.get(i));
        }
        return tag;
    }
}
