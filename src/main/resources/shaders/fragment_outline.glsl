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

//	out_Color = texture(originalTexture, centerTexCoords);

    float avgAlpha = 0.0f;

	for(int i = -radius; i <= radius; i++)
    {
        vec2 coords = centerTexCoords;

        if(vertical)
            coords = centerTexCoords + vec2(0.0, pixelSize * i);
        else
            coords = centerTexCoords + vec2(pixelSize * i, 0.0f);

        vec4 coordColor = texture(originalTexture, coords);

        avgAlpha += coordColor.a;
    }

    out_Color.a += out_Color.a = avgAlpha / float(radius * 2 + 1);
    out_Color.a = smoothstep(0.0f, 0.5f, clamp(out_Color.a, 0.0f, 1.0f));

    out_Color.r = color.r;
    out_Color.g = color.g;
    out_Color.b = color.b;

}