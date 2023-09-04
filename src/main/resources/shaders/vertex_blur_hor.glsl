#version 140

in vec2 position;

out vec2 blurTextureCoords[25];

uniform mat4 transformationMatrix;
uniform float target;
uniform int radius;

void main(void){

	gl_Position = transformationMatrix * vec4(position, 0.0, 1.0);

    vec2 centerTexCoords = position * 0.5 + 0.5;
    float pixelWidth = 1.0 / target;

    for(int i = -radius; i <= radius; i++)
    {
        vec2 coord = centerTexCoords + vec2(pixelWidth * i, 0.0);
        blurTextureCoords[i + radius].x = coord.x;
        blurTextureCoords[i + radius].y = coord.y;
    }

}