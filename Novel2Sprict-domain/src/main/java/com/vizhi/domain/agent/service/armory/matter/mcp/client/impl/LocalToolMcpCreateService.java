package com.vizhi.domain.agent.service.armory.matter.mcp.client.impl;

import com.vizhi.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.vizhi.domain.agent.service.armory.matter.mcp.client.TooMcpCreateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.MalformedURLException;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-05-13 15:53
 */
@Service
@Slf4j
public class LocalToolMcpCreateService implements TooMcpCreateService {

    @Resource
    protected ApplicationContext applicationContext;

    @Override
    public ToolCallback[] buildToolCallback(AiAgentConfigTableVO.Module.ChatModel.ToolMcp toolMcp) throws MalformedURLException {

        AiAgentConfigTableVO.Module.ChatModel.ToolMcp.LocalServerParameters local = toolMcp.getLocal();
        String beanName = local.getName();

        ToolCallbackProvider localToolCallbackProvider = (ToolCallbackProvider) applicationContext.getBean(local.getName());
        log.info("tool local mcp initialize {}", beanName);

        return localToolCallbackProvider.getToolCallbacks();
    }
}
