#version 140

out vec4 out_Color;

in vec2 blurTextureCoords[25];

uniform sampler2D originalTexture;
uniform sampler2D inputTexture;
uniform bool hasInput;
uniform vec4 color;
uniform bool hasColor;
uniform int radius;

void main(){

    vec2 currentCoords = vec2(blurTextureCoords[radius]);

    if(hasInput && texture(inputTexture, currentCoords).a != 0)
    {
        discard;
    }

	out_Color = vec4(1.0, 1.0, 1.0, 0.0);

	for(int i = -radius; i <= radius; i++)
    {
        vec2 coords = vec2(blurTextureCoords[i + radius]);
        if(texture(originalTexture, coords).a != 0)
        {
            out_Color.a = hasColor ? color.a : 1.0;
        }
    }

    if(hasColor)
    {
        out_Color.r = color.r;
        out_Color.g = color.g;
        out_Color.b = color.b;
    }
}