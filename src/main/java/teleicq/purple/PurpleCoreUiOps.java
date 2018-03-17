package teleicq.purple;

import com.sun.jna.*;

import java.util.*;

public class PurpleCoreUiOps extends Structure {
    public interface UiPrefsInit extends Callback { void callback(); }
    public interface DebugUiInit extends Callback { void callback(); }
    public interface UiInit extends Callback { void callback(); }
    public interface Quit extends Callback { void callback(); }
    public interface GetUiInfo extends Callback { Pointer callback(); }

    public UiPrefsInit ui_prefs_init;
    public DebugUiInit debug_ui_init;
    public UiInit ui_init;
    public Quit quit;
    public GetUiInfo get_ui_info;

    public Pointer _purple_reserved1;
    public Pointer _purple_reserved2;
    public Pointer _purple_reserved3;
    public Pointer _purple_reserved4;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(
            "ui_prefs_init",
            "debug_ui_init",
            "ui_init",
            "quit",
            "get_ui_info",
            "_purple_reserved1",
            "_purple_reserved2",
            "_purple_reserved3",
            "_purple_reserved4"
        );
    }
}
