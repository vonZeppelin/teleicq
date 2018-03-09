package teleicq.lowlevel;

import com.sun.jna.*;

public interface GSourceFunc extends Callback {
    boolean callback(Pointer user_data);
}
