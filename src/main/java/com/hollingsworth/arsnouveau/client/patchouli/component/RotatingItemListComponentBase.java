package com.hollingsworth.arsnouveau.client.patchouli.component;

/**
 * Base custom Patchouli component that draws a rotating circle of items.
 * Size is 80x80. For a centered one, set X to -1.
 */
//abstract class RotatingItemListComponentBase implements ICustomComponent {
//    protected transient List<Ingredient> ingredients;
//    protected transient int x, y;
//
//    @Override
//    public void build(int componentX, int componentY, int pageNum) {
//        this.x = componentX != -1 ? componentX : 17;
//        this.y = componentY;
//        this.ingredients = makeIngredients();
//    }
//
//    protected abstract List<Ingredient> makeIngredients();
//
//    @Override
//    public void render(PoseStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
//        int degreePerInput = (int) (360F / ingredients.size());
//        int ticksElapsed = 0;
//        float currentDegree = ticksElapsed;
//        for (Ingredient input : ingredients) {
//            renderIngredientAtAngle(ms, context, currentDegree, input, mouseX, mouseY);
//
//            currentDegree += degreePerInput;
//        }
//    }
//
//    private void renderIngredientAtAngle(PoseStack ms, IComponentRenderContext context, float angle, Ingredient ingredient, int mouseX, int mouseY) {
//        if (ingredient.isEmpty()) {
//            return;
//        }
//
//        angle -= 90;
//        int radius = 32;
//        double xPos = x + Math.cos(angle * Math.PI / 180D) * radius + 32;
//        double yPos = y + Math.sin(angle * Math.PI / 180D) * radius + 32;
//
//        ms.pushPose(); // This translation makes it not stuttery. It does not affect the tooltip as that is drawn separately later.
//        ms.translate(xPos - (int) xPos, yPos - (int) yPos, 0);
//        context.renderIngredient(ms, (int) xPos, (int) yPos, mouseX, mouseY, ingredient);
//        ms.popPose();
//    }
//
//}