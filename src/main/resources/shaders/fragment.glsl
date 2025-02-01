#version 330 core

in vec4 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragPos;
in vec3 fragTang;
in vec3 fragBitTang;
flat in int fragGmat;
in vec4 viewPosition;

out vec4 fragmentColor;

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};
struct DirectionalLight
{
    vec3 color;
    vec3 direction;
    float intensity;
};
struct PointLight
{
    vec3 color;
    vec3 position;
    float intensity;
    float constant;
    float linear;
    float exponent;
};
struct SpotLight
{
    PointLight pl;
    vec3 conedir;
    float cutoff;
};

uniform bool preview;
uniform sampler2D textureSampler[32];
uniform ivec2 gmatMAP[100];
uniform int gmatCount;
uniform vec4 thingColor;

uniform vec3 ambientLight;
uniform Material material;
uniform int highlightMode;
uniform vec4 highlightColor;
uniform float brightnessMul;
uniform float specularPower;
uniform DirectionalLight directionalLights[5];
uniform int directionalLightsSize;
uniform PointLight pointLights[50];
uniform int pointLightsSize;
uniform SpotLight spotLights[50];
uniform int spotLightsSize;
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec4 rimColor;
uniform vec4 rimColor2;
uniform float fogNear;
uniform float fogFar;

uniform vec3 camPos;
uniform vec3 sunPos;

const mat4 thresholdMatrix = mat4(
    1, 9, 3, 11,
    13, 5, 15, 7,
    4, 12, 2, 10,
    16, 8, 14, 6);

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void cullBackFace()
{
    if(!gl_FrontFacing)
        discard;
}

