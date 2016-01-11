#version 120

#ifdef GL_ES
precision mediump float;
#endif
uniform sampler2D u_texture;
uniform vec3 u_maskColor;
varying vec2 v_textCoord;
const float EPSILON = 0.1f;

void main() {
    vec4 pixel = texture2D(u_texture,v_textCoord);
    vec3 diff = abs(pixel.rgb - u_maskColor);
    if (all(lessThan(diff, vec3(EPSILON)))) {
        gl_FragColor = pixel;
    }
    else {
        gl_FragColor = vec4(0);
    }
}