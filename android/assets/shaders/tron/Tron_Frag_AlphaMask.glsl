#version 120

#ifdef GL_ES
precision highp float;
#endif
uniform sampler2D u_texture;
varying vec2 v_textCoord;

void main() {
    vec4 pixel = texture2D(u_texture,v_textCoord);

    if (pixel.a == 0.74901960784f) {
        pixel.a = 1.0;
        gl_FragColor = pixel;
    }
    else {
        gl_FragColor = vec4(0,0,0,0);
    }
}