package com.vizhi.domain.agent.service.armory.matter.skills.impl;

import com.google.genai.types.Tool;
import com.google.genai.types.ToolCall;
import com.vizhi.domain.agent.model.valobj.AiAgentConfigTableVO;
import com.vizhi.domain.agent.service.armory.matter.skills.IToolSkillsCreateService;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.agent.tools.SkillsTool;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-05-16 18:32
 */
@Service
@Slf4j
public class ToolSkillsCreateService implements IToolSkillsCreateService {
    @Override
    public ToolCallback[] buildToolCallback(AiAgentConfigTableVO.Module.ChatModel.ToolSkills toolSkills) {

        String path = toolSkills.getPath();

        List<ToolCallback> toolCallbacks = new ArrayList<>();

        if(toolSkills.getType().equals("resource")){
            ToolCallback toolCallback = SkillsTool.builder().addSkillsResource(new ClassPathResource("agent/skills")).build();
            toolCallbacks.add(toolCallback);
        }

        if(toolSkills.getType().equals("directory")){
            ToolCallback toolCallback = SkillsTool.builder()
                    .addSkillsDirectory(path)
                    .build();
            toolCallbacks.add(toolCallback);
        }


        return toolCallbacks.toArray(new ToolCallback[0]);
    }
}
