package teleicq.lowlevel;

import com.sun.jna.*;

import java.util.*;

public class PurpleEventLoopUiOps extends Structure {
    public interface TimeoutAdd extends Callback { int callback(int interval, GSourceFunc function, Pointer data); }
    public interface TimeoutRemove extends Callback { boolean callback(int handle); }
    public interface InputAdd extends Callback { int callback(int fd, int cond, PurpleInputFunction func, Pointer user_data); }
    public interface InputRemove extends Callback { boolean callback(int handle); }
    public interface InputGetError extends Callback { int callback(int fd, Pointer error); }
    public interface TimeoutAddSeconds extends Callback { int callback(int interval, GSourceFunc function, Pointer data); }


    public TimeoutAdd timeout_add;
    public TimeoutRemove timeout_remove;
    public InputAdd input_add;
    public InputRemove input_remove;
    public InputGetError input_get_error;
    public TimeoutAddSeconds timeout_add_seconds;

    public Pointer _purple_reserved2;
    public Pointer _purple_reserved3;
    public Pointer _purple_reserved4;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(
            "timeout_add",
            "timeout_remove",
            "input_add",
            "input_remove",
            "input_get_error",
            "timeout_add_seconds",
            "_purple_reserved2",
            "_purple_reserved3",
            "_purple_reserved4"
        );
    }
}
