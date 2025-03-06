#version 140

out vec4 out_Color;

in vec2 centerTexCoords;

uniform sampler2D originalTexture;

uniform bool hasMask;
uniform sampler2D maskTexture;

uniform vec4 color;

uniform float pixelSize;
uniform int radius;
uniform bool vertical;

void main(){

    if(hasMask && texture(maskTexture, centerTexCoords).a != 0.0)
    {
        discard;
    }

	out_Color = vec4(0.0);

	for(int i = -radius; i <= radius; i++)
    {
        vec2 coords = centerTexCoords;

        if(vertical)
            coords = centerTexCoords + vec2(0.0, pixelSize * i);
        else
            coords = centerTexCoords + vec2(pixelSize * i, 0.0);

        vec4 coordColor = texture(originalTexture, coords);
        if(coordColor.a != 0.0)
            out_Color.a = 1.0;
    }

    out_Color.r = color.r;
    out_Color.g = color.g;
    out_Color.b = color.b;

}