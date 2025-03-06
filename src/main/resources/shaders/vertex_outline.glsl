#version 140

in vec2 position;

out vec2 centerTexCoords;

void main(){

	gl_Position = vec4(position, 0.0, 1.0);
    centerTexCoords = position * 0.5 + 0.5;
}