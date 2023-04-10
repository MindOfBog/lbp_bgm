#version 140

out vec4 out_Color;

in vec2 blurTextureCoords[25];

uniform sampler2D originalTexture;
uniform vec3 color;
uniform bool hasColor;
uniform bool stipple;
uniform int radius;

const mat4 thresholdMatrix = mat4(
    1, 9, 3, 11,
    13, 5, 15, 7,
    4, 12, 2, 10,
    16, 8, 14, 6);

void main(){

	out_Color = vec4(0.0);

	for(int i = -radius; i <= radius; i++)
	{
	    out_Color += texture(originalTexture, blurTextureCoords[i + radius]);
	}

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