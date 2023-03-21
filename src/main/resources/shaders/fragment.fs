#version 400 core

in vec2 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragPos;

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

uniform sampler2D textureSampler;
uniform vec3 ambientLight;
uniform Material material;
uniform int highlightMode;
uniform vec3 highlightColor;
uniform float brightnessMul;
uniform float specularPower;
uniform DirectionalLight directionalLights[5];
uniform int directionalLightsSize;
uniform PointLight pointLights[70];
uniform int pointLightsSize;
uniform SpotLight spotLights[70];
uniform int spotLightsSize;

const mat4 thresholdMatrix = mat4(
    1, 9, 3, 11,
    13, 5, 15, 7,
    4, 12, 2, 10,
    16, 8, 14, 6);

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

vec4 calcLightColor(vec3 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
    float diffuseFactor = max(dot(to_light_dir, normal), 0.0);
    vec4 diffuseColor = diffuseC * vec4(light_color, 1.0) * light_intensity * diffuseFactor;

    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflectedLight = normalize(reflect(from_light_dir, normal));
    float specularFactor = max(dot(camera_direction, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    vec4 specularColor = specularC * light_intensity * specularFactor * material.reflectance * vec4(light_color, 1.0);

    vec4 outC = (diffuseColor + specularColor);

    return outC;
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
    vec3 light_dir = light.position - position;

    vec3 to_light_dir = normalize(light_dir);
    vec4 light_color = calcLightColor(light.color, light.intensity, position, normalize(to_light_dir), normalize(normal));

    float dist = length(light_dir);
    float attenuationInv = light.constant + (light.linear * dist) + (light.exponent * dist * dist);

    vec4 outC = light_color / attenuationInv;

    return outC;
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal)
{
    vec3 light_dir = light.pl.position - position;
    vec3 to_light_dir = normalize(light_dir);
    vec3 from_light_dir = -to_light_dir;
    float spot_alfa = dot(from_light_dir, normalize(light.conedir));

    vec4 color = vec4(0,0,0,0);

    if(spot_alfa > light.cutoff)
    {
        color = calcPointLight(light.pl, position, normal);
        color *= (1.0 - (1.0 - spot_alfa) / (1.0 - light.cutoff));
    }

    return color;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}

void setupColors()
{
    if(material.hasTexture == 1)
        {
            ambientC = texture(textureSampler, fragTextureCoord);

            if(highlightMode == 1)
            {
                ambientC = vec4(mix(ambientC.xyz, highlightColor, 0.5).xyz, ambientC.w);
            }
            else if(highlightMode == 2)
            {
                ambientC.r = ambientC.r * brightnessMul;
                ambientC.g = ambientC.g * brightnessMul;
                ambientC.b = ambientC.b * brightnessMul;
            }

            diffuseC = ambientC;
            specularC = ambientC;
        }
        else
        {
            ambientC = material.ambient;
            diffuseC = material.diffuse;
            specularC = material.specular;

            if(highlightMode == 1)
            {
                ambientC = vec4(mix(ambientC.xyz, highlightColor, 0.5).xyz, ambientC.w);
            }
            else if(highlightMode == 2)
            {
                ambientC.r = ambientC.r * brightnessMul;
                ambientC.g = ambientC.g * brightnessMul;
                ambientC.b = ambientC.b * brightnessMul;
            }
        }

        if(ambientC.a < 0.4)
        {
            ambientC.a = 0.4;
            ambientC.r = 0;
            ambientC.g = 0;
            ambientC.b = 0;
        }
        if(diffuseC.a < 0.4)
        {
            diffuseC.a = 0.4;
            diffuseC.r = 0;
            diffuseC.g = 0;
            diffuseC.b = 0;
        }
        if(specularC.a < 0.4)
        {
            specularC.a = 0.4;
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

    vec4 diffuseSpecularComp = vec4(0,0,0,0);

    for(int i = 0; i < directionalLightsSize; i++)
    {
        if(directionalLights[i].intensity > 0)
        {
            diffuseSpecularComp += calcDirectionalLight(directionalLights[i], fragPos, fragNormal);
        }
    }
    for(int i = 0; i < pointLightsSize; i++)
    {
        if(pointLights[i].intensity > 0)
        {
            diffuseSpecularComp += calcPointLight(pointLights[i], fragPos, fragNormal);
        }
    }
    for(int i = 0; i < spotLightsSize; i++)
    {
        if(spotLights[i].pl.intensity > 0)
        {
            diffuseSpecularComp += calcSpotLight(spotLights[i], fragPos, fragNormal);
        }
    }

    fragmentColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
}