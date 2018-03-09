package teleicq.lowlevel;

import com.sun.jna.*;

public final class Purple {

    static {
        Native.register("purple");
    }

    // core.h
    public static native String purple_core_get_version();
    public static native boolean purple_core_init(String ui);
    public static native void purple_core_quit();
    public static native void purple_core_set_ui_ops(PurpleCoreUiOps ops);

    // debug.h
    public static native void purple_debug_set_enabled(boolean enabled);

    // eventloop.h
    public static native void purple_eventloop_set_ui_ops(PurpleEventLoopUiOps ops);

    // plugin.h
    public static native GList purple_plugins_get_protocols();
    public static native String purple_plugin_get_name(Pointer plugin);

    // util.h
    public static native void purple_util_set_user_dir(String dir);

    private Purple() {
        throw new UnsupportedOperationException();
    }
}
