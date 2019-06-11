package com.magneton.service.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangmingshuang
 * @since 2019/6/3
 */
public interface Default {
    String NEW_LINE = System.getProperty("line.separator");
    String PLACE_HOLDER = "-";
    List EMPTY_LIST = new ArrayList(0);
    String[] EMPTY_STRING_ARRAY = new String[0];
}
