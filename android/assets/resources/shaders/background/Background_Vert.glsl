#version 120

attribute vec4 a_position;
attribute vec2 a_texCoord0;
attribute vec4 a_worldPos; // xy
uniform mat4 u_projTrans;
varying vec2 v_textCoord;
varying vec3 v_color;
varying vec2 v_pos;

void main() {
    v_textCoord = a_texCoord0;
    v_pos = a_worldPos.xy;

    gl_Position = u_projTrans * a_position;
}