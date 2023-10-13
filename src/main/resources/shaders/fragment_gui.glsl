#version 140

in vec2 textureCoords1;

out vec4 out_Color;

uniform sampler2D guiTexture;
uniform int hasColor;
uniform vec4 color;
uniform bool smoothst;

const float width = 0.41;
const float edge = 0.25;

void main(void){

	if(hasColor < 2)
	{
	    out_Color = texture(guiTexture,textureCoords1);
	}
	if(hasColor == 2)
	{
	    out_Color = color;
	}

    if(smoothst)
    {
	    float dist = 1.0 - out_Color.a;
	    float alpha = 1.0 - smoothstep(width, width + edge, dist);
        out_Color.a = alpha;
    }

	if(hasColor == 1)
	{
        out_Color *= color;
	}

}