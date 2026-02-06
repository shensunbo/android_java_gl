package com.example.timer2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureHelper {
    public static int loadTexture(Context context, int resourceId) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            throw new RuntimeException("生成纹理失败");
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;  // 不缩放

        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options
        );

        if (bitmap == null) {
            GLES20.glDeleteTextures(1, textureHandle, 0);
            throw new RuntimeException("加载图片失败");
        }

        // 绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

        // 设置过滤
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
        );
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
        );

        // 加载纹理到 OpenGL
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textureHandle[0];
    }

    public static int loadCubeMap(Context context, int[] resources) {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            throw new RuntimeException("生成立方体贴图失败");
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final int[] ids = {
                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X,  // 右
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,  // 左
                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,  // 上
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,  // 下
                GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,  // 后
                GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z   // 前
        };

        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureHandle[0]);

        // 设置立方体贴图参数
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_CUBE_MAP,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
        );
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_CUBE_MAP,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
        );
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_CUBE_MAP,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE
        );
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_CUBE_MAP,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE
        );

        // 加载6个面的纹理
        for (int i = 0; i < 6; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(
                    context.getResources(), resources[i], options
            );

            if (bitmap == null) {
                GLES20.glDeleteTextures(1, textureHandle, 0);
                throw new RuntimeException("加载图片失败: " + resources[i]);
            }

            GLUtils.texImage2D(ids[i], 0, bitmap, 0);
            bitmap.recycle();
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, 0);

        return textureHandle[0];
    }
}