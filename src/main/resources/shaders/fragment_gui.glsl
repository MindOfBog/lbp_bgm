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

uniform float guiScale;

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

//vec2 bezier(float t, vec2 p0, vec2 p1, vec2 p2, vec2 p3) {
//	float invT = 1.0 - t;
//	return invT*invT*invT*p0 + 3.0*invT*invT*t*p1 + 3.0*invT*t*t*p2 + t*t*t*p3;
//}
//
//float sdSegment(vec2 p, vec2 a, vec2 b) {
//	vec2 pa = p - a, ba = b - a;
//	float h = clamp(dot(pa, ba) / dot(ba, ba), 0.0, 1.0);
//	return length(pa - ba * h);
//}

float dot2(vec2 v) { return dot(v, v); }

// Exact minimum distance from point p to cubic Bezier (p0,p1,p2,p3)
// Returns the distance (not squared)
float sdCubicBezier(vec2 p, vec2 p0, vec2 p1, vec2 p2, vec2 p3)
{
	// Reduce to problem: minimize |B(t) - p|²
	// B(t) = (1-t)³p0 + 3(1-t)²t·p1 + 3(1-t)t²·p2 + t³·p3
	// Convert to polynomial form: B(t) = A·t³ + B·t² + C·t + p0
	vec2 A =  -p0 + 3.0*p1 - 3.0*p2 + p3;
	vec2 B =  3.0*p0 - 6.0*p1 + 3.0*p2;
	vec2 C =  -3.0*p0 + 3.0*p1;
	vec2 D =   p0 - p;           // shift so we minimize |B(t)|²

	// d/dt |B(t)|² = 0  →  a·t⁵ + b·t⁴ + c·t³ + d·t² + e·t + f = 0
	// Coefficients of the derivative polynomial:
	float a = dot2(A) * 3.0;                          // t⁵ coeff / extra factor removed below
	float b = dot(A,B) * 5.0;
	float c = dot(A,C)*4.0 + dot2(B)*2.0;            // ×2 absorbed in dot2
	float d = dot(A,D)*3.0 + dot(B,C)*3.0;
	float e = dot(B,D)*2.0 + dot2(C);
	float f = dot(C,D);

	// Newton's method — seed multiple starting points to avoid local minima
	float minDist = min(dot2(D), dot2(p3 - p));      // check endpoints t=0, t=1
	minDist = sqrt(minDist);

	const int SEEDS = 8;
	for (int s = 0; s < SEEDS; s++)
	{
		float t = float(s) / float(SEEDS - 1);       // seeds: 0, 1/7, 2/7 … 1
		t = clamp(t, 0.0, 1.0);

		// 5 Newton iterations
		for (int i = 0; i < 5; i++)
		{
			// Evaluate polynomial  p(t) = a·t⁵ + b·t⁴ + c·t³ + d·t² + e·t + f
			float pt  = ((((a*t + b)*t + c)*t + d)*t + e)*t + f;
			// Evaluate derivative  p'(t) = 5a·t⁴ + 4b·t³ + 3c·t² + 2d·t + e
			float dpt = (((5.0*a*t + 4.0*b)*t + 3.0*c)*t + 2.0*d)*t + e;
			if (abs(dpt) < 1e-6) break;
			t -= pt / dpt;
			t  = clamp(t, 0.0, 1.0);
		}

		// Evaluate actual Bezier point at converged t
		vec2 bt = ((A*t + B)*t + C)*t + D + p;       // D = p0-p, so add p back
		// Actually D already shifted: B(t)-p = A·t³+B·t²+C·t+D, so:
		vec2 diff = ((A*t + B)*t + C)*t + D;         // = B(t) - p
		float dist = sqrt(dot2(diff));
		minDist = min(minDist, dist);
	}

	return minDist;
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
				out_Color = vec4(hsv2rgb(vec3(rgb2hsv(color.rgb).x, saturation, value)), color.a);
			}
			else if(abstractInt == 2)
			{
				float size = round(guiScale / 6.0) * 2.0;

				float xBlock = floor((gl_FragCoord.x - dimensions.position.x) / size);
				float yBlock = floor((gl_FragCoord.y - dimensions.position.y) / size);

				bool isWhiteSquare = mod(xBlock + yBlock, 2.0) == 0.0;

				out_Color = vec4(isWhiteSquare ? vec3(235.0/255.0) : vec3(199.0/255.0), 1.0);
			}
			else if(abstractInt == 3)
			{
				float alpha = (gl_FragCoord.x - minX) / dimensions.size.x;
				out_Color = vec4(color.rgb, alpha);
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
		case NODE_LINE:
		{
			vec2 p0 = vec2(dimensions.position);
			vec2 p3 = vec2(dimensions.size);
			vec2 pixelPos = gl_FragCoord.xy;

			float deltaX = abs(p3.x - p0.x);
			float offset = max(deltaX * 0.5, 50.0);
			vec2 p1 = p0 + vec2(offset, 0.0);
			vec2 p2 = p3 - vec2(offset, 0.0);

			float minDist = sdCubicBezier(pixelPos, p0, p1, p2, p3);

			float thickness = guiScale / 24.0;
			float antialias = 1.5;
			float edge = smoothstep(thickness + antialias, thickness, minDist);

			out_Color = vec4(color.rgb, color.a * edge);
			if (edge < 0.01) discard;
		}
		break;
//		case NODE_LINE:
//		{
//			vec2 p0 = vec2(dimensions.position);
//			vec2 p3 = vec2(dimensions.size);
//			vec2 pixelPos = gl_FragCoord.xy;
//
//			float deltaX = abs(p3.x - p0.x);
//			float offset = max(deltaX * 0.5, 50.0);
//			vec2 p1 = p0 + vec2(offset, 0.0);
//			vec2 p2 = p3 - vec2(offset, 0.0);
//
//			float minDist = 1e10;
//			int samples = 32;
//			vec2 prevPoint = p0;
//
//			for (int i = 1; i <= samples; i++) {
//				float t = float(i) / float(samples);
//				vec2 currentPoint = bezier(t, p0, p1, p2, p3);
//				minDist = min(minDist, sdSegment(pixelPos, prevPoint, currentPoint));
//				prevPoint = currentPoint;
//			}
//
//			float thickness = guiScale / 24.0f;
//			float antialias = 1.5;
//			float edge = smoothstep(thickness + antialias, thickness, minDist);
//
//			out_Color = vec4(color.rgb, color.a * edge);
//
//			if (edge < 0.01) discard;
//		}
//		break;
		case QUAD_OUTLINE:
		{
			int minX = dimensions.position.x;
			int maxX = dimensions.position.x + dimensions.size.x;
			int minY = dimensions.position.y - dimensions.size.y;
			int maxY = dimensions.position.y;

			if(
				(gl_FragCoord.x < minX || gl_FragCoord.x > maxX ||
				gl_FragCoord.y < minY || gl_FragCoord.y > maxY)
					||
				!(gl_FragCoord.x < minX + 1 || gl_FragCoord.x > maxX - 1 ||
				gl_FragCoord.y < minY + 1 || gl_FragCoord.y > maxY - 1))
			discard;

			switch(abstractInt)
			{
				case 0: //UP
					if(gl_FragCoord.y > maxY - 1 && !(gl_FragCoord.x < minX + 1 || gl_FragCoord.x > maxX - 1))
						discard;
				break;
				case 1: //DOWN
					if(gl_FragCoord.y < minY + 1 && !(gl_FragCoord.x < minX + 1 || gl_FragCoord.x > maxX - 1))
						discard;
				break;
				case 2: //LEFT
					if(gl_FragCoord.x < minX + 1 && !(gl_FragCoord.y < minY + 1 || gl_FragCoord.y > maxY - 1))
						discard;
				break;
				case 3: //RIGHT
					if(gl_FragCoord.x > maxX - 1 && !(gl_FragCoord.y < minY + 1 || gl_FragCoord.y > maxY - 1))
						discard;
				break;
			}

			out_Color = color;
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