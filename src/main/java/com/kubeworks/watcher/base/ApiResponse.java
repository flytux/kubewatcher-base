package com.kubeworks.watcher.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    boolean success;
    String message;
    T data;

}
