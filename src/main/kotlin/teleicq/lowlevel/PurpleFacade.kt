package teleicq.lowlevel

import java.util.Collections.unmodifiableList

object PurpleFacade {
    private val coreOps = PurpleCoreUiOps()
    private val eventLoopOps = PurpleEventLoopUiOps().apply {
        timeout_add = PurpleEventLoopUiOps.TimeoutAdd { interval, function, data ->
            GLib.g_timeout_add(interval, function, data)
        }
        timeout_remove = PurpleEventLoopUiOps.TimeoutRemove { handle ->
            GLib.g_source_remove(handle)
        }
        input_add = PurpleEventLoopUiOps.InputAdd { fd, condition, func, user_data ->
            TODO()
        }
        input_remove = PurpleEventLoopUiOps.InputRemove { handle ->
            GLib.g_source_remove(handle)
        }
        timeout_add_seconds = PurpleEventLoopUiOps.TimeoutAddSeconds { interval, function, data ->
            GLib.g_timeout_add_seconds(interval, function, data)
        }
    }

    fun init(id: String, userDir: String? = null, debug: Boolean = false) {
        Purple.purple_util_set_user_dir(userDir)
        Purple.purple_debug_set_enabled(debug)

        Purple.purple_core_set_ui_ops(coreOps)
        Purple.purple_eventloop_set_ui_ops(eventLoopOps)

        if (!Purple.purple_core_init(id)) {
            throw RuntimeException("libpurple initialization failed")
        }
    }

    fun quit() {
        Purple.purple_core_quit()
    }

    val protocols : List<String>
        get() {
            val protocolNames = mutableListOf<String>()
            var itr = Purple.purple_plugins_get_protocols()
            while (itr != null) {
                protocolNames += Purple.purple_plugin_get_name(itr.data)
                itr = itr.next
            }
            return protocolNames
        }
}
