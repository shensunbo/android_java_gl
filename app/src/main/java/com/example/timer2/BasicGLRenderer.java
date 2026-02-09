package com.example.timer2;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class BasicGLRenderer implements GLSurfaceView.Renderer {

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int TEX_COORD_COMPONENT_COUNT = 2;
    private static final int POSITION_STRIDE = POSITION_COMPONENT_COUNT * FLOAT_SIZE_BYTES;
    private static final int TEX_COORD_STRIDE = TEX_COORD_COMPONENT_COUNT * FLOAT_SIZE_BYTES;

    // Rectangle vertex coordinates (composed of 2 triangles)
    private static final float[] TRIANGLE_COORDS = {
            -1.0f,  0.5f, 0.0f,   // top-left
            -1.0f, -0.5f, 0.0f,   // bottom-left
             1.0f, -0.5f, 0.0f,   // bottom-right
            -1.0f,  0.5f, 0.0f,   // top-left
             1.0f, -0.5f, 0.0f,   // bottom-right
             1.0f,  0.5f, 0.0f    // top-right
    };

    private static final float[] TEX_COORDS = {
            0.0f, 0.0f, // top-left
            0.0f, 1.0f, // bottom-left
            1.0f, 1.0f, // bottom-right
            0.0f, 0.0f, // top-left
            1.0f, 1.0f, // bottom-right
            1.0f, 0.0f  // top-right
    };

    private final android.content.Context context;
    private final String vertexShaderCode;
    private final String fragmentShaderCode;
    private final int textureResId;

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer texCoordBuffer;
    private final float[] color = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private int programId;
    private int textureId;
    private int aPositionLocation;
    private int aTexCoordLocation;
    private int uColorLocation;
    private int uTextureLocation;

    // Constructor, receives shader code
    public BasicGLRenderer(android.content.Context context, String vertexShaderCode, String fragmentShaderCode, int textureResId) {
        this.context = context;
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;
        this.textureResId = textureResId;
        this.vertexBuffer = createNativeFloatBuffer(TRIANGLE_COORDS);
        this.texCoordBuffer = createNativeFloatBuffer(TEX_COORDS);
    }

    public void setColor(float r, float g, float b, float a) {
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        buildProgram();
        fetchShaderHandles();
        textureId = TextureHelper.loadTexture(context, textureResId);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置视口
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
    if (programId == 0) {
        return;
    }

    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    GLES20.glUseProgram(programId);

    vertexBuffer.position(0);
    GLES20.glEnableVertexAttribArray(aPositionLocation);
    GLES20.glVertexAttribPointer(
        aPositionLocation,
        POSITION_COMPONENT_COUNT,
        GLES20.GL_FLOAT,
        false,
        POSITION_STRIDE,
        vertexBuffer
    );

    texCoordBuffer.position(0);
    GLES20.glEnableVertexAttribArray(aTexCoordLocation);
    GLES20.glVertexAttribPointer(
        aTexCoordLocation,
        TEX_COORD_COMPONENT_COUNT,
        GLES20.GL_FLOAT,
        false,
        TEX_COORD_STRIDE,
        texCoordBuffer
    );

    GLES20.glUniform4fv(uColorLocation, 1, color, 0);

    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
    GLES20.glUniform1i(uTextureLocation, 0);

    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

    GLES20.glDisableVertexAttribArray(aPositionLocation);
    GLES20.glDisableVertexAttribArray(aTexCoordLocation);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    /** Random color (RGBA), call externally then request render. */
    public void randomizeColor() {
        setColor((float) Math.random(), (float) Math.random(), (float) Math.random(), 1.0f);
    }

    public void release() {
        if (textureId != 0) {
            final int[] textures = { textureId };
            GLES20.glDeleteTextures(1, textures, 0);
            textureId = 0;
        }

        if (programId != 0) {
            GLES20.glDeleteProgram(programId);
            programId = 0;
        }
    }

    private void buildProgram() {
        final int vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        final int fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        final int program = GLES20.glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Unable to create OpenGL program");
        }

        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            final String log = GLES20.glGetProgramInfoLog(program);
            GLES20.glDeleteProgram(program);
            throw new RuntimeException("Program linking failed: " + log);
        }

        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
        programId = program;
    }

    private void fetchShaderHandles() {
        if (programId == 0) {
            throw new IllegalStateException("Program must be linked before fetching handles");
        }

        aPositionLocation = GLES20.glGetAttribLocation(programId, "vPosition");
        aTexCoordLocation = GLES20.glGetAttribLocation(programId, "aTexCoord");
        uColorLocation = GLES20.glGetUniformLocation(programId, "vColor");
        uTextureLocation = GLES20.glGetUniformLocation(programId, "uTexture");
    }

    private static FloatBuffer createNativeFloatBuffer(float[] data) {
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * FLOAT_SIZE_BYTES);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(data);
        buffer.position(0);
        return buffer;
    }

    private static int compileShader(int type, String shaderCode) {
        final int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            throw new RuntimeException("Unable to create shader of type " + type);
        }

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        final int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            final String log = GLES20.glGetShaderInfoLog(shader);
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("Shader compilation failed: " + log);
        }

        return shader;
    }
}
