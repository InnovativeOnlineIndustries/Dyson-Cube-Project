#version 150

in vec4 vColor;
in float vX;
in vec2 vRC;

out vec4 fragColor;

uniform float uTime;
uniform float uIntensity;

float hash(float n){ return fract(sin(n)*43758.5453); }
float noise(vec2 x){
    vec2 p = floor(x);
    vec2 f = fract(x);
    f = f*f*(3.0-2.0*f);
    float n = p.x + p.y*57.0;
    float res = mix(mix(hash(n+0.0), hash(n+1.0), f.x),
    mix(hash(n+57.0), hash(n+58.0), f.x), f.y);
    return res;
}

void main(){
    float t = uTime;

    // radial distance from beam axis (y,z plane)
    float r = length(vRC);

    // Core + halo profile
    float core = exp(- (r*r) * 600.0);// tight white-hot core
    float halo = exp(- pow(r*6.0, 2.0)) * 0.6;// broader cyan halo

    // Longitudinal pulses and speed streaks
    float wave = 0.5 + 0.5 * sin(vX*4.0 - t*60.0);
    float streak = 0.25 + 0.75 * smoothstep(0.0, 1.0, wave);

    // Micro crackle
    float crackle = noise(vec2(vX*3.0, r*12.0) + t*2.0);

    // Intensity shaping
    float I = clamp(uIntensity, 0.0, 2.5);

    // Color grading toward electric blue-white
    vec3 cyan = vec3(0.2, 0.9, 1.0);
    vec3 hot  = vec3(1.0);
    float hotMix = smoothstep(0.35, 1.2, core*I + streak*0.3);
    vec3 base = mix(cyan * vColor.rgb, hot, hotMix);

    // Combine components
    float glow = core*(1.5+I) + halo*(0.8+0.5*I) + streak*0.4 + crackle*0.15;
    vec3 color = base * (0.8 + 0.2 * sin(t*40.0 + vX*3.0)) * (1.0 + 0.75*glow);

    // Strong additive alpha, sharper near core
    float alpha = clamp(0.08 + 0.85*core + 0.55*halo + 0.25*streak, 0.0, 1.0) * vColor.a;

    fragColor = vec4(color, alpha);
}
