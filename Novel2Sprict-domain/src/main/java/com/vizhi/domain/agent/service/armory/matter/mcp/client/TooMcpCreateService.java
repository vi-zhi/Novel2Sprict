package com.vizhi.domain.agent.service.armory.matter.mcp.client;

import com.vizhi.domain.agent.model.valobj.AiAgentConfigTableVO;
import org.springframework.ai.tool.ToolCallback;

import java.net.MalformedURLException;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-05-13 15:27
 */
public interface TooMcpCreateService {

    ToolCallback[] buildToolCallback(AiAgentConfigTableVO.Module.ChatModel.ToolMcp toolMcp) throws MalformedURLException;

}
