#version 330 core

out ivec3 fragmentColor;

uniform ivec2 arrayIndex;

void main()
{
    fragmentColor = ivec3(arrayIndex.x, arrayIndex.y, 0);
}