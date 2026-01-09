package org.jhotdraw.bdd;

import com.tngtech.jgiven.Stage;

public class GivenDrawingApp extends Stage<GivenDrawingApp> {
    public GivenDrawingApp the_app_is_open(){
        return this;
    }

    public GivenDrawingApp polygon_tool_is_selected() {
        return this;
    }
}
