#version 140

in vec2 textureCoords1;

out vec4 out_Color;

uniform sampler2D guiTexture;
uniform int hasColor;
uniform vec4 color;
uniform bool smoothst;
uniform bool alpha;

uniform vec4 circle;

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

	if(circle.z != -1 && circle.z != 0)
	{
		out_Color.a *= smoothstep(circle.z + 1, circle.z, distance(gl_FragCoord.xy, circle.xy - 0.5));

		if(circle.w == 1)
			out_Color.a *= smoothstep(circle.z - 1.5, circle.z + 1, distance(gl_FragCoord.xy, circle.xy - 0.5));
	}

	if(alpha)
		out_Color.a = 1;
}