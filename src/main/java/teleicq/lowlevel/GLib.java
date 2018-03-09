package teleicq.lowlevel;

import com.sun.jna.*;

public final class GLib {

    static {
        Native.register("glib-2.0");
    }

    public static native int g_timeout_add(int interval, GSourceFunc function, Pointer data);
    public static native int g_timeout_add_seconds(int interval, GSourceFunc function, Pointer data);
    public static native boolean g_source_remove(int tag);

    private GLib() {
        throw new UnsupportedOperationException();
    }
}
