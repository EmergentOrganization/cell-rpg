#version 120

attribute vec4 a_position;
uniform mat4 u_projTrans;
attribute vec2 a_texCoord0;
varying vec2 v_textCoord;

void main() {
    gl_Position =  u_projTrans * a_position;
    v_textCoord = a_texCoord0;
}