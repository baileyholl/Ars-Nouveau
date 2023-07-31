#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler3;

uniform vec4 ColorModulator;
uniform float GameTime;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

void main() {

    vec4 color = texture(Sampler0, texCoord0);
    vec4 mask = texture(Sampler3, texCoord0);

    float time = (sin(GameTime * 800.) + 1.) / 2.;
    if (color.a < 0.1) {
        discard;
    }
    if (mask.a > 0.1) {
        vec3 rain = sqrt(sin((time + vec3(0, 2, 1) / 3.) * (acos(-1.)*2.)) * .5 + .5);
        color = mix(vec4(rain, 1.), mask, 0.5);
    }
    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
