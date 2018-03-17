package teleicq.glib;

import com.sun.jna.*;

import java.util.*;

public class GList extends Structure {
    public static class ByReference extends GList implements Structure.ByReference {}

    public Pointer data;
    public ByReference next;
    public ByReference prev;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(
            "data",
            "next",
            "prev"
        );
    }
}
