package com.example.candlesuppliesmanager;

import androidx.annotation.NonNull;

public class Warehouse {
    private String description;
    private String ref;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @NonNull
    @Override
    public String toString() {
        return description;
    }
}
