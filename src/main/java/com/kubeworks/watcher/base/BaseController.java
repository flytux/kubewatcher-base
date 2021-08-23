package com.kubeworks.watcher.base;

public interface BaseController {

    default String retrieveViewNamePrefix() {
        return "";
    }

    default String createViewName(final String name) {
        return createViewName(name, "");
    }

    default String createViewName(final String name, final String modal) {
        return retrieveViewNamePrefix() + name + modal;
    }

    final class Props {

        public static final String LINK = "link";
        public static final String PAGE = "page";
        public static final String HOST = "host";

        public static final String NAMESPACES = "namespaces";
        public static final String CONTENT_LIST = " :: contentList";
        public static final String MODAL_CONTENTS = " :: modalContents";

        private Props() {
            throw new UnsupportedOperationException("Cannot instantiate this class");
        }
    }
}
