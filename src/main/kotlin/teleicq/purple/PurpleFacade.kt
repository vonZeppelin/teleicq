package teleicq.purple

import com.sun.jna.*
import mu.*
import teleicq.glib.*

private const val PURPLE_INPUT_READ = 1 shl 0
private const val PURPLE_INPUT_WRITE = 1 shl 1
private const val PURPLE_GLIB_READ_COND = 1 or 8 or 16
private const val PURPLE_GLIB_WRITE_COND = 4 or 8 or 16 or 32

private val logger = KotlinLogging.logger {}

class PurpleFacade(id: String, userDir: String) : AutoCloseable {
    private val loop = GLib.g_main_loop_new(Pointer.NULL, false)
    private val coreOps = PurpleCoreUiOps()
    private val purpleGLibIOInvoke = GIOFunc { source, condition, data ->
        val closure = PurpleGLibIOClosure(data).apply { read() }
        var purpleCond = 0

        if (condition and PURPLE_GLIB_READ_COND != 0) {
            purpleCond = purpleCond or PURPLE_INPUT_READ
        }
        if (condition and PURPLE_GLIB_WRITE_COND != 0) {
            purpleCond = purpleCond or PURPLE_INPUT_WRITE
        }

        closure.function.callback(
            closure.data, GLib.g_io_channel_unix_get_fd(source), purpleCond
        )
        true
    }
    private val purpleGLibIODestroy = GDestroyNotify { data ->
        Native.free(Pointer.nativeValue(data))
    }
    private val debugOps = PurpleDebugUiOps().apply {
        val logger = KotlinLogging.logger("Purple")
        val loggingFns = listOf<Pair<() -> Boolean, (String) -> Unit>>(
            Pair(logger::isDebugEnabled, logger::debug),
            Pair(logger::isInfoEnabled, logger::info),
            Pair(logger::isWarnEnabled, logger::warn),
            Pair(logger::isErrorEnabled, logger::error),
            Pair(logger::isErrorEnabled, logger::error)
        )
        is_enabled = PurpleDebugUiOps.IsEnabled { level, _ ->
            loggingFns[level - 1].first()
        }
        print = PurpleDebugUiOps.Print { level, _, arg_s ->
            loggingFns[level - 1].second(arg_s.trim())
        }
    }
    private val eventLoopOps = PurpleEventLoopUiOps().apply {
        timeout_add = PurpleEventLoopUiOps.TimeoutAdd { interval, function, data ->
            GLib.g_timeout_add(interval, function, data)
        }
        timeout_remove = PurpleEventLoopUiOps.TimeoutRemove { handle ->
            GLib.g_source_remove(handle)
        }
        input_add = PurpleEventLoopUiOps.InputAdd { fd, condition, func, user_data ->
            val closureSize = Native.getNativeSize(PurpleGLibIOClosure.ByValue::class.java)
            val closurePtr = Pointer(Native.malloc(closureSize.toLong()))
            val closure = PurpleGLibIOClosure(closurePtr).apply {
                function = func
                data = user_data
                write()
            }

            var cond = 0
            if (condition and PURPLE_INPUT_READ != 0) {
                cond = cond or PURPLE_GLIB_READ_COND
            }
            if (condition and PURPLE_INPUT_WRITE != 0) {
                cond = cond or PURPLE_GLIB_WRITE_COND
            }

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
        logger.info {
            "Initializing libpurple v${Purple.purple_core_get_version()}"
        }

        Purple.purple_util_set_user_dir(userDir)
        Purple.purple_debug_set_ui_ops(debugOps)

        Purple.purple_core_set_ui_ops(coreOps)
        Purple.purple_eventloop_set_ui_ops(eventLoopOps)

        if (!Purple.purple_core_init(id)) {
            throw RuntimeException("libpurple initialization failed")
        }

        Purple.purple_set_blist(Purple.purple_blist_new())
        Purple.purple_blist_load()

        Purple.purple_pounces_load()

        kotlin.concurrent.thread {
            GLib.g_main_loop_run(loop)
        }
    }

    override fun close() {
        GLib.g_main_loop_quit(loop)
        Purple.purple_core_quit()
    }
}
