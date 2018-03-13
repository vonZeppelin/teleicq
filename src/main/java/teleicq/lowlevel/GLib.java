package teleicq.lowlevel;

import com.sun.jna.*;

public final class GLib {

    static {
        Native.register("glib-2.0");
    }

    // giochannel.h
    public static native int g_io_add_watch_full(
        Pointer channel, int priority, int condition, GIOFunc func, Pointer user_data, GDestroyNotify notify
    );
    public static native int g_io_channel_unix_get_fd(Pointer channel);
    public static native Pointer g_io_channel_unix_new(int fd);
    public static native void g_io_channel_unref(Pointer channel);

    // gmain.h
    public static native Pointer g_main_loop_new(Pointer context, boolean is_running);
    public static native void g_main_loop_quit(Pointer loop);
    public static native void g_main_loop_run(Pointer loop);
    public static native boolean g_source_remove(int tag);
    public static native int g_timeout_add(int interval, GSourceFunc function, Pointer data);
    public static native int g_timeout_add_seconds(int interval, GSourceFunc function, Pointer data);

    private GLib() {
        throw new UnsupportedOperationException();
    }
}
