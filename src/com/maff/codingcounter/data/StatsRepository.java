package com.maff.codingcounter.data;

import java.io.IOException;

/**
 * 数据持久化接口
 */
public interface StatsRepository {
    CodingStats load() throws IOException;
    void save(CodingStats var1) throws IOException;
}
