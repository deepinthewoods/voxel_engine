package com.niz.ui.EdgeUIs;

import com.niz.ui.elements.*;

/**
 * Created by niz on 27/05/2014.
 */
public class TestUI extends EdgeUI {
    public TestUI(){
        for (int y = 0, i = 0; y < 3; y++){
            for (int x = 0; x < 3; x++, i++){
               sides[i] = new UITable();
               sides[i].min = new UIElement[1];
               sides[i].min[0] = new ControllerSlider();
            }
            table.row();
        }
    }
}
