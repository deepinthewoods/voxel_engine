package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by niz on 07/06/2014.
 */
public class BlockHighlight implements Component{
    public Vector3 size = new Vector3(1.08f,1.08f,1.08f);
    public int face;
    public transient Mesh mesh = new Mesh(true,
            8,
            24,
            VertexAttribute.Position(),
            VertexAttribute.Color()
    );
    public Color color = new Color(Color.DARK_GRAY)
            ;
    public boolean dirty;

    @Override
    public void reset() {

    }
}
