#version 140

in vec2 position;
in vec2 textureCoords;

out vec2 textureCoords1;

uniform mat4 transformationMatrix;
uniform bool hasCoords;

void main(void){
	gl_Position = transformationMatrix * vec4(position, 0.0, 1.0);

	if(hasCoords)
	{
	    textureCoords1 = textureCoords;
	}
	else
	{
	    textureCoords1 = vec2((position.x+1.0)/2.0, 1 - (position.y+1.0)/2.0);
	}
}