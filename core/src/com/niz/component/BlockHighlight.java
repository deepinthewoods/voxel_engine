package com.niz.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.g3d.voxel.BlockDefinition;

/**
 * Created by niz on 07/06/2014.
 */
public class BlockHighlight implements Component{
    public Vector3 size = new Vector3(1.008f,1.008f,1.008f);
    public int face = BlockDefinition.ALL;
    public transient Mesh mesh = new Mesh(true,
            8,
            24,
            VertexAttribute.Position(),
            VertexAttribute.Color()
    );
    public Color color = new Color(Color.DARK_GRAY);
    public boolean dirty;

    @Override
    public void reset() {

    }
}
