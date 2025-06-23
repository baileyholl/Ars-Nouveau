package com.hollingsworth.arsnouveau.api.particle.configurations;

import java.util.Map;

/**
 * Used to preserve WidgetProvider data inside a property without exposing client-side code to the server.
 */
public interface IWidgetProviderData {

    public Map<String, Object> getData();

}
