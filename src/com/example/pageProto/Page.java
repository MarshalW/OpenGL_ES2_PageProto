package com.example.pageProto;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-1-26
 * Time: 下午1:53
 * To change this template use File | Settings | File Templates.
 */
public class Page {

    private Context context;

    private int[] textureIds = new int[1];

    private FloatBuffer verticesBuffer;
    private FloatBuffer texCoordsBuffer;
    private FloatBuffer colorBuffer;

    private float[] pageRect = {
            -1f, 1f, 0,
            -1f, -1f, 0,
            1f, 1f, 0,
            1f, -1f, 0
    };

    private float[] color = {
            1f, 1f, 1f, 1f
    };

    private float[] texCoods = {
            0f, 0f,
            0f, 1f,
            1f, 0f,
            1f, 1f
    };


    private RectF viewRect;

    public Page(Context context) {
        this.context = context;
    }

    public void setViewRect(RectF viewRect) {
        this.viewRect = viewRect;

        float x = viewRect.width() / viewRect.height();
        for (int i = 0; i < pageRect.length; i += 3) {
            pageRect[i] = pageRect[i] * x;
        }
    }

    public void onDrawFrame(PageShader textureShader) {
        //设置数据
        this.setData();

        /**
         * 设置纹理属性，加载纹理
         */
        GLES20.glGenTextures(1, this.textureIds, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.textureIds[0]);
        int width = (int) viewRect.width() - 100 * 2;
        int height = (int) (width * (viewRect.height() / viewRect.width()));
        Bitmap texture = this.loadBitmap(width, height, R.drawable.zhufeng);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);
        texture.recycle();

        GLES20.glDisable(GLES20.GL_TEXTURE_2D);

        int aPosition = textureShader.getHandle("aPosition");
        int aColor = textureShader.getHandle("aColor");
        int aTextureCoord = textureShader.getHandle("aTextureCoord");

        //设置顶点属性
        GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 0,
                this.verticesBuffer);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0,
                this.colorBuffer);
        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glVertexAttribPointer(aTextureCoord, 2, GLES20.GL_FLOAT, false,
                0, this.texCoordsBuffer);
        GLES20.glEnableVertexAttribArray(aTextureCoord);

        //设置纹理属性
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.textureIds[0]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, this.pageRect.length / 3);

        GLES20.glDisable(GLES20.GL_TEXTURE_2D);
    }

    private void setData() {
        this.setVertices();
        this.setColor();
        this.setTexCoords();
    }

    /**
     * 设置顶点坐标
     */
    private void setVertices() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(this.pageRect.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        this.verticesBuffer = buffer.asFloatBuffer();
        verticesBuffer.put(this.pageRect);
        verticesBuffer.position(0);
    }

    /**
     * 设置顶点纹理坐标
     */
    private void setTexCoords() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(this.texCoods.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        this.texCoordsBuffer = buffer.asFloatBuffer();
        texCoordsBuffer.put(this.texCoods);
        texCoordsBuffer.position(0);
    }

    /**
     * 设置顶点颜色
     */
    private void setColor() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(this.pageRect.length * 4 * 4);
        buffer.order(ByteOrder.nativeOrder());
        this.colorBuffer = buffer.asFloatBuffer();
        for (int i = 0; i < this.pageRect.length / 3; i++) {
            this.colorBuffer.put(this.color);
        }
        this.colorBuffer.position(0);
    }

    private Bitmap loadBitmap(int width, int height, int bitmapId) {
        Bitmap b = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        b.eraseColor(0xFFFFFFFF);
        Canvas c = new Canvas(b);
        Drawable d = this.context.getResources().getDrawable(bitmapId);

        int margin = 7;
        int border = 3;
        Rect r = new Rect(margin, margin, width - margin, height - margin);

        int imageWidth = r.width() - (border * 2);
        int imageHeight = imageWidth * d.getIntrinsicHeight()
                / d.getIntrinsicWidth();
        if (imageHeight > r.height() - (border * 2)) {
            imageHeight = r.height() - (border * 2);
            imageWidth = imageHeight * d.getIntrinsicWidth()
                    / d.getIntrinsicHeight();
        }

        r.left += ((r.width() - imageWidth) / 2) - border;
        r.right = r.left + imageWidth + border + border;
        r.top += ((r.height() - imageHeight) / 2) - border;
        r.bottom = r.top + imageHeight + border + border;

        Paint p = new Paint();
        p.setColor(0xFFC0C0C0);
        c.drawRect(r, p);
        r.left += border;
        r.right -= border;
        r.top += border;
        r.bottom -= border;

        d.setBounds(r);
        d.draw(c);

        return b;
    }
}
