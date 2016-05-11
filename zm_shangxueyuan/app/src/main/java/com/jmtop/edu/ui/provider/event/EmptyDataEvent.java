package com.jmtop.edu.ui.provider.event;


public class EmptyDataEvent {
    private boolean isEmpty;

    public EmptyDataEvent(boolean isEmpty) {
        super();
        this.isEmpty = isEmpty;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

}
