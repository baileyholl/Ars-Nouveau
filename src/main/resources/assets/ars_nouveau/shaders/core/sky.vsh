#version 150

#moj_import <projection.glsl>

in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 projection;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    projection = projection_from_position(gl_Position);
}
