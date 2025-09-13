#version 150

in vec4 vColor;
in vec3 vWorldPos;// actually view-space position passed from CPU; we will offset by camera to get true world-space

out vec4 fragColor;

uniform float uTime;
uniform float uValid;// 1.0 = valid placement, 0.0 = invalid
uniform vec3 uCamPos;// camera world position

// Simple hash/noise helpers
float hash(float n) { return fract(sin(n) * 43758.5453); }
float noise(vec2 x){
    vec2 p = floor(x);
    vec2 f = fract(x);
    f = f*f*(3.0-2.0*f);
    float n = p.x + p.y*57.0;
    float res = mix(mix(hash(n+  0.0), hash(n+  1.0), f.x),
    mix(hash(n+ 57.0), hash(n+ 58.0), f.x), f.y);
    return res;
}

void main() {
    // Recover true world-space position using camera offset (CPU fed view-space coords)
    vec3 worldPosBase = vWorldPos + uCamPos;

    // Lightweight glitchiness: occasional horizontal tear bands that laterally offset the lattice phase
    float bandSize = 1.0;// pixels per band
    float yBand    = floor(gl_FragCoord.y / bandSize);
    float bandRnd  = hash(yBand + floor(uTime * 3.0));// changes a few times per second
    float bandActive = step(0.985, bandRnd);// ~1.5% of bands active
    // shift up to ~0.125m when active, stable per band
    float bandShift = (hash(yBand * 7.0 + 3.14) - 0.5) * 0.25 * bandActive;

    // Rare burst amplifies the shift slightly
    float burst = step(0.99, fract(uTime * 0.7));
    bandShift += burst * (hash(yBand * 13.0 + 1.23) - 0.5) * 0.05;

    // Apply the horizontal phase shift to world position for procedural patterns
    vec3 worldPos = worldPosBase + vec3(bandShift, 0.0, 0.0);

    // Validity-driven palette (cool cyan vs warm error red/orange)
    vec3 colValid   = vec3(0.1, 0.95, 1.0);
    vec3 colInvalid = vec3(1.0, 0.25, 0.15);
    vec3 base = mix(colInvalid, colValid, clamp(uValid, 0.0, 1.0));

    // 3D lattice grid in world space fixed to the world (not camera)
    // frequency controls grid cell size; larger value => denser grid
    vec3 freq = vec3(2.0, 3.0, 4.0);// 0.25m spacing
    vec3 g = abs(fract(worldPos * freq) - 0.5);
    // line thickness
    float line = smoothstep(0.08, 0.02, min(min(g.x, g.y), g.z));

    // Animated scanlines + vertical sweep
    float scan = 0.55 + 0.45 * sin(gl_FragCoord.y * 0.07 + uTime * 8.0);
    float sweep = 1.0 - abs(fract(worldPos.y * 0.35 + uTime * 0.35) - 0.5) * 2.0;
    sweep = smoothstep(0.75, 1.0, sweep);

    // Added explicit scan lines
    // Fine screen-space horizontal scan lines (thin bright bands moving upward)
    float ssSaw = fract(gl_FragCoord.y * 0.18 + uTime * 0.25);
    float fineScreenScan = smoothstep(0.0, 0.06, ssSaw) * (1.0 - smoothstep(0.06, 0.12, ssSaw));

    // World-space horizontal scan bands inside the volume (gives volumetric feel)
    float wsSaw = fract(worldPos.y * 3.0 + uTime * 0.5);
    float worldScanBand = 0;

    // Rim-style emphasis using a secondary, thicker lattice falloff
    float rim = smoothstep(0.0, 0.2, 1.0 - min(min(g.x, g.y), g.z));

    // Subtle per-fragment flicker
    float n = noise(worldPos.xz * 3.0 + uTime * 0.5);
    float flicker = 0.9 + 0.1 * sin(uTime * 12.0 + n * 6.2831);

    // Screen-space wavy glitch amplification during bursts (already computed) and during active bands
    float wave = sin(gl_FragCoord.x * 0.25 + gl_FragCoord.y * 0.15 + uTime * 6.0);
    float glitch = 1.0 + wave * (0.05 * bandActive + 0.10 * burst);

    // Chromatic jitter: tiny channel separation when bands/bursts are active
    float chroma = 0.02 * bandActive + 0.015 * burst;

    vec3 holoCore = base * (0.35 + 0.65 * scan) * (0.5 + 0.5 * sweep);
    vec3 gridGlow = base * (0.6 * rim + 1.4 * line);

    // Additional scan line glow contributions
    vec3 scanGlow = base * (0.20 * fineScreenScan + 0.30 * worldScanBand);

    float rCh = (holoCore.r * 0.9 + gridGlow.r * 1.1 + scanGlow.r);
    float gCh = (holoCore.g * 1.0 + gridGlow.g * 1.0 + scanGlow.g);
    float bCh = (holoCore.b * 1.1 + gridGlow.b * 0.9 + scanGlow.b);

    // Apply chromatic jitter by modulating channels slightly out of sync
    rCh *= 1.0 + chroma * sin(uTime * 9.0 + yBand * 0.7);
    bCh *= 1.0 + chroma * sin(uTime * 9.0 + yBand * 0.7 + 2.094);// +120 deg

    vec3 color = vec3(rCh, gCh, bCh) * flicker * glitch;

    // Alpha: mostly translucent, stronger on lines, sweep, and scan lines; dim if invalid toggles
    float alpha = vColor.a;
    float baseA = 0.10;
    float linesA = 0.35 * line + 0.20 * rim;
    float sweepA = 0.25 * sweep;
    float scanA = 0.10 * (scan - 0.5);
    float scanLinesA = 0.20 * fineScreenScan + 0.25 * worldScanBand;
    float validityBoost = mix(0.0, 0.05, clamp(uValid, 0.0, 1.0));
    float aOut = clamp(baseA + linesA + sweepA + scanA + scanLinesA + validityBoost, 0.03, 0.9) * alpha;

    // Slight alpha wobble during active bands makes the hologram feel less stable
    aOut *= 1.0 + 0.08 * bandActive * sin(uTime * 20.0 + yBand);

    fragColor = vec4(color, aOut);
}
