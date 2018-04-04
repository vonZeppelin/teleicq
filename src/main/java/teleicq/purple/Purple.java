package teleicq.purple;

import com.sun.jna.*;
import teleicq.glib.*;

public final class Purple {

    static {
        Native.register("purple");
    }

    // blist.h
    public static native Pointer purple_blist_new();
    public static native void purple_blist_load();
    public static native void purple_set_blist(Pointer blist);

    // core.h
    public static native String purple_core_get_version();
    public static native boolean purple_core_init(String ui);
    public static native void purple_core_quit();
    public static native void purple_core_set_ui_ops(PurpleCoreUiOps ops);

    // debug.h
    public static native void purple_debug_set_enabled(boolean enabled);
    public static native void purple_debug_set_ui_ops(PurpleDebugUiOps ops);

    // eventloop.h
    public static native void purple_eventloop_set_ui_ops(PurpleEventLoopUiOps ops);

    // plugin.h
    public static native GList purple_plugins_get_protocols();
    public static native String purple_plugin_get_name(Pointer plugin);

    // pounce.h
    public static native boolean purple_pounces_load();

    // prefs.h
    public static native boolean purple_prefs_load();

    // util.h
    public static native void purple_util_set_user_dir(String dir);

    private Purple() {
        throw new UnsupportedOperationException();
    }
}
