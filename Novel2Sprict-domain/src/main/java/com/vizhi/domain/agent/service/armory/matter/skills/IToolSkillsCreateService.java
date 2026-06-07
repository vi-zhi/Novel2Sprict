package com.vizhi.domain.agent.service.armory.matter.skills;

import com.vizhi.domain.agent.model.valobj.AiAgentConfigTableVO;
import org.springframework.ai.tool.ToolCallback;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-05-16 18:32
 */
public interface IToolSkillsCreateService {

    ToolCallback[] buildToolCallback(AiAgentConfigTableVO.Module.ChatModel.ToolSkills toolSkills);

}
