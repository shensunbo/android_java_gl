package com.example.timer2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.util.Log;
import android.opengl.GLSurfaceView;

public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private Runnable updateTimeTask;
    private Test test;
    private GLSurfaceView glSurfaceView;
    private BasicGLRenderer renderer;

    private float[] initColor = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    // 顶点着色器和片元着色器代码移到 assets 目录下的文件
    private static String loadShaderCodeFromAsset(String filename, android.content.Context context) {
        try (java.io.InputStream is = context.getAssets().open(filename)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            return new String(buffer);
        } catch (Exception e) {
            throw new RuntimeException("无法加载着色器文件: " + filename, e);
        }
    }

    private String vertexShaderCode;
    private String fragmentShaderCode;
    private int textureResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        glSurfaceView = findViewById(R.id.glSurfaceView);

        // Set up OpenGL surface and renderer
        glSurfaceView.setEGLContextClientVersion(2);
        // 加载着色器代码
        String vertexShaderCode = loadShaderCodeFromAsset("vertex_shader.glsl", this);
        String fragmentShaderCode = loadShaderCodeFromAsset("fragment_shader.glsl", this);
        // 只传递图片资源ID，不直接加载纹理
        textureResId = R.drawable.front;
        renderer = new BasicGLRenderer(this, vertexShaderCode, fragmentShaderCode, textureResId);
        glSurfaceView.setRenderer(renderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        Log.i("MainActivity", "String resource ID: " + R.string.greeting_message);

        // 移除 updateTimeTask 和 Toast/greetingText 相关代码
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimeTask);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MainActivity", "onStart called");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "onResume called");
        if (glSurfaceView != null) {
            glSurfaceView.onResume();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainActivity", "onPause called");
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MainActivity", "onStop called");
    }
}