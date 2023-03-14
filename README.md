# NOTES:

GLSurface view
The view used in the android guide. Is like any other view(textView, ...) but is for out putting result of comming out of the render pipeline.(maybe the easiest view to use)
Other Views to use
- TextureView -> for full screen
- SurfaceView -> diy, more code, moreflexability?

To Use OpenGL using kotlin and android studio:
- You needs an Activity. This one is called OpenGLActivity
- You need to use an Intent in the MainActivity to start the OpenGLActivity
- You must define your activity in the android manifest file like MainActivity. However only the name tag is needed
- Extending GLSurvaceView is recommended for responding to events

Implementation Steps(nitty gritty):
 - init gl context
 - define a renderer (eehhh....)
 - vertex data
 - vertex & pixel shaders (at least)

TODO: Continue at: https://developer.android.com/develop/ui/views/graphics/opengl/projection

