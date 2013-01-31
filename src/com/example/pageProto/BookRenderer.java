package com.example.pageProto;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-1-26
 * Time: 下午1:37
 * To change this template use File | Settings | File Templates.
 */
public class BookRenderer implements GLSurfaceView.Renderer {

    private static int backgroundColor = 0xFF000000;

    /**
     * 片段着色器
     */
    private static final String SHADER_TEXTURE_FRAGMENT =
            "precision mediump float;\n" +
                    "varying vec4 vColor;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform sampler2D sTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "  gl_FragColor.rgb *= vColor.rgb;\n" +
                    "  gl_FragColor = mix(vColor, gl_FragColor, vColor.a);\n" +
                    "  gl_FragColor.a = 1.0;\n" +
                    "}\n";
    /**
     * 顶点着色器
     */
    private static final String SHADER_TEXTURE_VERTEX =
            "uniform mat4 uProjectionM;\n" +
                    "attribute vec3 aPosition;\n" +
                    "attribute vec4 aColor;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "varying vec4 vColor;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = uProjectionM * vec4(aPosition, 1.0);\n" +
                    "  vColor = aColor;\n" +
                    "  vTextureCoord = aTextureCoord;\n" +
                    "}\n";

    private Page page;

    private int lastWidth = -1;

    private PageShader textureShader = new PageShader();

    private final float[] projectionMatrix = new float[16];

    public BookRenderer(Context context) {
        this.page = new Page(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Log.d("glDemo", ">>>>on surface created");
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        this.textureShader.setProgram(SHADER_TEXTURE_VERTEX, SHADER_TEXTURE_FRAGMENT);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        Log.d("glDemo", ">>>>on surface changed, width:" + width + ", height:" + height);

        if (lastWidth != width) {
            //设置视口为全屏
            GLES20.glViewport(0, 0, width, height);
            page.setViewRect(new RectF(0, 0, width, height));

            //使用正交投影，设置长宽比
            float ratio = (float) width / height;
            Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, -10f, 10f);

            lastWidth = width;
        }

        this.textureShader.useProgram();
        GLES20.glUniformMatrix4fv(this.textureShader.getHandle("uProjectionM"), 1, false, this.projectionMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        Log.d("glDemo", "render on draw frame");
        GLES20.glClearColor(Color.red(backgroundColor) / 255f,
                Color.green(backgroundColor) / 255f,
                Color.blue(backgroundColor) / 255f,
                Color.alpha(backgroundColor) / 255f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT |
                GLES20.GL_DEPTH_BUFFER_BIT);

        this.page.onDrawFrame(this.textureShader);//
    }
}