vec4 calcLightColor(vec3 light_color, float light_intensity, vec3 to_light_dir)
{
    float diffuseFactor = max(dot(normalize(gl_FrontFacing ? fragNormal : -fragNormal), to_light_dir), 0.2);
    vec4 diffuseColor = diffuseC * vec4(light_color, 1.0) * light_intensity * diffuseFactor;

    vec3 camera_direction = normalize(-fragPos);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflectedLight = normalize(reflect(from_light_dir, normalize(gl_FrontFacing ? fragNormal : -fragNormal)));
    float specularFactor = max(dot(reflectedLight, camera_direction), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    vec4 specularColor = specularC * light_intensity * specularFactor * material.reflectance * vec4(light_color, 1.0);

    vec4 outC = (diffuseColor + specularColor);

    return outC;
}

vec4 calcPointLight(PointLight light)
{
    vec3 light_dir = light.position - fragPos;

    vec3 to_light_dir = normalize(light_dir);
    vec4 light_color = calcLightColor(light.color, light.intensity, to_light_dir);

    float dist = length(light_dir);
    float attenuationInv = light.constant + (light.linear * dist) + (light.exponent * dist * dist);

    vec4 outC = light_color / attenuationInv;

    return outC;
}

vec4 calcSpotLight(SpotLight light)
{
    vec3 light_dir = light.pl.position - fragPos;
    vec3 to_light_dir = normalize(light_dir);
    vec3 from_light_dir = -to_light_dir;
    float spot_alfa = dot(from_light_dir, normalize(light.conedir));

    vec4 color = vec4(0,0,0,0);

    if(spot_alfa > light.cutoff)
    {
        color = calcPointLight(light.pl);
        color *= (1.0 - (1.0 - spot_alfa) / (1.0 - light.cutoff));
    }

    return color;
}

vec4 calcDirectionalLight(DirectionalLight light)
{
    return calcLightColor(light.color, light.intensity, normalize(light.direction));
}

int getGmatIndex(int gmat)
{
    for(int i = 0; i < gmatCount; i++)
        if(gmatMAP[i].x == gmat)
            return i;
    return 0;
}

vec3 reinhardToneMapping(vec3 color, float exposure) {
    return color * (exposure / (color + vec3(1.0)));
}

void setupColors()
{
    bool custom = false;

    //%&AMBIENTC

    ambientC.r = min(ambientC.r, 1.0);
    ambientC.g = min(ambientC.g, 1.0);
    ambientC.b = min(ambientC.b, 1.0);
    ambientC.a = min(ambientC.a, 1.0);

    float minAlpha = 0.25;

    if(ambientC.a < minAlpha)
    {
        ambientC.a = preview ? 0 : minAlpha;
        ambientC.r = 0;
        ambientC.g = 0;
        ambientC.b = 0;
    }
    if(diffuseC.a < minAlpha)
    {
        diffuseC.a = preview ? 0 : minAlpha;
        diffuseC.r = 0;
        diffuseC.g = 0;
        diffuseC.b = 0;
    }
    if(specularC.a < minAlpha)
    {
        specularC.a = preview ? 0 : minAlpha;
        specularC.r = 0;
        specularC.g = 0;
        specularC.b = 0;
    }

    ivec2 coord = ivec2(gl_FragCoord.xy);
    float threshold = thresholdMatrix[coord.x % 4][coord.y % 4] / 17.0;

    if(threshold > ambientC.a || threshold > diffuseC.a || threshold > specularC.a)
    {
        discard;
    }
}

void main()
{
    setupColors();

    vec3 translation = vec3(transformationMatrix[0][3], transformationMatrix[1][3], transformationMatrix[2][3]);
    vec4 diffuseSpecularComp = vec4(0.0,0.0,0.0,0.0);

    for(int i = 0; i < directionalLightsSize; i++)
    {
        if(directionalLights[i].intensity > 0.0)
        {
            diffuseSpecularComp += calcDirectionalLight(directionalLights[i]);
        }
    }
    for(int i = 0; i < pointLightsSize; i++)
    {
        if(pointLights[i].intensity > 0.0)
        {
            diffuseSpecularComp += calcPointLight(pointLights[i]);
        }
    }
    for(int i = 0; i < spotLightsSize; i++)
    {
        if(spotLights[i].pl.intensity > 0.0)
        {
            diffuseSpecularComp += calcSpotLight(spotLights[i]);
        }
    }

    vec3 viewNormal = (viewMatrix * vec4(gl_FrontFacing ? fragNormal : -fragNormal, 0.0)).xyz;
    float rim = pow(1.0 + dot(viewNormal, normalize(viewPosition.xyz)), 3.5f);
    float rim1 = rim * (dot(normalize(sunPos - fragPos), vec3(viewMatrix[0][2], viewMatrix[1][2], viewMatrix[2][2])));
    float rim2 = rim * (dot(normalize(fragPos - sunPos), vec3(viewMatrix[0][2], viewMatrix[1][2], viewMatrix[2][2])));
    rim1 *= 0.275f;
    rim2 *= 0.275f;

    vec3 rimFinal1 = clamp(vec3(rimColor.rgb * rim1 * rimColor.a), 0.0, 1.0);
    vec3 rimFinal2 = clamp(vec3(rimColor2.rgb * rim2 * rimColor2.a), 0.0, 1.0);

    float camToPixelDist = length(fragPos - camPos);
    float fogRange = fogFar - fogNear;
    float fogDist = fogFar - camToPixelDist;
    float fogFactor = clamp(fogDist / fogRange, 0.0f, 1.0f);

    fragmentColor = ambientC * vec4(ambientLight, 1.0f) + diffuseSpecularComp;
    fragmentColor.r = fragmentColor.r + rimFinal1.r + rimFinal2.r;
    fragmentColor.g = fragmentColor.g + rimFinal1.g + rimFinal2.g;
    fragmentColor.b = fragmentColor.b + rimFinal1.b + rimFinal2.b;
    fragmentColor.a = (fogFar == -1 && fogNear == -1) || !preview ? 1.0f : fogFactor;

    fragmentColor = clamp(fragmentColor, 0.0, 1.0);
}