package com.hollingsworth.arsnouveau.api.spell;

public class CastResolveType {

    public static final CastResolveType SUCCESS = new CastResolveType("success", true);
    public static final CastResolveType FAILURE = new CastResolveType("failure", false);
    public static final CastResolveType SUCCESS_NO_EXPEND = new CastResolveType("success_no_expend", true);


    public String id;
    public boolean wasSuccess; // If the spell was actually cast.

    public CastResolveType(String id, boolean wasSuccess) {
        this.id = id;
        this.wasSuccess = wasSuccess;
    }
}
