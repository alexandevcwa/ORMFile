package com.ormfile.exeption;

public class OrmFileDbSetError extends Exception{
    public OrmFileDbSetError() {
        super();
    }

    public OrmFileDbSetError(String message) {
        super(message);
    }
}
