package com.example.pageProto;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-1-26
 * Time: 下午1:35
 * To change this template use File | Settings | File Templates.
 */
public class BookView extends GLSurfaceView {

    private BookRenderer renderer;

    public BookView(Context context) {
        super(context);
        this.init();
    }

    public BookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init(){
        this.renderer=new BookRenderer(getContext());
        this.setEGLContextClientVersion(2);//使用OpenGL2.0
        this.setRenderer(this.renderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
