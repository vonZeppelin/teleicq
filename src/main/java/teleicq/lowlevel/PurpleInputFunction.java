package teleicq.lowlevel;

import com.sun.jna.*;

public interface PurpleInputFunction extends Callback {
    void callback(Pointer user_data, int fd, int cond);
}
