#version 150

in vec4 vColor;
in float vGlow;
in float vSpark;
in float vPhase;
in float vAlong;
in float vSeed;

out vec4 fragColor;

uniform float uTime;
uniform float uIntensity;

// small noise
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

    // Subtle electric flicker + crackle
    float fineNoise = noise(gl_FragCoord.xy*0.9 + t*0.2 + vSeed*13.0);
    float crackle = noise(gl_FragCoord.yx*0.35 + t*1.7 + vSeed*31.0);
    float flicker = 0.78 + 0.22 * sin(t*45.0 + fineNoise*6.2831) + 0.08 * (crackle - 0.5);

    // Slow energy pulse with intensity scaling
    float pulse = 0.5 + 0.5 * sin(t*12.0 + vSeed*6.0);

    // Occasional surge events to simulate discharge jumps
    float surgeRnd = hash(floor(t*5.0) + vSeed*100.0);
    float surge = step(0.93, surgeRnd) * (0.6 + 0.4*sin(t*60.0));

    // Boosted glow combining shimmer, pulse, spark and surge
    float spark = vSpark * (0.75 + 0.25 * sin(t*24.0 + vSeed*9.0));
    float baseGlow = (vGlow * 0.6 + pulse * 0.4) * (0.8 + 0.8 * uIntensity);
    float glow = clamp(baseGlow + spark + surge, 0.0, 4.0);

    // Slight longitudinal variation so lines look segmented/branchy
    float branch = 0.4 + 0.6 * sin((vAlong*6.2831 + t*8.0) + vSeed*12.0);
    glow *= 0.85 + 0.15 * branch;

    // Color grading toward a white-hot electric cyan with violet accents
    vec3 cyan   = vec3(0.25, 0.95, 1.0);
    vec3 violet = vec3(0.68, 0.3, 1.0);
    vec3 hot    = vec3(1.0, 1.0, 1.0);
    float hotMix = smoothstep(0.55, 1.8, glow);
    float violetMix = smoothstep(0.2, 0.8, crackle) * 0.25;
    vec3 base = mix(cyan * vColor.rgb, hot, hotMix);
    base = mix(base, violet, violetMix * (0.6 + 0.4 * uIntensity));

    // Twinkle and micro-shimmer
    float tw = 0.02 * sin(gl_FragCoord.x*0.17 + gl_FragCoord.y*0.09 + t*7.0 + vSeed*5.0);

    // Corona/halo multiplier: more when spark or surge is high
    float corona = smoothstep(0.4, 1.6, glow) + 0.3 * surge;

    vec3 color = base * (0.85 + 0.15 * flicker) * (0.6 + 0.4 * glow) * (1.0 + 0.35 * corona) + tw;

    // Stronger alpha for more visible, shiny additive lines; keep within sane range
    float alpha = clamp(0.12 + 0.60 * glow + 0.18 * pulse + 0.10 * vSpark + 0.10 * surge, 0.0, 1.0) * vColor.a;

    fragColor = vec4(color, alpha);
}
