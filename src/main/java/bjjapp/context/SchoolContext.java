package bjjapp.context;

public class SchoolContext {
    private static final ThreadLocal<Long> SCHOOL_ID = new ThreadLocal<>();

    public static void set(Long id) {
        SCHOOL_ID.set(id);
    }

    public static Long get() {
        return SCHOOL_ID.get();
    }

    public static void clear() {
        SCHOOL_ID.remove();
    }
}
