#version 400 core

in vec3 position;
in vec2 textureCoord;
in vec3 normal;
in ivec4 joints;
in vec4 weights;

out vec2 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPos;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 triangleOffset;
uniform mat4 bones[100];
uniform bool hasBones;

vec4 setupBones(vec4 inPos)
{
    mat4 skin = mat4(
    weights.x * bones[joints.x] +
    weights.y * bones[joints.y] +
    weights.z * bones[joints.z] +
    weights.w * bones[joints.w]);

    return skin * inPos;
}

void main()
{
    vec4 vertPos = vec4(position + normal * triangleOffset, 1.0);

    if(hasBones)
    {
        vertPos = setupBones(vertPos);
    }

    vec4 worldPos = mat4(transformationMatrix) * vertPos;
    gl_Position = projectionMatrix * viewMatrix * worldPos;
    fragNormal = normalize(worldPos).xyz;
    fragPos = worldPos.xyz;
    fragTextureCoord = vec2(textureCoord);
}