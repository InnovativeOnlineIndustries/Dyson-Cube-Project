#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 uCamPos;

out vec4 vColor;
out vec3 vWorldPos;

void main() {
    // Anchor pattern to object/local space so it does not follow camera or player
    vWorldPos = Position;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vColor = Color;
}
