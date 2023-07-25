#version 400 core

in vec3 position;
in vec4 textureCoord;
in vec3 normal;
in ivec4 joints;
in vec4 weights;
in vec3 tangent;

out vec4 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPos;
out vec3 fragTang;
out vec3 fragBitTang;

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

    vec3 surfaceNormal = (transformationMatrix * vec4(normal,0.0)).xyz;

    fragNormal = normalize(surfaceNormal);
    fragTang = normalize((viewMatrix * vec4(tangent, 0.0)).xyz);
    fragBitTang = normalize(cross(fragNormal, fragTang));

    fragPos = worldPos.xyz;
    fragTextureCoord = vec4(textureCoord);
}