package teleicq.lowlevel;

import com.sun.jna.*;

import java.util.*;

public class PurpleGLibIOClosure extends Structure {
    public PurpleInputFunction function;
    public int result;
    public Pointer data;

    public PurpleGLibIOClosure() {}

    public PurpleGLibIOClosure(Pointer p) { super(p); }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(
            "function",
            "result",
            "add"
        );
    }
}
