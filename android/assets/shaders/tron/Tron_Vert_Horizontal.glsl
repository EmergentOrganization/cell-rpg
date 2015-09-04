#version 120

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
attribute vec4 a_resolution;
uniform mat4 u_projTrans;
varying vec2[17] v_pixelCoords;

void main() {
    gl_Position =  u_projTrans * a_position;

    float v_texelSize = 1.0f / a_resolution.x;
    v_pixelCoords[0] = a_texCoord0 + vec2(v_texelSize * -8.0f, 0.0);
    v_pixelCoords[1] = a_texCoord0 + vec2(v_texelSize * -7.0f, 0.0);
    v_pixelCoords[2] = a_texCoord0 + vec2(v_texelSize * -6.0f, 0.0);
    v_pixelCoords[3] = a_texCoord0 + vec2(v_texelSize * -5.0f, 0.0);
    v_pixelCoords[4] = a_texCoord0 + vec2(v_texelSize * -4.0f, 0.0);
    v_pixelCoords[5] = a_texCoord0 + vec2(v_texelSize * -3.0f, 0.0);
    v_pixelCoords[6] = a_texCoord0 + vec2(v_texelSize * -2.0f, 0.0);
    v_pixelCoords[7] = a_texCoord0 + vec2(v_texelSize * -1.0f, 0.0);
    v_pixelCoords[8] = a_texCoord0;
    v_pixelCoords[9] = a_texCoord0 + vec2(v_texelSize * 1.0f, 0.0);
    v_pixelCoords[10] = a_texCoord0 + vec2(v_texelSize * 2.0f, 0.0);
    v_pixelCoords[11] = a_texCoord0 + vec2(v_texelSize * 3.0f, 0.0);
    v_pixelCoords[12] = a_texCoord0 + vec2(v_texelSize * 4.0f, 0.0);
    v_pixelCoords[13] = a_texCoord0 + vec2(v_texelSize * 5.0f, 0.0);
    v_pixelCoords[14] = a_texCoord0 + vec2(v_texelSize * 6.0f, 0.0);
    v_pixelCoords[15] = a_texCoord0 + vec2(v_texelSize * 7.0f, 0.0);
    v_pixelCoords[16] = a_texCoord0 + vec2(v_texelSize * 8.0f, 0.0);
}