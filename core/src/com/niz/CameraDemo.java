/*
 * Copyright 2012 Johnny Lish (johnnyoneeyed@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.niz;

import java.nio.ByteBuffer;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.utils.Array;
import com.niz.component.DeviceCamera;
import com.niz.ui.ColorPicker;

public class CameraDemo extends EntitySystem {

    private Pixmap pixmap = null;
    private Color color = new Color()
            ;

    @Override
    protected void processEntities(Array<Entity> entities) {
        if (Gdx.input.isTouched()) {
            if (mode == Mode.normal) {
                mode = Mode.prepare;
                if (deviceCameraControl != null) {
                    deviceCameraControl.prepareCameraAsync();
                }
            }
        } else { // touch removed
            if (mode == Mode.preview) {
                mode = Mode.takePicture;
            }
        }

        //Gdx.gl.glHint(GL20.GL_PERSPECTIVE_CORRECTION_HINT, GL20.GL_NICEST);
        if (mode == Mode.takePicture) {
            Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            if (deviceCameraControl != null) {
                deviceCameraControl.takePicture();
            }
            mode = Mode.waitForPictureReady;
        } else if (mode == Mode.waitForPictureReady) {
            Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        } else if (mode == Mode.prepare) {
            Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            if (deviceCameraControl != null) {
                if (deviceCameraControl.isReady()) {
                    deviceCameraControl.startPreviewAsync();
                    mode = Mode.preview;
                }
            }
        } else if (mode == Mode.preview) {
            Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        } else { // mode = normal
            Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_TEXTURE);
        Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
        Gdx.gl.glClearDepthf(1.0F);

        if (mode == Mode.waitForPictureReady) {
            if (deviceCameraControl.getPictureData() != null) { // camera picture was actually taken
                // take Gdx Screenshot
                //Pixmap screenshotPixmap = getScreenshot(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
                if (pixmap != null){
                    pixmap.dispose();
                    pixmap = null;
                }
                pixmap = new Pixmap(deviceCameraControl.getPictureData(), 0, deviceCameraControl.getPictureData().length);
                processPixmap(pixmap);
                if (texture != null){
                    texture.dispose();
                    texture = null;
                }
                texture = new Texture(pixmap);
                //merge2Pixmaps(cameraPixmap, screenshotPixmap);
                // we could call PixmapIO.writePNG(pngfile, cameraPixmap);
                //FileHandle jpgfile = Gdx.files.external("libGdxSnapshot.jpg");
                //deviceCameraControl.saveAsJpeg(jpgfile, cameraPixmap);

                deviceCameraControl.stopPreviewAsync();
                mode = Mode.normal;
            }
        }
    }

    private void processPixmap(Pixmap p) {
        for (int x = 0; x < p.getWidth(); x++) {
            int hueVariations = 0;
            float lastHue;
            for (int y = 0; y < p.getHeight(); y++) {
                int pixel = p.getPixel(x, y);
                color.set(pixel);
                float[] hsl = new float[4];
                ColorPicker.RGBtoHSL(color, hsl);
                ColorPicker.HSLtoRGB(hsl[0], 1.0f, hsl[2], color);
                p.drawPixel(x, y, color.toIntBits());
            }

        }
    }

    public enum Mode {
        normal,
        prepare,
        preview,
        takePicture,
        waitForPictureReady,
    }

    public static final float vertexData[] = {
            1.0f,  1.0f,  1.0f, Color.toFloatBits(255,255,255,255),  0.0f, 0.0f, // quad/face 0/Vertex 0 
            0.0f,  1.0f,  1.0f, Color.toFloatBits(255,255,255,255),  0.0f, 1.0f, // quad/face 0/Vertex 1
            0.0f,  0.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 1.0f, // quad/face 0/Vertex 2
            1.0f,  0.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 0.0f, // quad/face 0/Vertex 3

            1.0f,  1.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 1.0f, // quad/face 1/Vertex 4
            1.0f,  0.0f,  1.0f, Color.toFloatBits(255,255,255,255),  0.0f, 1.0f, // quad/face 1/Vertex 5
            1.0f,  0.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 0.0f, // quad/face 1/Vertex 6
            1.0f,  1.0f,  0.0f, Color.toFloatBits(255,255,255,255),  1.0f, 0.0f, // quad/face 1/Vertex 7

            1.0f,  1.0f,  1.0f, Color.toFloatBits(255,255,255,255),  0.0f, 0.0f, // quad/face 2/Vertex 8
            1.0f,  1.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 1.0f, // quad/face 2/Vertex 9
            0.0f,  1.0f,  0.0f, Color.toFloatBits(255,255,255,255),  1.0f, 1.0f, // quad/face 2/Vertex 10
            0.0f,  1.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 0.0f, // quad/face 2/Vertex 11

            1.0f,  0.0f,  0.0f, Color.toFloatBits(255,255,255,255),  1.0f, 1.0f, // quad/face 3/Vertex 12
            0.0f,  0.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 1.0f, // quad/face 3/Vertex 13
            0.0f,  1.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 0.0f, // quad/face 3/Vertex 14
            1.0f,  1.0f,  0.0f, Color.toFloatBits(255,255,255,255),  1.0f, 0.0f, // quad/face 3/Vertex 15

            0.0f,  1.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 1.0f, // quad/face 4/Vertex 16
            0.0f,  1.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 1.0f, // quad/face 4/Vertex 17
            0.0f,  0.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 0.0f, // quad/face 4/Vertex 18
            0.0f,  0.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 0.0f, // quad/face 4/Vertex 19

            0.0f,  0.0f,  0.0f, Color.toFloatBits(255,255,255,255),  1.0f, 1.0f, // quad/face 5/Vertex 20
            1.0f,  0.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 1.0f, // quad/face 5/Vertex 21
            1.0f,  0.0f,  1.0f, Color.toFloatBits(255,255,255,255),  0.0f, 0.0f, // quad/face 5/Vertex 22
            0.0f,  0.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 0.0f, // quad/face 5/Vertex 23
    };


    public static final short facesVerticesIndex[][] = {
            { 0, 1, 2, 3 },
            { 4, 5, 6, 7 },
            { 8, 9, 10, 11 },
            { 12, 13, 14, 15 },
            { 16, 17, 18, 19 },
            { 20, 21, 22, 23 }
    };

    private final static VertexAttribute verticesAttributes[] = new VertexAttribute[] {
            new VertexAttribute(Usage.Position, 3, "a_position"),
            new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
            new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"),
    };


    private Texture texture;






    private Mode mode = Mode.normal;


    private final DeviceCameraControl deviceCameraControl;


    public CameraDemo(DeviceCameraControl cameraControl) {
        super(Aspect.getAspectForAll(DeviceCamera.class));
        this.deviceCameraControl = cameraControl;
    }



   //TODO
    public void dispose() {
        texture.dispose();
       /* for (int i=0;i<6;i++) {
            mesh[i].dispose();
            mesh[i] = null;
        }*/
        texture = null;
    }



    private Pixmap merge2Pixmaps(Pixmap mainPixmap, Pixmap overlayedPixmap) {
        // merge to data and Gdx screen shot - but fix Aspect Ratio issues between the screen and the camera
        Pixmap.setFilter(Filter.BiLinear);
        float mainPixmapAR = (float)mainPixmap.getWidth() / mainPixmap.getHeight();
        float overlayedPixmapAR = (float)overlayedPixmap.getWidth() / overlayedPixmap.getHeight();
        if (overlayedPixmapAR < mainPixmapAR) {
            int overlayNewWidth = (int)(((float)mainPixmap.getHeight() / overlayedPixmap.getHeight()) * overlayedPixmap.getWidth());
            int overlayStartX = (mainPixmap.getWidth() - overlayNewWidth)/2;
            // Overlaying pixmaps
            mainPixmap.drawPixmap(overlayedPixmap, 0, 0, overlayedPixmap.getWidth(), overlayedPixmap.getHeight(), overlayStartX, 0, overlayNewWidth, mainPixmap.getHeight());
        } else {
            int overlayNewHeight = (int)(((float)mainPixmap.getWidth() / overlayedPixmap.getWidth()) * overlayedPixmap.getHeight());
            int overlayStartY = (mainPixmap.getHeight() - overlayNewHeight)/2;
            // Overlaying pixmaps
            mainPixmap.drawPixmap(overlayedPixmap, 0, 0, overlayedPixmap.getWidth(), overlayedPixmap.getHeight(), 0, overlayStartY, mainPixmap.getWidth(), overlayNewHeight);
        }
        return mainPixmap;
    }

    public Pixmap getScreenshot(int x, int y, int w, int h, boolean flipY) {
        Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);

        final Pixmap pixmap = new Pixmap(w, h, Format.RGBA8888);
        ByteBuffer pixels = pixmap.getPixels();
        Gdx.gl.glReadPixels(x, y, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);

        final int numBytes = w * h * 4;
        byte[] lines = new byte[numBytes];
        if (flipY) {
            final int numBytesPerLine = w * 4;
            for (int i = 0; i < h; i++) {
                pixels.position((h - i - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
            pixels.clear();
            pixels.put(lines);
        } else {
            pixels.clear();
            pixels.get(lines);
        }

        return pixmap;
    }


}