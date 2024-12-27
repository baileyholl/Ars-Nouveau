package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IndexScreen extends BaseDocScreen {

    List<DocCategory> categoryList;
    List<DocSectionButton> sections = new ArrayList<>();

    public IndexScreen(Collection<DocCategory> categories) {
        super();
        categoryList = new ArrayList<>(categories);
        this.maxArrowIndex = (categoryList.size() - 1) / 5;
    }

    public IndexScreen() {
        this(DocumentationRegistry.getMainCategoryMap().values().stream().sorted().toList());
    }

    @Override
    public void init() {
        super.init();
        initSections();
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new IndexScreen());
    }


    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        DocClientUtils.blit(graphics, DocAssets.SPLASH_FRAME, bookLeft + LEFT_PAGE_OFFSET , bookTop + PAGE_TOP_OFFSET -12);

    }

    public void initSections(){
        for(DocSectionButton section : sections){
            removeWidget(section);
        }
        sections.clear();
        List<DocCategory> sliced = categoryList.subList(arrowIndex * 5, Math.min((arrowIndex + 1) * 5, categoryList.size()));
        for(int i = 0; i < sliced.size(); i++){
            DocCategory category = sliced.get(i);
            var button = new DocSectionButton(bookLeft + 18 + 135, bookTop + 24 + 29 * (i > 4 ? i - 5 : i), category.getTitle(), category.renderIcon(), (b) -> {
                if(!category.subCategories().isEmpty()){
                    transition(new IndexScreen(category.subCategories()));
                }else{
                    transition(new EntriesScreen(category));
                }
            });
            addRenderableWidget(button);
            sections.add(button);
        }
    }

    @Override
    public void onArrowIndexChange() {
        super.onArrowIndexChange();
        initSections();
    }
}
