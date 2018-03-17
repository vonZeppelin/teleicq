package teleicq.glib;

import com.sun.jna.*;

public interface GSourceFunc extends Callback {
    boolean callback(Pointer user_data);
}
