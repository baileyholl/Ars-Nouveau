package com.hollingsworth.arsnouveau.client.container;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface IAutoFillTerminal {
	List<ISearchHandler> updateSearch = new ArrayList<>();

	List<StoredItemStack> getStoredItems();

	static boolean hasSync() {
		return !updateSearch.isEmpty();
	}

	static void sync(String searchString) {
		updateSearch.forEach(c -> c.setSearch(searchString));
	}

	static String getHandlerName() {
		return updateSearch.stream().map(ISearchHandler::getName).collect(Collectors.joining(", "));
	}

	interface ISearchHandler {
		void setSearch(String set);
		String getName();
		String getSearch();
	}
}
