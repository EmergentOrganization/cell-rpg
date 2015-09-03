#version 120

#ifdef GL_ES
precision mediump float;
#endif
varying vec2[9] v_pixelCoords;
uniform sampler2D u_texture;

void main() {
    vec4 pixel = texture2D(u_texture,v_pixelCoords[4]);

    if (pixel.a == 0.99607843137f) { // assumption for white shades
        float[5] weights;
        weights[0] = 1.0;
        weights[1] = 0.9f;
        weights[2] = 0.6f;
        weights[3] = 0.5f;
        weights[4] = 0.3f;

        float normalization = (weights[0] + 2.0f * (weights[1] + weights[2] + weights[3] + weights[4]));
        weights[0] = weights[0] / normalization;
        weights[1] = weights[1] / normalization;
        weights[2] = weights[2] / normalization;
        weights[3] = weights[3] / normalization;
        weights[4] = weights[4] / normalization;

        vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
        color += texture2D(u_texture, v_pixelCoords[0]) * weights[4];
        color += texture2D(u_texture, v_pixelCoords[1]) * weights[3];
        color += texture2D(u_texture, v_pixelCoords[2]) * weights[2];
        color += texture2D(u_texture, v_pixelCoords[3]) * weights[1];
        color += pixel * weights[0];
        color += texture2D(u_texture, v_pixelCoords[5]) * weights[1];
        color += texture2D(u_texture, v_pixelCoords[6]) * weights[2];
        color += texture2D(u_texture, v_pixelCoords[7]) * weights[3];
        color += texture2D(u_texture, v_pixelCoords[8]) * weights[4];

        color.a = 0.99607843137f;
        gl_FragColor = color;
    }
    else {
        gl_FragColor = pixel;
    }
}