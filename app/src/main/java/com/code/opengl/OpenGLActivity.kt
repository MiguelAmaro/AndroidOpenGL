package com.code.opengl


import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.content.Context
import android.app.Activity
import android.os.Bundle
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class OpenGLActivity : Activity() {
    private lateinit var gLView: GLSurfaceView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gLView = MyGLSurfaceView(this)
        setContentView(gLView)
    }

    inner class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

        private val renderer: MyGLRenderer

        init {
            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2)
            renderer = MyGLRenderer()
            // Set the Renderer for drawing on the GLSurfaceView
            setRenderer(renderer)
        }
    }

    inner class MyGLRenderer : GLSurfaceView.Renderer {

        private lateinit var mTriangle: Triangle
        //private lateinit var mSquare: Square

        override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
            // Set the background frame color
            // initialize a triangle
            mTriangle = Triangle()
            // initialize a square
            //mSquare = Square()

            GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f)
        }

        override fun onDrawFrame(unused: GL10) {
            // Redraw background color
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            mTriangle.draw()
        }

        override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
        }

        //UTILITY
        fun loadShader(type: Int, shaderCode: String): Int {

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            return GLES20.glCreateShader(type).also { shader ->

                // add the source code to the shader and compile it
                GLES20.glShaderSource(shader, shaderCode)
                GLES20.glCompileShader(shader)
            }
        }


    //SHAPES
    // number of coordinates per vertex in this array
    val COORDS_PER_VERTEX = 3
    var triangleCoords = floatArrayOf(     // in counterclockwise order:
        0.0f, 0.622008459f, 0.0f,      // top
        -0.5f, -0.311004243f, 0.0f,    // bottom left
        0.5f, -0.311004243f, 0.0f      // bottom right
    )

    inner class Triangle {

        // Set color with red, green, blue and alpha (opacity) values
        val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)
        private var positionHandle: Int = 0
        private var mColorHandle: Int = 0

        private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
        private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex


        private val vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}"

        private val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"


        private var mProgram: Int

        init {
            val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
            val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

            // create empty OpenGL ES Program
            mProgram = GLES20.glCreateProgram().also {

                // add the vertex shader to program
                GLES20.glAttachShader(it, vertexShader)

                // add the fragment shader to program
                GLES20.glAttachShader(it, fragmentShader)

                // creates OpenGL ES program executables
                GLES20.glLinkProgram(it)
            }
        }

        private var vertexBuffer: FloatBuffer =
            // (number of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
                // use the device hardware's native byte order
                order(ByteOrder.nativeOrder())

                // create a floating point buffer from the ByteBuffer
                asFloatBuffer().apply {
                    // add the coordinates to the FloatBuffer
                    put(triangleCoords)
                    // set the buffer to read the first coordinate
                    position(0)
                }
            }

            fun draw() {
                // Add program to OpenGL ES environment
                GLES20.glUseProgram(mProgram)

                // get handle to vertex shader's vPosition member
                positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {

                    // Enable a handle to the triangle vertices
                    GLES20.glEnableVertexAttribArray(it)

                    // Prepare the triangle coordinate data
                    GLES20.glVertexAttribPointer(
                        it,
                        COORDS_PER_VERTEX,
                        GLES20.GL_FLOAT,
                        false,
                        vertexStride,
                        vertexBuffer
                    )

                    // get handle to fragment shader's vColor member
                    mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                        // Set color for drawing the triangle
                        GLES20.glUniform4fv(colorHandle, 1, color, 0)
                    }

                    // Draw the triangle
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

                    // Disable vertex array
                    GLES20.glDisableVertexAttribArray(it)
                }
            }
        }
    }
}

/* NOTES:
*  GLSurface view is like any other view but is for out putting result of comming out
*  of the render pipeline.(maybe the easiest view to use)
*  Other View to use
*   - TextureView -> for full screen
*   - SurfaceView -> diy, more code, moreflexability?
*
*  You needs an Activity. This one is called OpenGLActivity
*  You need to use an Intent in the MainActivity to start the OpenGLActivity
*  You must define your activity in the android manifest file like MainActivity. However only the name tag is needed
*  Extending GLSurvaceView is recommended for responding to events
*  Steps
*  - init gl context
*  - define a renderer
*
*  TODO: Continue at: https://developer.android.com/develop/ui/views/graphics/opengl/projection
*
*
*
 */