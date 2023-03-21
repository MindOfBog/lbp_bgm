#version 400 core

layout( triangles ) in;
layout( triangle_strip, max_vertices = 10 ) out;

in vec2 geoTextureCoord[];
in vec3 geoNormal[];

out vec2 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPos;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform float extrusion;
uniform vec3 camPos;

void main()
{
    int index = 0;
    float dist = 0;
    vec4 worldPos;

    if(extrusion > 0)
    {
        dist = distance((projectionMatrix * viewMatrix * (transformationMatrix * gl_in[index].gl_Position)).xyz, camPos);
        worldPos = transformationMatrix * vec4(gl_in[index].gl_Position.xyz + geoNormal[index] * (dist / extrusion), 1);
        gl_Position = projectionMatrix * viewMatrix * worldPos;
        fragNormal = normalize(worldPos).xyz;
        fragPos = worldPos.xyz;
        fragTextureCoord = geoTextureCoord[index];
        EmitVertex();

        index = 1;

        dist = distance((projectionMatrix * viewMatrix * (transformationMatrix * gl_in[index].gl_Position)).xyz, camPos);
        worldPos = transformationMatrix * vec4(gl_in[index].gl_Position.xyz + geoNormal[index] * (dist / extrusion), 1);
        gl_Position = projectionMatrix * viewMatrix * worldPos;
        fragNormal = normalize(worldPos).xyz;
        fragPos = worldPos.xyz;
        fragTextureCoord = geoTextureCoord[index];
        EmitVertex();
    }

    index = 0;

    worldPos = transformationMatrix * (gl_in[index].gl_Position);
    gl_Position = projectionMatrix * viewMatrix * worldPos;
    fragNormal = normalize(worldPos).xyz;
    fragPos = worldPos.xyz;
    fragTextureCoord = geoTextureCoord[index];
    EmitVertex();

    index = 1;

    worldPos = transformationMatrix * (gl_in[index].gl_Position);
    gl_Position = projectionMatrix * viewMatrix * worldPos;
    fragNormal = normalize(worldPos).xyz;
    fragPos = worldPos.xyz;
    fragTextureCoord = geoTextureCoord[index];
    EmitVertex();

    index = 2;

    worldPos = transformationMatrix * (gl_in[index].gl_Position);
    gl_Position = projectionMatrix * viewMatrix * worldPos;
    fragNormal = normalize(worldPos).xyz;
    fragPos = worldPos.xyz;
    fragTextureCoord = geoTextureCoord[index];
    EmitVertex();

    if(extrusion > 0)
    {
        index = 1;

        dist = distance((projectionMatrix * viewMatrix * (transformationMatrix * gl_in[index].gl_Position)).xyz, camPos);
        worldPos = transformationMatrix * vec4(gl_in[index].gl_Position.xyz + geoNormal[index] * (dist / extrusion), 1);
        gl_Position = projectionMatrix * viewMatrix * worldPos;
        fragNormal = normalize(worldPos).xyz;
        fragPos = worldPos.xyz;
        fragTextureCoord = geoTextureCoord[index];
        EmitVertex();

        index = 2;

        dist = distance((projectionMatrix * viewMatrix * (transformationMatrix * gl_in[index].gl_Position)).xyz, camPos);
        worldPos = transformationMatrix * vec4(gl_in[index].gl_Position.xyz + geoNormal[index] * (dist / extrusion), 1);
        gl_Position = projectionMatrix * viewMatrix * worldPos;
        fragNormal = normalize(worldPos).xyz;
        fragPos = worldPos.xyz;
        fragTextureCoord = geoTextureCoord[index];
        EmitVertex();

        index = 0;

        dist = distance((projectionMatrix * viewMatrix * (transformationMatrix * gl_in[index].gl_Position)).xyz, camPos);
        worldPos = transformationMatrix * vec4(gl_in[index].gl_Position.xyz + geoNormal[index] * (dist / extrusion), 1);
        gl_Position = projectionMatrix * viewMatrix * worldPos;
        fragNormal = normalize(worldPos).xyz;
        fragPos = worldPos.xyz;
        fragTextureCoord = geoTextureCoord[index];
        EmitVertex();

        index = 2;

        worldPos = transformationMatrix * (gl_in[index].gl_Position);
        gl_Position = projectionMatrix * viewMatrix * worldPos;
        fragNormal = normalize(worldPos).xyz;
        fragPos = worldPos.xyz;
        fragTextureCoord = geoTextureCoord[index];
        EmitVertex();

        index = 0;

        worldPos = transformationMatrix * (gl_in[index].gl_Position);
        gl_Position = projectionMatrix * viewMatrix * worldPos;
        fragNormal = normalize(worldPos).xyz;
        fragPos = worldPos.xyz;
        fragTextureCoord = geoTextureCoord[index];
        EmitVertex();
    }
}