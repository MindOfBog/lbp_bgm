#version 140

in vec2 textureCoords1;

out vec4 out_Color;

//following code was shamelessly stolen from user "Leadwerks" -> https://community.khronos.org/t/screen-space-ambient-occlusion/54406/12 (and tweaked a bit)

uniform sampler2D texture0;
uniform sampler2D texture1;

uniform float zRatio;

uniform vec2 camerarange;
uniform vec2 screensize;

uniform vec4 fogColor;

float readDepth(vec2 coord) {
	return (2.0f * camerarange.x) / (camerarange.y + camerarange.x - texture(texture0, coord).x * (camerarange.y - camerarange.x));
}

float compareDepths(in float depth1, in float depth2, float aoMultiplier) {
	float depthTolerance = 0.0001f;
	float aorange = 300.0f;
	float diff = sqrt(clamp(1.0f - (depth1 - depth2) / (aorange / (camerarange.y - camerarange.x)), 0.0f, 1.0f));
	float ao = (max(0.0f, depth1 - depth2 - depthTolerance) * aoMultiplier) * diff;
	return ao;
}

void main(void)
{
	vec2 texCoord = textureCoords1;
	float depth = readDepth(texCoord);

	float d;

	float pw = 1.0f / screensize.x;
	float ph = 1.0f / screensize.y;

	float ao = 0.0f;

	float aoMultiplier = zRatio * 6.5f;

	float aoscale = 5.0f;

	int steps = 9;

	for(int i = 0; i < steps; i++)
	{
		d = readDepth(vec2(texCoord.x + pw, texCoord.y + ph));
		ao += compareDepths(depth, d, aoMultiplier) / aoscale;

		d = readDepth(vec2(texCoord.x - pw, texCoord.y + ph));
		ao += compareDepths(depth, d, aoMultiplier) / aoscale;

		d = readDepth(vec2(texCoord.x + pw, texCoord.y - ph));
		ao += compareDepths(depth, d, aoMultiplier) / aoscale;

		d = readDepth(vec2(texCoord.x - pw, texCoord.y - ph));
		ao += compareDepths(depth, d, aoMultiplier) / aoscale;

		pw *= 2.0f;
		ph *= 2.0f;
		aoMultiplier /= 2.0f;
		aoscale *= 1.2f;
	}

	ao /= pow(steps, 2);
//	if(ao < 0.25f)
//		ao = 0.25f;
//	if(ao > 0.75f)
//		ao = 0.75f;

//	ao -= 0.25f;

	out_Color = vec4(vec3(1.0f - ao), 1.0f) * vec4(texture(texture1, texCoord).rgb, 1.0f);
	out_Color = mix(fogColor, out_Color, texture(texture1, texCoord).a);
}