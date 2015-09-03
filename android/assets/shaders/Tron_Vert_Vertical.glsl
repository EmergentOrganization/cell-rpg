#version 120

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
attribute vec4 a_resolution;
uniform mat4 u_projTrans;
varying vec2[9] v_pixelCoords;

void main() {
    gl_Position =  u_projTrans * a_position;

    float v_texelSize = 1.0f / a_resolution.y;
    v_pixelCoords[0] = a_texCoord0 + vec2(0.0, v_texelSize * -4.0f);
    v_pixelCoords[1] = a_texCoord0 + vec2(0.0, v_texelSize * -3.0f);
    v_pixelCoords[2] = a_texCoord0 + vec2(0.0, v_texelSize * -2.0f);
    v_pixelCoords[3] = a_texCoord0 + vec2(0.0, v_texelSize * -1.0f);
    v_pixelCoords[4] = a_texCoord0;
    v_pixelCoords[5] = a_texCoord0 + vec2(0.0, v_texelSize * 1.0f);
    v_pixelCoords[6] = a_texCoord0 + vec2(0.0, v_texelSize * 2.0f);
    v_pixelCoords[7] = a_texCoord0 + vec2(0.0, v_texelSize * 3.0f);
    v_pixelCoords[8] = a_texCoord0 + vec2(0.0, v_texelSize * 4.0f);
}