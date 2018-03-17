package teleicq.purple;

import com.sun.jna.*;

import java.util.*;

public class PurpleGLibIOClosure extends Structure {
    public PurpleInputFunction function;
    public int result;
    public Pointer data;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(
            "function",
            "result",
            "data"
        );
    }
}
