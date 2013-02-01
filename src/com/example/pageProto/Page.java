package com.example.pageProto;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-1-26
 * Time: 下午1:53
 * To change this template use File | Settings | File Templates.
 */
public class Page {

    private Context context;

    private int[] textureIds;

    private FloatBuffer vertexesBuffer;
    private FloatBuffer texCoordsBuffer;
    private FloatBuffer colorBuffer;

    private List<Vertex> vertexes;

    private float[] color = {
            1f, 1f, 1f, 1f
    };

    private RectF viewRect;

    public Page(Context context) {
        this.context = context;
    }

    public void setViewRect(RectF viewRect) {
        this.viewRect = viewRect;
    }

    public void onDrawFrame(PageShader textureShader) {
        //设置数据
        this.setData();

        /**
         * 设置纹理属性，加载纹理
         */

        this.textureIds = new int[1];

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
        int width = ((int) viewRect.width() - 100 * 2);
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
                this.vertexesBuffer);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0,
                this.colorBuffer);
        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glVertexAttribPointer(aTextureCoord, 2, GLES20.GL_FLOAT, false,
                0, this.texCoordsBuffer);
        GLES20.glEnableVertexAttribArray(aTextureCoord);

        //设置纹理属性
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.textureIds[0]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, this.vertexes.size());

        GLES20.glDisable(GLES20.GL_TEXTURE_2D);
    }

    private void setData() {
        float radio = this.viewRect.width() / this.viewRect.height();
        this.vertexes = new ArrayList<Vertex>();

        //设置左上顶点
        Vertex v = new Vertex();
        v.positionX = -1 * radio;
        v.positionY = 1;
        v.positionZ = 0;

        v.textureX = 0;
        v.textureY = 0;

        this.vertexes.add(v);

        //设置左下顶点
        v = new Vertex();
        v.positionX = -1 * radio;
        v.positionY = -1;
        v.positionZ = 0;

        v.textureX = 0;
        v.textureY = 1;

        this.vertexes.add(v);

        //设置右上顶点
        v = new Vertex();
        v.positionX = 1 * radio;
        v.positionY = 1;
        v.positionZ = 0;

        v.textureX = 1;
        v.textureY = 0;

        this.vertexes.add(v);

        //设置右下顶点
        v = new Vertex();
        v.positionX = 0;
        v.positionY = -1;
        v.positionZ = 0;

        v.textureX = .5f;
        v.textureY = 1;

        this.vertexes.add(v);

        //设置右偏下顶点
        v = new Vertex();
        v.positionX = 1 * radio;
        v.positionY = 0;
        v.positionZ = 0;

        v.textureX = 1;
        v.textureY = .5f;

        this.vertexes.add(v);

        //设置顶点坐标buffer
        ByteBuffer buffer = ByteBuffer.allocateDirect(this.vertexes.size() * 3 * 4);
        buffer.order(ByteOrder.nativeOrder());
        this.vertexesBuffer = buffer.asFloatBuffer();

        buffer = ByteBuffer.allocateDirect(this.vertexes.size() * 2 * 4);
        buffer.order(ByteOrder.nativeOrder());
        this.texCoordsBuffer = buffer.asFloatBuffer();

        buffer = ByteBuffer.allocateDirect(this.vertexes.size() * 4 * 4);
        buffer.order(ByteOrder.nativeOrder());
        this.colorBuffer = buffer.asFloatBuffer();

        for (Vertex vertex : this.vertexes) {
            this.addVertex(vertex);
        }

        vertexesBuffer.position(0);
        texCoordsBuffer.position(0);
        colorBuffer.position(0);
    }

    private void addVertex(Vertex v) {
        vertexesBuffer.put(v.positionX);
        vertexesBuffer.put(v.positionY);
        vertexesBuffer.put(v.positionZ);

        texCoordsBuffer.put(v.textureX);
        texCoordsBuffer.put(v.textureY);

        colorBuffer.put(this.color);
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

    private class Vertex {
        float positionX;
        float positionY;
        float positionZ;

        float textureX;
        float textureY;
    }
}
