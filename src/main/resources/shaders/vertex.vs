#version 400 core

in vec3 position;
in vec2 textureCoord;
in vec3 normal;

out vec2 geoTextureCoord;
out vec3 geoNormal;

uniform vec3 triangleOffset;

void main()
{
    gl_Position = vec4(position + normal * triangleOffset, 1);
    geoTextureCoord = textureCoord;
    geoNormal = normal;
}