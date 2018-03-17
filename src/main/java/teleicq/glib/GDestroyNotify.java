package teleicq.glib;

import com.sun.jna.*;

public interface GDestroyNotify extends Callback {
    void callback(Pointer data);
}
