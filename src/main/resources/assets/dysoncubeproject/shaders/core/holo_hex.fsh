#version 150

in vec4 vColor;
in vec3 vWorldPos;// compatibility

out vec4 fragColor;

uniform float uTime;// seconds
uniform float uValid;// unused here but kept for compatibility
uniform vec3 uCamPos;
uniform float uSize;

// --- Constants from Shadertoy ---
const float PI = 3.141592;
const float SQRT3 = 1.7320508075688772;
const float SC = 1.732050;// Shadertoy constant

vec3 aces_tonemap(vec3 color){
    mat3 m1 = mat3(
    0.59719, 0.07600, 0.02840,
    0.35458, 0.90834, 0.13383,
    0.04823, 0.01566, 0.83777
    );
    mat3 m2 = mat3(
    1.60475, -0.10208, -0.00327,
    -0.53108, 1.10813, -0.07276,
    -0.07367, -0.00605, 1.07602
    );
    vec3 v = m1 * color;
    vec3 a = v * (v + 0.0245786) - 0.000090537;
    vec3 b = v * (0.983729 * v + 0.4329510) + 0.238081;
    return pow(clamp(m2 * (a / b), 0.0, 1.0), vec3(1.0 / 2.2));
}

// Hash for 2D
float hash(vec2 p){
    // Cheap 2D hash
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

// Signed distance to a regular hexagon of circumradius r centered at origin (Inigo Quilez)
float sdHex(vec2 p, float r){
    const vec3 k = vec3(-0.8660254, 0.5, 0.57735027);
    p = abs(p);
    p -= 2.0 * min(dot(k.xy, p), 0.0) * k.xy;
    p -= vec2(clamp(p.x, -k.z * r, k.z * r), r);
    return length(p) * sign(p.y);
}

// Axial/cart conversions for a flat-top hex grid
vec2 cartToAxial(vec2 p){
    float q = (2.0/3.0) * p.x;
    float r = (-1.0/3.0) * p.x + (1.0/SQRT3) * p.y;
    return vec2(q, r);
}
vec2 axialToCart(vec2 h){
    float x = (3.0/2.0) * h.x;
    float y = SQRT3 * (h.y + 0.5 * h.x);
    return vec2(x, y);
}

// Finds the closest hexagon center
vec4 beehive_center(vec2 p) {
    vec2 s = vec2(1.0, 1.73205080757);
    // Candidate centers
    vec4 hC = floor(vec4(p, p - vec2(0.5, 1.0)) / vec4(s, s)) + 0.5;
    // Distances to two possible hex centers
    vec4 h = vec4(p - hC.xy * s, p - (hC.zw + 0.5) * s);
    // Return the closer one (xy = local coords, zw = hex index/ID)
    return (dot(h.xy, h.xy) < dot(h.zw, h.zw))
    ? vec4(h.xy, hC.xy)
    : vec4(h.zw, hC.zw + 9.73);// offset to avoid overlap
}

// Distance to beehive hex border
float beehive_dist(vec2 p) {
    vec2 s = vec2(1.0, 1.73205080757);
    p = abs(p);
    return max(dot(p, s * 0.5), p.x);
}

void main(){

    // Recover true world-space position: CPU feeds camera-relative coords, add camera position
    vec3 worldPos = vWorldPos + uCamPos;

    // Choose UV plane based on the dominant surface facing using geometric normal
    // This makes the pattern render properly on any face orientation (tri-planar style selection)
    vec3 dpdx = dFdx(worldPos);
    vec3 dpdy = dFdy(worldPos);
    vec3 n = normalize(cross(dpdx, dpdy));
    vec3 an = abs(n);
    vec2 uv;
    if (an.z >= an.x && an.z >= an.y) {
        // Facing along +/-Z → use XY plane
        uv = worldPos.xy;
    } else if (an.x >= an.y) {
        // Facing along +/-X → use ZY plane
        uv = worldPos.zy;
    } else {
        // Facing along +/-Y → use XZ plane (top/bottom)
        uv = worldPos.xz;
    }
    uv = uv / uSize;
    vec3 baseColor = vec3(0.5, 0.8, 1.0);


    // Map into beehive
    vec2 scaleUV = vec2(12.0, 7.0*SC);
    vec4 p = beehive_center(uv * scaleUV);

    vec4 b = fract(
    vec4(uv * scaleUV - p.xy - vec2(0.5, 0.57735), vec2(1.0, 1.1547)) / (scaleUV.xyxy));

    // Random cell offset 
    float randOffset = hash(p.zw) * 90.0;

    // Circular gradient inside each hex cell
    float angle = -atan(p.y, p.x) / (2.0 * PI) + 0.5;
    float gradient = fract(angle - 0.6 * uTime + randOffset);

    // Beehive core shape
    float g = 1.0 - 2.0 * beehive_dist(p.xy);
    float col = clamp(0.5 + (g - 0.1)/0.21, 0.0, 1.0);
    col = clamp((min(col, 1.0 - col) * 2.0) / 0.7, 0.0, 1.0);

    // Fill effect
    float fill = length(fract(b.xy + 0.5*b.zw) - vec2(0.5));//0.5
    fill = pow(fract(fill - uTime * 0.5), 8.0) * 0.05;

    // Background variation
    float bg = length(sin(5.0 * b.zw * b.xy * randOffset + uTime / 5.));
    bg = clamp(bg, 0.9, 1.);
    // Colorize
    vec3 mask = (col * gradient * fill * 3.0 + fill) * baseColor;
    vec3 background = (baseColor * gradient * col + (bg * baseColor));

    // Combine and tone-map
    vec3 f = mask + background;
    vec3 outColor = aces_tonemap(f*f*f);

    // Modulate by incoming vertex color for integration/tint and alpha from vertex
    outColor *= vColor.rgb;
    fragColor = vec4(outColor, vColor.a);
}
