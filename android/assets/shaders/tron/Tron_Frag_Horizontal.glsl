#version 120

#ifdef GL_ES
precision mediump float;
#endif
varying vec2[17] v_pixelCoords;
uniform sampler2D u_texture;

void main() {
    vec4 pixel = texture2D(u_texture,v_pixelCoords[8]);

    float[10] weights;
    weights[0] = 1.0;
    weights[1] = 0.2f;
    weights[2] = 0.1f;
    weights[3] = 0.09f;
    weights[4] = 0.08f;
    weights[5] = 0.07f;
    weights[6] = 0.05f;
    weights[7] = 0.03f;
    weights[8] = 0.02f;
    weights[9] = 0.01f;

    float normalization = (weights[0] + weights[1] + weights[2] + weights[3] + weights[4] + weights[5] +
                                                weights[6] + weights[7] + weights[8] + weights[9]);
    weights[0] = weights[0] / normalization;
    weights[1] = weights[1] / normalization;
    weights[2] = weights[2] / normalization;
    weights[3] = weights[3] / normalization;
    weights[4] = weights[4] / normalization;
    weights[5] = weights[5] / normalization;
    weights[6] = weights[6] / normalization;
    weights[7] = weights[7] / normalization;
    weights[8] = weights[8] / normalization;
    weights[9] = weights[9] / normalization;

    vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
    color += texture2D(u_texture, v_pixelCoords[0]) * weights[8];
    color += texture2D(u_texture, v_pixelCoords[1]) * weights[7];
    color += texture2D(u_texture, v_pixelCoords[2]) * weights[6];
    color += texture2D(u_texture, v_pixelCoords[3]) * weights[5];
    color += texture2D(u_texture, v_pixelCoords[4]) * weights[4];
    color += texture2D(u_texture, v_pixelCoords[5]) * weights[3];
    color += texture2D(u_texture, v_pixelCoords[6]) * weights[2];
    color += texture2D(u_texture, v_pixelCoords[7]) * weights[1];
    color += pixel * weights[0];
    color += texture2D(u_texture, v_pixelCoords[9]) * weights[1];
    color += texture2D(u_texture, v_pixelCoords[10]) * weights[2];
    color += texture2D(u_texture, v_pixelCoords[11]) * weights[3];
    color += texture2D(u_texture, v_pixelCoords[12]) * weights[4];
    color += texture2D(u_texture, v_pixelCoords[13]) * weights[5];
    color += texture2D(u_texture, v_pixelCoords[14]) * weights[6];
    color += texture2D(u_texture, v_pixelCoords[15]) * weights[7];
    color += texture2D(u_texture, v_pixelCoords[16]) * weights[8];

    color.a = 0.1;
    gl_FragColor = color;
}