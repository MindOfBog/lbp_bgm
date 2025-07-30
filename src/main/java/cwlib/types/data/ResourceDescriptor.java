package cwlib.types.data;

import com.google.gson.annotations.JsonAdapter;
import cwlib.enums.ResourceFlags;
import cwlib.enums.ResourceType;
import cwlib.io.gson.ResourceSerializer;
import cwlib.types.archives.Fart;
import cwlib.types.databases.FileEntry;
import cwlib.types.save.BigSave;
import cwlib.util.Strings;

import java.nio.charset.StandardCharsets;

/**
 * Represents references to resources in either
 * the UGC/Profile caches and data archives,
 * or the FileDB.
 */
@JsonAdapter(ResourceSerializer.class)
public final class ResourceDescriptor {
    private final ResourceType type;
    private final GUID guid;
    private final SHA1 sha1;
    private transient int flags;

    /**
     * Constructs a ResourceReference from parsed string and type.
     * @param resource
     * @param type
     */
    public ResourceDescriptor(String resource, ResourceType type) {
        if (Strings.isSHA1(resource)) {
            SHA1 sha1 = Strings.getSHA1(resource);
            this.sha1 = SHA1.EMPTY.equals(sha1) ? null : sha1;
            this.guid = null;
        } else if (Strings.isGUID(resource)) {
            this.guid = new GUID(Strings.getLong(resource));
            this.sha1 = null;
        }
        else throw new IllegalArgumentException("Invalid resource reference passed into resource reference!");

        this.type = type;
    }

    /**
     * Constructs a ResourceReference with GUID and type.
     * @param guid Unique identifer of resource
     * @param type Type of resource
     */
    public ResourceDescriptor(long guid, ResourceType type) {
        this.guid = new GUID(guid);
        this.type = type;
        this.sha1 = null;
        this.flags = ResourceFlags.NONE;
    }

    /**
     * Constructs a ResourceReference with GUID and type.
     * @param guid Unique identifer of resource
     * @param type Type of resource
     */
    public ResourceDescriptor(GUID guid, ResourceType type) {
        this.guid = guid;
        this.type = type;
        this.sha1 = null;
        this.flags = ResourceFlags.NONE;
    }

    /**
     * Constructs a ResourceReference with SHA1 and type.
     * @param sha1 SHA1 signature of resource
     * @param type Type of resource
     */
    public ResourceDescriptor(SHA1 sha1, ResourceType type) {
        this.sha1 = SHA1.EMPTY.equals(sha1) ? null : sha1;
        this.type = type;
        this.guid = null;
        this.flags = ResourceFlags.NONE;
    }

    /**
     * Constructs a ResourceReference with SHA1, GUID, and type.
     * @param guid Unique identifier of resource
     * @param sha1 SHA1 signature of resource
     * @param type Type of resource
     */
    public ResourceDescriptor(GUID guid, SHA1 sha1, ResourceType type) {
        this.guid = guid;
        this.sha1 = SHA1.EMPTY.equals(sha1) ? null : sha1;
        this.type = type;
        this.flags = ResourceFlags.NONE;
    }

    /**
     * Constructs a ResourceReference with SHA1, GUID, and type from a FileEntry.
     * @param entry FileEntry of the resource from a MAP or other database
     * @param archive Fart archive in which resource is located
     */

    public ResourceDescriptor(FileEntry entry, Fart archive)
    {
        Object key = entry.getKey();
        this.guid = key != null && key instanceof GUID ? (GUID) key : null;
        this.sha1 = entry.getSHA1();
        this.type = ResourceType.fromMagic(new String(archive.extract(entry.getSHA1(), 3), StandardCharsets.UTF_8));
        this.flags = ResourceFlags.NONE;

        if(entry.getInfo() == null)
            entry.setInfo(new ResourceInfo());

        entry.getInfo().setType(this.type);
    }

    public ResourceDescriptor(FileEntry entry, BigSave archive)
    {
        Object key = entry.getKey();
        this.guid = key != null && key instanceof GUID ? (GUID) key : null;
        this.sha1 = entry.getSHA1();
        byte[] data = archive.archive.extract(entry.getSHA1(), 3);
            this.type = data != null ? ResourceType.fromMagic(new String(data, StandardCharsets.UTF_8)) : ResourceType.PLAN;
        this.flags = ResourceFlags.NONE;

        if(entry.getInfo() == null)
            entry.setInfo(new ResourceInfo());

        entry.getInfo().setType(this.type);
    }

    public ResourceDescriptor(FileEntry entry)
    {
        Object key = entry.getKey();
        this.guid = key != null && key instanceof GUID ? (GUID) key : null;
        this.sha1 = entry.getSHA1();
        this.type = ResourceType.fromExtension(entry.getPath());
        this.flags = ResourceFlags.NONE;

        if(entry.getInfo() == null)
            entry.setInfo(new ResourceInfo());

        entry.getInfo().setType(this.type);
    }

    /**
     * Is this resource a GUID reference?
     * @return Whether or not this resource testForMouse a GUID reference
     */
    public boolean isGUID() { return this.guid != null; }

    /**
     * IS this resource a SHA1 reference?
     * @return Whether or not this resource testForMouse a SHA1 reference
     */
    public boolean isHash() { return this.sha1 != null; }


    /**
     * Checks if this resource descriptor has either a valid hash or GUID.
     * @return
     */
    public boolean isValid() {
        if (this.guid != null) return true;
        if (this.sha1 != null) {
            if (this.sha1.equals(SHA1.EMPTY)) return false;
            return true;
        }
        return false;
    }

    public GUID getGUID() { return this.guid; }
    public SHA1 getSHA1() { return this.sha1; }
    public ResourceType getType() { return this.type; }

    public int getFlags() { return this.flags; }
    public void setFlags(int flags) { this.flags = flags; }

    @Override public int hashCode() { return this.toString().hashCode(); }

    @Override public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof ResourceDescriptor)) return false;
        ResourceDescriptor reference = (ResourceDescriptor) other;
        return reference.toString().equals(this.toString());
    }

    @Override public String toString() {
        if (this.sha1 != null && this.guid != null)
            return String.format("%s (%s)", this.sha1, this.guid);
        if (this.sha1 != null) 
            return "h" + this.sha1.toString();
        else if (this.guid != null)
            return this.guid.toString();
        return "null";
    }
}
