package com.example.timer2;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BasicGLRenderer implements GLSurfaceView.Renderer {

    private android.content.Context context;
    private int mProgram;
    private int positionHandle;
    private FloatBuffer vertexBuffer;

    // 三角形顶点坐标
    private static final float[] TRIANGLE_COORDS = {
            0.0f,  0.622008459f, 0.0f,   // 顶点
            -0.5f, -0.311004243f, 0.0f,  // 左下
            0.5f, -0.311004243f, 0.0f    // 右下
    };

    // 颜色 (R, G, B, A)
    private static float[] COLOR = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private String vertexShaderCode;
    private String fragmentShaderCode;

    private int textureResId;
    private int textureId;
    private FloatBuffer texCoordBuffer;
    private static final float[] TEX_COORDS = {
        0.5f, 0.0f, // 顶点
        0.0f, 1.0f, // 左下
        1.0f, 1.0f  // 右下
    };

    // 构造函数，接收着色器代码
    public BasicGLRenderer(android.content.Context context, String vertexShaderCode, String fragmentShaderCode, int textureResId) {
        this.context = context;
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;
        this.textureResId = textureResId;
    }

    public void setColor(float r, float g, float b, float a) {
        COLOR[0] = r;
        COLOR[1] = g;
        COLOR[2] = b;
        COLOR[3] = a;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 设置背景颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // 初始化顶点数据
        initVertexData();

        // 初始化纹理坐标数据
        initTexCoordData();

        // 编译着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // 创建程序
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        // 加载纹理（此时OpenGL上下文已激活）
        textureId = TextureHelper.loadTexture(context, textureResId);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置视口
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清除颜色缓冲区
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // 使用程序
        GLES20.glUseProgram(mProgram);

        // 获取属性位置
        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(
                positionHandle, 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer
        );

        // 获取统一变量位置
        int colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, COLOR, 0);

        // 绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        int uTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
        GLES20.glUniform1i(uTextureHandle, 0);
        // 获取纹理坐标属性位置
        int texCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        texCoordBuffer.position(0);
        GLES20.glVertexAttribPointer(
            texCoordHandle, 2, GLES20.GL_FLOAT, false, 8, texCoordBuffer
        );

        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        // 禁用顶点数组
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    /** 随机颜色 (RGBA)，供外部调用后请求重绘。 */
    public void randomizeColor() {
        COLOR[0] = (float) Math.random();
        COLOR[1] = (float) Math.random();
        COLOR[2] = (float) Math.random();
        COLOR[3] = 1.0f;
    }

    private void initVertexData() {
        // 分配本地内存空间
        ByteBuffer bb = ByteBuffer.allocateDirect(TRIANGLE_COORDS.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(TRIANGLE_COORDS);
        vertexBuffer.position(0);
    }

    private void initTexCoordData() {
        // 分配本地内存空间
        ByteBuffer bb = ByteBuffer.allocateDirect(TEX_COORDS.length * 4);
        bb.order(ByteOrder.nativeOrder());
        texCoordBuffer = bb.asFloatBuffer();
        texCoordBuffer.put(TEX_COORDS);
        texCoordBuffer.position(0);
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        // 检查编译状态
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            String log = GLES20.glGetShaderInfoLog(shader);
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("着色器编译失败: " + log);
        }
        return shader;
    }
}
