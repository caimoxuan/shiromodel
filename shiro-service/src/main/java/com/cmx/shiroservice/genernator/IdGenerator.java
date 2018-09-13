package com.cmx.shiroservice.genernator;

import com.cmx.shiroapi.enums.SerialNumEnum;

public interface IdGenerator {
    /**
     * 生成Id.
     *
     * @param serialNumberType 序列号前缀
     * @return 返回生成的Id, 返回值为@{@link String}对象
     */
    String generateId(SerialNumEnum serialNumberType, Integer appId);

    long generateIdLong(SerialNumEnum serialNumberType, Integer appId);
}
