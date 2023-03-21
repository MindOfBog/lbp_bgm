#version 140

in vec2 position;

out vec2 blurTextureCoords[11];

uniform mat4 transformationMatrix;
uniform float target;

void main(void){

	gl_Position = transformationMatrix * vec4(position, 0.0, 1.0);

    vec2 centerTexCoords = position * 0.5 + 0.5;
    float pixelWidth = 1.0 / target;

    for(int i = -5; i <= 5; i++)
    {
        blurTextureCoords[i + 5] = centerTexCoords + vec2(pixelWidth * i, 0.0);
    }

}