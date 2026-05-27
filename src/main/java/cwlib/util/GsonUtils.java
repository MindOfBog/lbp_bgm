package cwlib.util;

import bog.lbpas.view3d.utils.print;
import com.google.gson.*;
import cwlib.enums.Branch;
import cwlib.io.gson.*;
import cwlib.structs.things.Thing;
import cwlib.types.data.Revision;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class GsonUtils
{
    public static Revision REVISION = new Revision(Branch.MIZUKI.getHead(), Branch.MIZUKI.getID()
        , Branch.MIZUKI.getRevision());
    public static final HashMap<Integer, Thing> THINGS = new HashMap<>();
    public static final HashSet<Thing> UNIQUE_THINGS = new HashSet<>();
    public static Lock _gsonLock = new ReentrantLock();

    public static Gson GetGson()
    {
        return new GsonBuilder()
            .setPrettyPrinting()
            .serializeSpecialFloatingPointValues()
            .enableComplexMapKeySerialization()
            .serializeNulls()
            .addSerializationExclusionStrategy(new ExclusionStrategy()
            {
                @Override
                public boolean shouldSkipField(FieldAttributes field)
                {
                    boolean skip = false;

                    if (field.getAnnotation(GsonRevision.class) != null)
                    {
                        GsonRevision revision =
                            field.getAnnotation(GsonRevision.class);
                        int head = (revision.lbp3()) ? REVISION.getSubVersion() :
                            REVISION.getVersion();

                        if (revision.branch() != -1 && REVISION.getBranchID() != revision.branch())
                            skip = true;
                        if (revision.max() != -1 && head > revision.max())
                            skip = true;
                        if (revision.min() != -1 && head < revision.min())
                            skip = true;
                    }

                    if (field.getAnnotation(GsonRevisions.class) != null)
                    {
                        GsonRevision[] revisions =
                            field.getAnnotation(GsonRevisions.class).value();
                        boolean anyTrue = false;
                        for (GsonRevision revision : revisions)
                        {
                            int head = (revision.lbp3()) ?
                                REVISION.getSubVersion() :
                                REVISION.getVersion();

                            boolean max =
                                ((revision.max() == -1) || (revision.max() >= head));
                            boolean min =
                                ((revision.min() == -1) || (revision.min() <= head));
                            boolean branch =
                                ((revision.branch() == -1) || (revision.branch() == REVISION.getBranchID()));

                            if (max && min && branch)
                            {
                                anyTrue = true;
                                break;
                            }
                        }
                        skip = !anyTrue;
                    }

                    return skip;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz)
                {
                    return false;
                }
            })
            .registerTypeAdapter(Vector2f.class, new Vector2fSerializer())
            .registerTypeAdapter(Vector3f.class, new Vector3fSerializer())
            .registerTypeAdapter(Vector4f.class, new Vector4fSerializer())
            .registerTypeAdapter(Matrix4f.class, new Matrix4fSerializer())
            .create();
    }

    public static Gson GetGsonForCodeEditor()
    {
        return new GsonBuilder()
            .setPrettyPrinting()
            .serializeSpecialFloatingPointValues()
            .enableComplexMapKeySerialization()
            .serializeNulls()
            .addSerializationExclusionStrategy(new ExclusionStrategy()
            {

                private final Set<Class<?>> blacklisted = new HashSet<>(Arrays.asList(Thing.class));

                @Override
                public boolean shouldSkipField(FieldAttributes field)
                {
                    boolean skip = blacklisted.contains(field.getDeclaredClass());

                    if(field.getName().equalsIgnoreCase("UID") ||
                            field.getName().equalsIgnoreCase("parent") ||
                            field.getName().equalsIgnoreCase("group") )

                    if (field.getAnnotation(GsonRevision.class) != null)
                    {
                        GsonRevision revision =
                            field.getAnnotation(GsonRevision.class);
                        int head = (revision.lbp3()) ? REVISION.getSubVersion() :
                            REVISION.getVersion();

                        if (revision.branch() != -1 && REVISION.getBranchID() != revision.branch())
                            skip = true;
                        if (revision.max() != -1 && head > revision.max())
                            skip = true;
                        if (revision.min() != -1 && head < revision.min())
                            skip = true;
                    }

                    if (field.getAnnotation(GsonRevisions.class) != null)
                    {
                        GsonRevision[] revisions =
                            field.getAnnotation(GsonRevisions.class).value();
                        boolean anyTrue = false;
                        for (GsonRevision revision : revisions)
                        {
                            int head = (revision.lbp3()) ?
                                REVISION.getSubVersion() :
                                REVISION.getVersion();

                            boolean max =
                                ((revision.max() == -1) || (revision.max() >= head));
                            boolean min =
                                ((revision.min() == -1) || (revision.min() <= head));
                            boolean branch =
                                ((revision.branch() == -1) || (revision.branch() == REVISION.getBranchID()));

                            if (max && min && branch)
                            {
                                anyTrue = true;
                                break;
                            }
                        }
                        skip = !anyTrue;
                    }

                    return skip;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz)
                {
                    return blacklisted.contains(clazz);
                }
            })
            .registerTypeAdapter(Vector2f.class, new Vector2fSerializer())
            .registerTypeAdapter(Vector3f.class, new Vector3fSerializer())
            .registerTypeAdapter(Vector4f.class, new Vector4fSerializer())
            .registerTypeAdapter(Matrix4f.class, new Matrix4fSerializer())
            .create();
    }

    /**
     * Deserializes a JSON string to an object.
     *
     * @param <T>   Type to deserialize
     * @param json  JSON object to deserialize
     * @param clazz Class to deserialize
     * @return Deserialized object
     */
    public static <T> T fromJSON(String json, Class<T> clazz)
    {
        _gsonLock.lock();
        try
        {
            THINGS.clear();
            UNIQUE_THINGS.clear();
            return GetGson().fromJson(json, clazz);
        }
        finally { _gsonLock.unlock(); }
    }

    public static <T> T fromJSONCodeEditor(String json, Class<T> clazz)
    {
        _gsonLock.lock();
        try
        {
            THINGS.clear();
            UNIQUE_THINGS.clear();
            return GetGsonForCodeEditor().fromJson(json, clazz);
        }
        finally { _gsonLock.unlock(); }
    }

    /**
     * Serializes an object to a JSON string.
     *
     * @param object Object to serialize
     * @return Serialized JSON string
     */
    public static String toJSON(Object object)
    {
        _gsonLock.lock();
        try
        {
            THINGS.clear();
            UNIQUE_THINGS.clear();
            return GetGson().toJson(object);
        }
        finally { _gsonLock.unlock(); }
    }
    public static String toJSONCodeEditor(Object object)
    {
        _gsonLock.lock();
        try
        {
            THINGS.clear();
            UNIQUE_THINGS.clear();
            return GetGsonForCodeEditor().toJson(object);
        }
        finally { _gsonLock.unlock(); }
    }

    /**
     * Serializes an object to a JSON string with revision.
     *
     * @param object Object to serialize
     * @return Serialized JSON string
     */
    public static String toJSON(Object object, Revision revision)
    {
        _gsonLock.lock();
        try
        {
            REVISION = revision;
            THINGS.clear();
            UNIQUE_THINGS.clear();
            return GetGson().toJson(object);
        }
        finally { _gsonLock.unlock(); }

    }

    public static class CodeEditorUtil {

        public static void mergeExcluding(Object source, Object dest, Class<?>... blacklisted) {
            Set<Class<?>> blacklist = new HashSet<>(Arrays.asList(blacklisted));

            for (Field field : getAllFields(dest.getClass())) {
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (isBlacklisted(field.getType().getComponentType(), blacklist)) continue;

                try {
                    Object sourceVal = field.get(source);
                    Object destVal = field.get(dest);

                    field.set(dest, sourceVal);
                } catch (IllegalAccessException e) {print.stackTrace(e);}
            }
        }

        private static boolean isBlacklisted(Class<?> type, Set<Class<?>> blacklist) {
            if (type == null) return false;
            for (Class<?> b : blacklist) {
                if (b.isAssignableFrom(type)) return true;
            }
            return false;
        }

        private static List<Field> getAllFields(Class<?> clazz) {
            List<Field> fields = new ArrayList<>();
            while (clazz != null && clazz != Object.class) {
                for (Field f : clazz.getDeclaredFields())
                    if (!Modifier.isStatic(f.getModifiers())) fields.add(f);
                clazz = clazz.getSuperclass();
            }
            return fields;
        }
    }
}
