package com.vizhi.domain.agent.service.armory.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.springai.SpringAI;
import com.vizhi.domain.agent.model.entity.ArmoryCommandEntity;
import com.vizhi.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.vizhi.domain.agent.model.valobj.AiAgentRegisterVO;
import com.vizhi.domain.agent.service.armory.AbstractArmorySupport;
import com.vizhi.domain.agent.service.armory.factory.DefaultArmoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.google.adk.agents.LlmAgent.IncludeContents.NONE;

/**
 * @author vizhi
 * 描述 : Ai Agent 装配操作
 * @create 2026-05-10 10:31
 */
@Service
@Slf4j
public class AgentNode extends AbstractArmorySupport {

    @Resource
    private AgentWorkflowNode agentWorkflowNode;

    @Override
    protected AiAgentRegisterVO doApply(ArmoryCommandEntity requestParameter, DefaultArmoryFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Ai Agent 装配操作 - AgentNode");

        ChatModel chatModel = dynamicContext.getChatModel();

        AiAgentConfigTableVO aiAgentConfigTableVO = requestParameter.getAiAgentConfigTableVO();
        List<AiAgentConfigTableVO.Module.Agent> agents = aiAgentConfigTableVO.getModule().getAgents();

        for(AiAgentConfigTableVO.Module.Agent agent : agents){
            LlmAgent llmAgent = LlmAgent.builder()
                    .name(agent.getName())
                    .description(agent.getDescription())
                    .model(new SpringAI(chatModel))
                    .instruction(agent.getInstruction())
                    .outputKey(agent.getOutputKey())
                    .build();

            dynamicContext.getAgentGroup().put(agent.getName() , llmAgent);
        }
        return router(requestParameter , dynamicContext);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryFactory.DynamicContext, AiAgentRegisterVO> get(ArmoryCommandEntity armoryCommandEntity, DefaultArmoryFactory.DynamicContext dynamicContext) throws Exception {
        return agentWorkflowNode;
    }
}
