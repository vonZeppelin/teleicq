package teleicq.purple;

import com.sun.jna.*;

import java.util.*;

public class PurpleDebugUiOps extends Structure {
    public interface Print extends Callback { void callback(int level, String category, String arg_s); }
    public interface IsEnabled extends Callback { boolean callback(int level, String category); }

    public Print print;
    public IsEnabled is_enabled;

    public Pointer _purple_reserved1;
    public Pointer _purple_reserved2;
    public Pointer _purple_reserved3;
    public Pointer _purple_reserved4;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(
            "print",
            "is_enabled",
            "_purple_reserved1",
            "_purple_reserved2",
            "_purple_reserved3",
            "_purple_reserved4"
        );
    }
}
