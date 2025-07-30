#version 400

in vec2 textureCoords1;

out vec4 out_Color;

//%&TYPES;

struct Blur
{
	bool isGaussian;
	float gaussKernel[41];
	float pixelSize;
	int radius;
	bool vertical;
};

struct Dimensions
{
	ivec2 position;
	ivec2 size;
};

uniform int type;

uniform int abstractInt;

uniform sampler2D guiTexture;
uniform int hasColor;
uniform vec4 color;
uniform bool alpha;

uniform vec4 circle;

uniform Blur blur;
uniform Dimensions dimensions;

uniform bool smoothst;
uniform float smoothstWidth;
uniform float smoothstEdge;

vec3 rgb2hsv(vec3 c)
{
	vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
	vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
	vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

	float d = q.x - min(q.w, q.y);
	float e = 1.0e-10;
	return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
	vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
	vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
	return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main(void){

	switch(type)
	{
		case BLUR:
		{
			if(blur.isGaussian)
			{
				out_Color = vec4(0.0);
				for(int i = -blur.radius; i <= blur.radius; i++)
				{
					vec2 coords = textureCoords1 + vec2(!blur.vertical ? blur.pixelSize * i : 0.0, blur.vertical ? blur.pixelSize * i : 0.0);
					out_Color += texture(guiTexture, coords) * blur.gaussKernel[i + blur.radius];
				}
			}
			else
			{
				out_Color = vec4(0.0);
				for(int i = -blur.radius; i <= blur.radius; i++)
				{
					vec2 coords = textureCoords1 + vec2(!blur.vertical ? blur.pixelSize * i : 0.0, blur.vertical ? blur.pixelSize * i : 0.0);
					out_Color += texture(guiTexture, coords);
				}
				out_Color /= blur.radius * 2 + 1;
			}
			out_Color.a = 1.0;
		}
		break;
		case COLOR_PICKER:
		{
			int minX = dimensions.position.x;
			int maxX = dimensions.position.x + dimensions.size.x;
			int minY = dimensions.position.y - dimensions.size.y;
			int maxY = dimensions.position.y;

			if(gl_FragCoord.x < minX || gl_FragCoord.x > maxX ||
				gl_FragCoord.y < minY || gl_FragCoord.y > maxY)
				discard;

			if(abstractInt == 0)
			{
				float hue = (gl_FragCoord.x - minX) / dimensions.size.x;
				out_Color = vec4(hsv2rgb(vec3(hue, 1, 1)), 1.0f);
			}
			else if(abstractInt == 1)
			{
				float saturation = (gl_FragCoord.x - minX) / dimensions.size.x;
				float value = (gl_FragCoord.y - minY) / dimensions.size.y;
				out_Color = vec4(hsv2rgb(vec3(rgb2hsv(color.rgb).x, saturation, value)), 1.0f);
			}
			else if(abstractInt == 2)
			{
				bool isXAlternate = mod(floor((gl_FragCoord.x - dimensions.position.x) / 4.0), 2.0) == 0;
				bool isYAlternate = mod(floor((gl_FragCoord.y - dimensions.position.y) / 4.0), 2.0) == 0;

				bool isWhiteSquare = isXAlternate != isYAlternate;

				out_Color = vec4(isWhiteSquare ? vec3(235.0/255.0) : vec3(199.0/255.0), 1.0);
			}
		}
		break;
		case GLYPH:
		{
			float glyphAlpha = texture(guiTexture, textureCoords1)[abstractInt];

			if (smoothst)
			{
				float dist = 1.0 - glyphAlpha;
				glyphAlpha = 1.0 - smoothstep(smoothstWidth, smoothstEdge, dist);
			}

			out_Color = vec4(color.r, color.g, color.b, glyphAlpha);
		}
		break;
		default:
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
				float alpha = 1.0 - smoothstep(smoothstWidth, smoothstEdge, dist);
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
		break;
	}
}