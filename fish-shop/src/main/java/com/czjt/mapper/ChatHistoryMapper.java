package com.czjt.mapper;

import com.czjt.pojo.ChatHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatHistoryMapper {

    int insert(ChatHistory chatHistory);

    List<ChatHistory> findBySessionId(@Param("sessionId") String sessionId);

    List<ChatHistory> findByUserId(@Param("userId") Long userId);
}
