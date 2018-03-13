package teleicq.lowlevel;

import com.sun.jna.*;

public interface GDestroyNotify extends Callback {
    void callback(Pointer data);
}
