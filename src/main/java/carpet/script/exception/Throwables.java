package carpet.script.exception;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class contains default Scarpet catchable types, as well as their inheritance and
 * methods to check whether filters are compatible with those.
 */
public class Throwables {
    private final String id;
    private final Throwables parent;

    private static final Map<String, Throwables> byId = new HashMap<>();
    private static final Map<String, Set<Throwables>> handles = new HashMap<>();

    public static final Throwables THROWN_EXCEPTION_TYPE = register("exception", null);
    public static final Throwables VALUE_EXCEPTION       = register("value_exception", THROWN_EXCEPTION_TYPE);
    public static final Throwables UNKNOWN_ITEM          = register("unknown_item", VALUE_EXCEPTION);
    public static final Throwables UNKNOWN_BLOCK         = register("unknown_block", VALUE_EXCEPTION);
    public static final Throwables UNKNOWN_BIOME         = register("unknown_biome", VALUE_EXCEPTION);
    public static final Throwables UNKNOWN_SOUND         = register("unknown_sound", VALUE_EXCEPTION);
    public static final Throwables UNKNOWN_PARTICLE      = register("unknown_particle", VALUE_EXCEPTION);
    public static final Throwables UNKNOWN_POI           = register("unknown_poi", VALUE_EXCEPTION);
    public static final Throwables UNKNOWN_DIMENSION     = register("unknown_dimension", VALUE_EXCEPTION);
    public static final Throwables UNKNOWN_STRUCTURE     = register("unknown_structure", VALUE_EXCEPTION);
    public static final Throwables UNKNOWN_CRITERION     = register("unknown_criterion", VALUE_EXCEPTION);
    public static final Throwables IO_EXCEPTION          = register("io_exception", THROWN_EXCEPTION_TYPE);
    public static final Throwables NBT_READ              = register("nbt_read_error", IO_EXCEPTION);
    public static final Throwables JSON_READ             = register("json_read_error", IO_EXCEPTION);
    public static final Throwables USER_DEFINED          = register("user_exception", THROWN_EXCEPTION_TYPE);

    /**
     * Creates an exception and registers it to be used as parent for
     * user defined exceptions in Scarpet's throw function.
     * <p>Scarpet exceptions should have a top-level parent being {@link Throwables#THROWN_EXCEPTION_TYPE}
     * @param id The value for the exception as a {@link String}.
     * @param parent The parent of the exception being created, or <code>null</code> if top-level
     * @return The created exception
     */
    public static Throwables register(String id, Throwables parent)
    {
        Throwables exc = new Throwables(id, parent);
        byId.put(id, exc);
        Set<Throwables> itHandles = new HashSet<>();
        itHandles.add(exc);
        while (parent!=null)
        {
            handles.get(parent.id).add(exc);
            parent = parent.parent;
        }
        handles.put(id, itHandles);
        return exc;
    }

    /**
     * Creates a new exception.
     * <p>Not suitable for creating exceptions that can't be caught.
     * Use an {@link InternalExpressionException} for that
     * @param id The exception's value as a {@link String}
     */
    protected Throwables(String id, Throwables parent)
    {
        this.id = id;
        this.parent = parent;
    }

    public static Throwables getTypeForException(String type)
    {
        Throwables properType = byId.get(type);
        if (properType == null) throw new InternalExpressionException("Unknown exception type: "+type);
        return properType;
    }


    /**
     * Checks whether the given filter matches an instance of this exception, by checking equality
     * with itself and possible parents.
     * @param filter The type to check against
     * @return Whether or not the given value matches this exception's hierarchy
     */
    public boolean isRelevantFor(String filter)
    {
        Set<Throwables> relevantSet = handles.get(filter);
        if (relevantSet == null) throw new InternalExpressionException("Unknown exception type: "+filter);
        return relevantSet.contains(this);
    }

    /**
     * Returns the throwable type
     * @return The type of this exception
     */
    public String getId() {
        return id;
    }
}
