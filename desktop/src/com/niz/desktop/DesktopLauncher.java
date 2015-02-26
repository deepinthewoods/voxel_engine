package com.niz.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.niz.DeviceCameraControl;
import com.niz.NizMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1024;
        config.height = 768;

        DeviceCameraControl cameraControl = new DeviceCameraControl() {
            @Override
            public void prepareCamera() {

            }

            @Override
            public void startPreview() {

            }

            @Override
            public void stopPreview() {

            }

            @Override
            public void takePicture() {

            }

            @Override
            public byte[] getPictureData() {
                return new byte[0];
            }

            @Override
            public void startPreviewAsync() {

            }

            @Override
            public void stopPreviewAsync() {

            }

            @Override
            public byte[] takePictureAsync(long timeout) {
                return new byte[0];
            }

            @Override
            public void saveAsJpeg(FileHandle jpgfile, Pixmap cameraPixmap) {

            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void prepareCameraAsync() {

            }
        };
        new LwjglApplication(new NizMain(new DesktopCoreInfo(), cameraControl), config);
	}
}
