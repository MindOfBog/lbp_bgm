#version 140

out vec4 out_Color;

in vec2 blurTextureCoords[11];

uniform sampler2D originalTexture;
uniform vec3 color;
uniform bool hasColor;
uniform bool stipple;

const mat4 thresholdMatrix = mat4(
    1, 9, 3, 11,
    13, 5, 15, 7,
    4, 12, 2, 10,
    16, 8, 14, 6);

void main(void){

	out_Color = vec4(0.0);
	out_Color += texture(originalTexture, blurTextureCoords[0]) * 0.0093;
    out_Color += texture(originalTexture, blurTextureCoords[1]) * 0.028002;
    out_Color += texture(originalTexture, blurTextureCoords[2]) * 0.065984;
    out_Color += texture(originalTexture, blurTextureCoords[3]) * 0.121703;
    out_Color += texture(originalTexture, blurTextureCoords[4]) * 0.175713;
    out_Color += texture(originalTexture, blurTextureCoords[5]) * 0.198596;
    out_Color += texture(originalTexture, blurTextureCoords[6]) * 0.175713;
    out_Color += texture(originalTexture, blurTextureCoords[7]) * 0.121703;
    out_Color += texture(originalTexture, blurTextureCoords[8]) * 0.065984;
    out_Color += texture(originalTexture, blurTextureCoords[9]) * 0.028002;
    out_Color += texture(originalTexture, blurTextureCoords[10]) * 0.0093;

    ivec2 coord = ivec2(gl_FragCoord.xy);
    float threshold = thresholdMatrix[coord.x % 4][coord.y % 4] / 17.0;

    if(out_Color.a > 0)
    {
        out_Color.a = min(out_Color.a * 2, 1.0);
    }

    if(hasColor)
    {
        out_Color.r = color.r;
        out_Color.g = color.g;
        out_Color.b = color.b;
    }

    if(threshold > out_Color.a && stipple)
    {
        discard;
    }
}