package nl.esciencecenter.vlet.exception;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;

public class NestedVrsException extends VrsException {

    private final String name;

    public NestedVrsException(String message, Throwable e)
    {
        super(message,e);
        this.name=e.getClass().getCanonicalName();
    }


    public NestedVrsException(String message, Throwable e, String name)
    {
        super(message,e);
        this.name=name;
    }

    public static VrsException create(String message, Throwable e, String name) {
        return new NestedVrsException(name+":"+message,e);
    }

    public String getName() {
        return name;
    }

}
