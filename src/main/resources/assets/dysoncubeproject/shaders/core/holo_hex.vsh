#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vColor;
out vec3 vWorldPos;
out vec3 vViewPos;

void main() {
    vWorldPos = Position;// world position as provided by CPU (object space treated as world for hologram)
    vViewPos = (ModelViewMat * vec4(Position, 1.0)).xyz;// camera/view-space position for camera-facing pattern
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vColor = Color;
}
