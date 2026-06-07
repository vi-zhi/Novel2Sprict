package com.vizhi.domain.agent.service.armory.matter.mcp.client.factory;

import com.vizhi.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.vizhi.domain.agent.service.armory.matter.mcp.client.TooMcpCreateService;
import com.vizhi.domain.agent.service.armory.matter.mcp.client.impl.LocalToolMcpCreateService;
import com.vizhi.domain.agent.service.armory.matter.mcp.client.impl.SSEToolMcpCreateService;
import com.vizhi.domain.agent.service.armory.matter.mcp.client.impl.StdioToolMcpCreateService;
import com.vizhi.types.enums.ResponseCode;
import com.vizhi.types.exception.AppException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-05-13 15:53
 */
@Service
public class DefaultMcpClientFactory {


    @Resource
    private LocalToolMcpCreateService localToolMcpCreateService;
    @Resource
    private StdioToolMcpCreateService stdioToolMcpCreateService;
    @Resource
    private SSEToolMcpCreateService sseToolMcpCreateService;

    public TooMcpCreateService getTooMcpCreateService(AiAgentConfigTableVO.Module.ChatModel.ToolMcp toolMcp){

        if(toolMcp.getSse() != null) return sseToolMcpCreateService;
        if(toolMcp.getStdio() != null) return stdioToolMcpCreateService;
        if(toolMcp.getLocal() != null) return localToolMcpCreateService;
        throw new AppException(ResponseCode.NOT_FOUND_METHOD.getCode() , ResponseCode.NOT_FOUND_METHOD.getInfo());
    }


}
