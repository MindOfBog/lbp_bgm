#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec4 textureCoord;
layout(location = 2) in vec3 normal;
layout(location = 3) in ivec4 joints;
layout(location = 4) in vec4 weights;
layout(location = 5) in vec3 tangent;
layout(location = 6) in int gmat;

out vec4 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPos;
out vec3 fragTang;
out vec3 fragBitTang;
flat out int fragGmat;
out vec4 viewPosition;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 triangleOffset;
uniform mat4 bones[100];
uniform bool hasBones;

void main() {
    mat4 modelMatrix = transformationMatrix;

    fragGmat = gmat;

    if (hasBones) {
        mat4 boneTransform = mat4(0.0);
        for (int i = 0; i < 4; ++i) {
            boneTransform += weights[i] * bones[joints[i]];
        }
        modelMatrix = modelMatrix * boneTransform;
    }

    vec4 worldPosition = modelMatrix * vec4(position, 1.0);
    worldPosition.xyz += triangleOffset * normal;

    viewPosition = viewMatrix * worldPosition;

    fragTextureCoord = textureCoord;
    fragNormal = normalize(mat3(transpose(inverse(modelMatrix))) * normal);
    fragPos = worldPosition.xyz;
    fragTang = normalize(mat3(transpose(inverse(modelMatrix))) * tangent);
    fragBitTang = cross(fragNormal, fragTang);

    gl_Position = projectionMatrix * viewPosition;
}
