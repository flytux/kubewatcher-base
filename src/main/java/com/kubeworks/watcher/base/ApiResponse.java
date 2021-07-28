package com.kubeworks.watcher.base;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
}
