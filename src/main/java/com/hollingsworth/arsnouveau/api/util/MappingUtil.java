package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.entity.item.ItemEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class MappingUtil {
    private static String ITEM_ENTITY_AGE;

    private static String  equippedProgressMainHand;
    public static void setup(){
        //Prevent 'jump' in the bobbing
        //Bobbing is calculated as the age plus the yaw
       try{
           ObfuscationReflectionHelper.findField(ItemEntity.class, "age");
           ITEM_ENTITY_AGE = "age";
       }catch (Error e){
           System.out.println("Production field for Item Entity Age not found. Attempting to set dev mapping.");
           ObfuscationReflectionHelper.findField(ItemEntity.class, "age");
           ITEM_ENTITY_AGE = "age";
       }
       try{
           ObfuscationReflectionHelper.findField(ItemEntity.class, "age");
           equippedProgressMainHand = "mainHandHeight";
       }catch (Error e ){
           System.out.println("Production field for Item Entity Age not found. Attempting to set dev mapping.");
           ObfuscationReflectionHelper.findField(ItemEntity.class, "age");
           equippedProgressMainHand = "equippedProgressMainHand";
       }
    }

    public static String getEquippedProgressMainhand(){
        return equippedProgressMainHand;
    }



    public static String getItemEntityAge() {
        return ITEM_ENTITY_AGE;
    }
}
