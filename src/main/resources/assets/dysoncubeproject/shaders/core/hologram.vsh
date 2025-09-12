#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vColor;
out vec3 vWorldPos;

void main() {
    // Pass through world-space position (before ModelView transform) for procedural effects
    vWorldPos = Position;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vColor = Color;
}
