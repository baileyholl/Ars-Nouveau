package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocCategory;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IndexScreen extends BaseDocScreen {

    List<DocCategory> categoryList;
    List<DocSectionButton> sections = new ArrayList<>();

    public IndexScreen(Collection<DocCategory> categories) {
        super();
        categoryList = new ArrayList<>(categories);
        this.maxArrowIndex = (categoryList.size() - 1) / 10;
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

    public void initSections(){
        for(DocSectionButton section : sections){
            removeWidget(section);
        }
        sections.clear();
        List<DocCategory> sliced = categoryList.subList(arrowIndex * 10, Math.min((arrowIndex + 1) * 10, categoryList.size()));
        for(int i = 0; i < sliced.size(); i++){
            DocCategory category = sliced.get(i);
            var button = new DocSectionButton(bookLeft + 18 + (i > 4 ? 135 : 0), bookTop + 24 + 29 * (i > 4 ? i - 5 : i), category.getTitle(), category.renderIcon(), (b) -> {
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
