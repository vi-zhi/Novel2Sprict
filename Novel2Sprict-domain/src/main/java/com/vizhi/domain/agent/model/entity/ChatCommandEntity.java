package com.vizhi.domain.agent.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-05-15 10:04
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ChatCommandEntity {

    private String agentId;

    private String userId;

    private String sessionId;

    private List<Context.Text> texts;
    private List<Context.File> files;
    private List<Context.InlineData> inlineDatas;

    @Data
    public static class Context{

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Text{
            private String message;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class File{
            private String fileUri;
            private String mimeType;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class InlineData {
            private byte[] bytes;
            private String mimeType;
        }
    }


}
