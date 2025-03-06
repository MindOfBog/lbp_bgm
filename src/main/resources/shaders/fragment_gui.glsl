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

uniform bool isBlur;
uniform bool isGaussian;
uniform float gaussKernel[41];
uniform float pixelSize;
uniform int radius;
uniform bool vertical;

void main(void){

	if(isBlur)
	{
		if(isGaussian)
		{
			out_Color = vec4(0.0);
			for(int i = -radius; i <= radius; i++)
			{
				vec2 coords = textureCoords1 + vec2(!vertical ? pixelSize * i : 0.0, vertical ? pixelSize * i : 0.0);
				out_Color += texture(guiTexture, coords) * gaussKernel[i + radius];
			}
		}
		else
		{
			out_Color = vec4(0.0);
			for(int i = -radius; i <= radius; i++)
			{
				vec2 coords = textureCoords1 + vec2(!vertical ? pixelSize * i : 0.0, vertical ? pixelSize * i : 0.0);
				out_Color += texture(guiTexture, coords);
			}
			out_Color /= radius * 2 + 1;
		}
		out_Color.a = 1.0;
	}
	else
	{
		if (hasColor < 2)
		{
			out_Color = texture(guiTexture, textureCoords1);
		}
		if (hasColor == 2)
		{
			out_Color = color;
		}

		if (smoothst)
		{
			float dist = 1.0 - out_Color.a;
			float alpha = 1.0 - smoothstep(width, width + edge, dist);
			out_Color.a = alpha;
		}

		if (hasColor == 1)
		{
			out_Color *= color;
		}

		if (circle.z != -1 && circle.z != 0)
		{
			out_Color.a *= smoothstep(circle.z + 1, circle.z, distance(gl_FragCoord.xy, circle.xy - 0.5));

			if (circle.w == 1)
			out_Color.a *= smoothstep(circle.z - 1.5, circle.z + 1, distance(gl_FragCoord.xy, circle.xy - 0.5));
		}

		if (alpha)
		out_Color.a = 1;
	}
}