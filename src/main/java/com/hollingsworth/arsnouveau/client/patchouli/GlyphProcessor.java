package com.hollingsworth.arsnouveau.client.patchouli;

//public class GlyphProcessor implements IComponentProcessor {
//
//    GlyphRecipe recipe;
//
//    @Override
//    public void setup(IVariableProvider variables) {
//        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
//        String recipeID = variables.get("recipe").asString();
//        try {
//            recipe = (GlyphRecipe) manager.byKey(new ResourceLocation(recipeID)).orElse(null);
//        } catch (Exception ignored) {
//        }
//    }
//
//    @Override
//    public IVariable process(String s) {
//        if (recipe == null)
//            return null;
//
//        if (s.equals("tier"))
//            return IVariable.wrap(Component.translatable("ars_nouveau.tier").getString() + ": " + Component.translatable("ars_nouveau.spell_tier." + recipe.getSpellPart().getConfigTier().value).getString());
//        if (s.equals("schools")) {
//            AbstractSpellPart part = ((Glyph) recipe.output.getItem()).spellPart;
//            StringBuilder str = new StringBuilder(Component.translatable("ars_nouveau.spell_schools").getString() + ": ");
//            for (SpellSchool spellSchool : part.spellSchools) {
//                str.append(spellSchool.getTextComponent().getString()).append(",");
//            }
//            if (!part.spellSchools.isEmpty())
//                str = new StringBuilder(str.substring(0, str.length() - 1));
//            return IVariable.wrap(str.toString());
//        }
//        if (s.equals("mana_cost")) {
//            if (recipe.output.getItem() instanceof Glyph) {
//                int cost = ((Glyph) recipe.output.getItem()).spellPart.getCastingCost();
//                String costLang = "";
//                if (cost == 0)
//                    costLang = Component.translatable("ars_nouveau.mana_cost.none").getString();
//                if (cost < 20)
//                    costLang = Component.translatable("ars_nouveau.mana_cost.low").getString();
//                if (cost < 50)
//                    costLang = Component.translatable("ars_nouveau.mana_cost.medium").getString();
//                if (cost >= 50)
//                    costLang = Component.translatable("ars_nouveau.mana_cost.high").getString();
//                return IVariable.wrap(Component.translatable("ars_nouveau.casting_cost").getString() + ": " + costLang);
//            }
//            return IVariable.wrap("");
//        }
//        return null;
//    }
//}

