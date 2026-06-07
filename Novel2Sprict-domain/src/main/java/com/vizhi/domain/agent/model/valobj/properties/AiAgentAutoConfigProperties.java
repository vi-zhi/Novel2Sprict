package com.vizhi.domain.agent.model.valobj.properties;

import com.vizhi.domain.agent.model.valobj.AiAgentConfigTableVO;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author  vizhi
 * 描述 :  
 * @create 2026-05-08 15:38
 */
@Data
@ConfigurationProperties(prefix = "ai.agent.config", ignoreInvalidFields = true)
public class AiAgentAutoConfigProperties {
    private boolean enabled = false;
    private Map<String , AiAgentConfigTableVO> tables;

}
