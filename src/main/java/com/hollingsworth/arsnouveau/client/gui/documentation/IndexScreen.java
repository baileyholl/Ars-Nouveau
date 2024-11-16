package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.nuggets.client.gui.BaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class IndexScreen extends BaseScreen {

    public static ResourceLocation background = ArsNouveau.prefix("textures/gui/spell_book_template.png");

    public IndexScreen() {
        super(Component.empty(), 290, 194, background);
    }

    @Override
    public void init() {
        super.init();
        List<DocCategory> categoryList = DocumentationRegistry.getCategoryMap().values().stream().sorted().toList();
        for(int i = 0; i < categoryList.size(); i++){
            DocCategory category = categoryList.get(i);
            addRenderableWidget(new DocSectionButton(bookLeft + 18, bookTop + 24 + 30 * i, category.getTitle(), category.renderIcon(), (button) -> {
                System.out.println("hi");
            }));
        }
//        addRenderableWidget(new DocSectionButton(bookLeft + 18, bookTop + 24, Component.literal("Getting Started"), MethodProjectile.INSTANCE.glyphItem.getDefaultInstance(), (button) -> {
//            System.out.println("hi");
//        }));
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new IndexScreen());
    }
}
