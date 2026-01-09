package org.jhotdraw.bdd;

import com.tngtech.jgiven.junit.ScenarioTest;
import org.testng.annotations.Test;

public class PolygonDrawingBDDTest extends ScenarioTest<GivenDrawingApp, WhenUserDrawsPolygon, ThenPolygonIsCreated> {
    @Test
    public void user_can_draw_polygon(){
        given()
                .the_app_is_open()
                .and()
                .polygon_tool_is_selected();
        when()
                .user_clicks_three_points()
                .and()
                .user_closes_polygon();
        then()
                .closed_polygon_should_be_created();
    }
}
