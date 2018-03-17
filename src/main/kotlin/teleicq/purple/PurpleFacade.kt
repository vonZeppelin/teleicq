package teleicq.purple

import com.sun.jna.*
import teleicq.glib.*
import java.util.concurrent.*

private const val PURPLE_INPUT_READ = 1 shl 0
private const val PURPLE_INPUT_WRITE = 1 shl 1
private const val PURPLE_GLIB_READ_COND = 1 or 8 or 16
private const val PURPLE_GLIB_WRITE_COND = 4 or 8 or 16 or 32

class PurpleFacade(id: String, userDir: String, debug: Boolean) : AutoCloseable {
    // TODO Fix ioClosures workaround?
    private val ioClosures = ConcurrentHashMap<Pointer, PurpleGLibIOClosure>()
    private val loop = GLib.g_main_loop_new(Pointer.NULL, false)
    private val coreOps = PurpleCoreUiOps()
    private val purpleGLibIOInvoke = GIOFunc { source, condition, data ->
        val closure = ioClosures[data]
        if (closure != null) {
            var purpleCond = 0

            if (condition and PURPLE_GLIB_READ_COND != 0)
                purpleCond = purpleCond or PURPLE_INPUT_READ
            if (condition and PURPLE_GLIB_WRITE_COND != 0)
                purpleCond = purpleCond or PURPLE_INPUT_WRITE

            closure.function.callback(
                closure.data, GLib.g_io_channel_unix_get_fd(source), purpleCond
            )
            true
        } else false
    }
    private val purpleGLibIODestroy = GDestroyNotify { data ->
        ioClosures -= data
    }
    private val eventLoopOps = PurpleEventLoopUiOps().apply {
        timeout_add = PurpleEventLoopUiOps.TimeoutAdd { interval, function, data ->
            GLib.g_timeout_add(interval, function, data)
        }
        timeout_remove = PurpleEventLoopUiOps.TimeoutRemove { handle ->
            GLib.g_source_remove(handle)
        }
        input_add = PurpleEventLoopUiOps.InputAdd { fd, condition, func, user_data ->
            val closure = PurpleGLibIOClosure()
            closure.function = func
            closure.data = user_data
            ioClosures[closure.pointer] = closure

            var cond = 0
            if (condition and PURPLE_INPUT_READ != 0)
                cond = cond or PURPLE_GLIB_READ_COND
            if (condition and PURPLE_INPUT_WRITE != 0)
                cond = cond or PURPLE_GLIB_WRITE_COND

            val channel = GLib.g_io_channel_unix_new(fd)
            closure.result = GLib.g_io_add_watch_full(
                channel, 0, cond, purpleGLibIOInvoke, closure.pointer, purpleGLibIODestroy
            )
            GLib.g_io_channel_unref(channel)
            closure.result
        }
        input_remove = PurpleEventLoopUiOps.InputRemove { handle ->
            GLib.g_source_remove(handle)
        }
        timeout_add_seconds = PurpleEventLoopUiOps.TimeoutAddSeconds { interval, function, data ->
            GLib.g_timeout_add_seconds(interval, function, data)
        }
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

    init {
        Purple.purple_util_set_user_dir(userDir)
        Purple.purple_debug_set_enabled(debug)

        Purple.purple_core_set_ui_ops(coreOps)
        Purple.purple_eventloop_set_ui_ops(eventLoopOps)

        if (!Purple.purple_core_init(id)) {
            throw RuntimeException("libpurple initialization failed")
        }

        Purple.purple_set_blist(Purple.purple_blist_new())
        Purple.purple_blist_load()

        Purple.purple_pounces_load()

        Thread({ GLib.g_main_loop_run(loop) }).start()
    }

    override fun close() {
        GLib.g_main_loop_quit(loop)
        Purple.purple_core_quit()
    }
}
