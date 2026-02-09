package com.example.timer2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public final class TextureHelper {

        private TextureHelper() {
                // Utility class
        }

        public static int loadTexture(Context context, int resourceId) {
                Bitmap bitmap = null;
                try {
                        bitmap = decodeBitmap(context, resourceId);
                        return create2DTexture(bitmap);
                } finally {
                        if (bitmap != null && !bitmap.isRecycled()) {
                                bitmap.recycle();
                        }
                }
        }

        public static int loadCubeMap(Context context, int[] resources) {
                if (resources == null || resources.length != 6) {
                        throw new IllegalArgumentException("Cube map requires exactly 6 resource ids");
                }

                final int[] textureHandle = new int[1];
                GLES20.glGenTextures(1, textureHandle, 0);

                if (textureHandle[0] == 0) {
                        throw new RuntimeException("Failed to generate cube map texture");
                }

                final int[] cubeTargets = {
                                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
                                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
                                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
                                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
                                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
                                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
                };

                GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureHandle[0]);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

                for (int i = 0; i < cubeTargets.length; i++) {
                        Bitmap bitmap = null;
                        try {
                                bitmap = decodeBitmap(context, resources[i]);
                                GLUtils.texImage2D(cubeTargets[i], 0, bitmap, 0);
                        } finally {
                                if (bitmap != null && !bitmap.isRecycled()) {
                                        bitmap.recycle();
                                }
                        }
                }

                GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, 0);
                return textureHandle[0];
        }

        private static Bitmap decodeBitmap(Context context, int resourceId) {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
                if (bitmap == null) {
                        throw new IllegalArgumentException("Unable to decode resource: " + resourceId);
                }
                return bitmap;
        }

        private static int create2DTexture(Bitmap bitmap) {
                final int[] textureHandle = new int[1];
                GLES20.glGenTextures(1, textureHandle, 0);

                if (textureHandle[0] == 0) {
                        throw new RuntimeException("Failed to generate texture handle");
                }

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                return textureHandle[0];
        }
}