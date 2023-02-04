#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;

in vec4 projection;
out vec4 fragColor;

void main() {
    fragColor = textureProj(Sampler0, projection);
}
