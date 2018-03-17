package teleicq.purple;

import com.sun.jna.*;

public interface PurpleInputFunction extends Callback {
    void callback(Pointer user_data, int fd, int condition);
}
