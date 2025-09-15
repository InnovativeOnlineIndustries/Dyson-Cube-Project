#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float uTime;
uniform float uIntensity;

out vec4 vColor;
out float vGlow;
out float vSpark;// drives bright spark pulses
out float vPhase;// general phase along the segment
out float vAlong;// normalized along-barrel position
out float vSeed;// stable random seed per segment

float hash(float n){ return fract(sin(n)*43758.5453); }

void main() {
    // base position
    vec4 viewPos = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * viewPos;

    // Pass through base color
    vColor = Color;

    // A little vertex-based shimmer to break up lines
    float t = uTime * 6.2831853;// tau seconds
    float phase = sin(dot(Position, vec3(3.1, 5.7, 2.3)) + t * 1.7);
    vGlow = 0.5 + 0.5 * phase;

    // Longitudinal phase along X helps us move spark highlights down the barrel
    float along = Position.x;
    vAlong = fract(along * 0.25);
    vPhase = fract(along * 0.75 + uTime * 0.8);

    // Stable seed per segment derived from YZ so parallel segments differ
    vSeed = hash(dot(Position.yz, vec2(12.9898, 78.233)));

    // Spark pulses: several narrow peaks over phase with slight seed offsets
    float s = 0.0;
    s += exp(-40.0 * pow(fract(vPhase + 0.03 + vSeed*0.11) - 0.5, 2.0));
    s += exp(-40.0 * pow(fract(vPhase + 0.41 + vSeed*0.07) - 0.5, 2.0));
    s += exp(-40.0 * pow(fract(vPhase + 0.77 + vSeed*0.19) - 0.5, 2.0));
    // scale by intensity for stronger sparks
    vSpark = clamp(s * (0.6 + 0.8 * uIntensity), 0.0, 3.0);
}
