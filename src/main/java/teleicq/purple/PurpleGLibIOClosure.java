package teleicq.purple;

import com.sun.jna.*;

import java.util.*;

public class PurpleGLibIOClosure extends Structure {
    public static class ByValue extends PurpleGLibIOClosure implements Structure.ByValue {}

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
            "data"
        );
    }
}
