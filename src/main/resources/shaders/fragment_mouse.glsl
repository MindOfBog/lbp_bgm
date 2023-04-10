#version 400 core

in vec2 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragPos;

out ivec3 fragmentColor;

uniform ivec2 arrayIndex;

void main()
{
    fragmentColor = ivec3(arrayIndex.x, arrayIndex.y, 0);
}