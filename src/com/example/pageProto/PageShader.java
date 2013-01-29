package com.example.pageProto;

import android.opengl.GLES20;
import android.util.Log;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-1-27
 * Time: 上午9:59
 * To change this template use File | Settings | File Templates.
 */
public class PageShader {

    private static String TAG = "page.PageShader";

    private int program = -1;

    private final HashMap<String, Integer> shaderHandleMap = new HashMap<String, Integer>();

    public void setProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        this.program = GLES20.glCreateProgram();

        if (this.program != 0) {
            GLES20.glAttachShader(this.program, vertexShader);
            GLES20.glAttachShader(this.program, fragmentShader);
            GLES20.glLinkProgram(this.program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(this.program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                String error = GLES20.glGetProgramInfoLog(this.program);
                GLES20.glDeleteProgram(this.program);
                throw new RuntimeException(error);
            }
        }

        this.shaderHandleMap.clear();
    }

    public void useProgram() {
        GLES20.glUseProgram(this.program);
    }

    public int getHandle(String name) {
        if (shaderHandleMap.containsKey(name)) {
            return shaderHandleMap.get(name);
        }
        int handle = GLES20.glGetAttribLocation(this.program, name);
        if (handle == -1) {
            handle = GLES20.glGetUniformLocation(this.program, name);
        }
        if (handle != -1) {
            this.shaderHandleMap.put(name,handle);
        } else {
            Log.d(TAG, "can not find attribute for" + name);
        }

        return handle;
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);

        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                String error = GLES20.glGetShaderInfoLog(shader);
                GLES20.glDeleteShader(shader);
                throw new RuntimeException(error);
            }
        }
        return shader;
    }
}
