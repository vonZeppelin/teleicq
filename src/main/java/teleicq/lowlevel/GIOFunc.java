package teleicq.lowlevel;

import com.sun.jna.*;

public interface GIOFunc extends Callback {
    boolean callback(Pointer source, int condition, Pointer data);
}
