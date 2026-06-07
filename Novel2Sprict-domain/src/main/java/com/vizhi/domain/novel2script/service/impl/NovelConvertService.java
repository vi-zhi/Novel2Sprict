package com.vizhi.domain.novel2script.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.vizhi.domain.agent.model.valobj.AiAgentRegisterVO;
import com.vizhi.domain.novel2script.model.entity.NovelConvertCommandEntity;
import com.vizhi.domain.novel2script.model.valobj.ScriptConvertResultVO;
import com.vizhi.domain.novel2script.service.INovelConvertService;
import io.reactivex.rxjava3.core.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NovelConvertService implements INovelConvertService {

    @Resource
    private ApplicationContext applicationContext;

    private static final String AGENT_ID = "100006";

    @Override
    public ScriptConvertResultVO convertNovelToScript(NovelConvertCommandEntity commandEntity) {
        long startTime = System.currentTimeMillis();

        try {
            log.info("开始转换小说为剧本: userId={}, title={}",
                    commandEntity.getUserId(), commandEntity.getNovelTitle());

            AiAgentRegisterVO agentRegisterVO = getAgentRegisterVO(AGENT_ID);
            InMemoryRunner runner = agentRegisterVO.getRunner();

            Session session = runner.sessionService()
                    .createSession(agentRegisterVO.getAppName(), commandEntity.getUserId())
                    .blockingGet();

            String novelContent = buildNovelContentPrompt(commandEntity);

            StringBuilder fullResponse = new StringBuilder();

            Flowable<Event> events = runner.runAsync(
                    commandEntity.getUserId(),
                    session.id(),
                    Content.fromParts(Part.fromText(novelContent))
            );

            events.blockingForEach(event -> {
                String content = event.stringifyContent();
                if (content != null && !content.isEmpty()) {
                    fullResponse.append(content);
                }
            });

            String result = fullResponse.toString();
            log.info("小说转换完成: userId={}, resultLength={}",
                    commandEntity.getUserId(), result.length());

            ScriptConvertResultVO resultVO = parseConvertResult(result, startTime);
            resultVO.setStatus("success");

            return resultVO;

        } catch (Exception e) {
            log.error("小说转换失败: userId={}", commandEntity.getUserId(), e);
            ScriptConvertResultVO errorVO = ScriptConvertResultVO.builder()
                    .status("error")
                    .convertTime(System.currentTimeMillis() - startTime)
                    .build();
            throw new RuntimeException("小说转换失败: " + e.getMessage(), e);
        }
    }

    private AiAgentRegisterVO getAgentRegisterVO(String agentId) {
        try {
            return applicationContext.getBean(agentId, AiAgentRegisterVO.class);
        } catch (Exception e) {
            log.error("获取Agent失败: agentId={}", agentId, e);
            throw new RuntimeException("Agent未找到: " + agentId);
        }
    }

    private String buildNovelContentPrompt(NovelConvertCommandEntity commandEntity) {
        StringBuilder prompt = new StringBuilder();

        if (commandEntity.getNovelTitle() != null && !commandEntity.getNovelTitle().isEmpty()) {
            prompt.append("小说标题：").append(commandEntity.getNovelTitle()).append("\n\n");
        }

        if (commandEntity.getConvertStyle() != null && !commandEntity.getConvertStyle().isEmpty()) {
            prompt.append("转换风格：").append(commandEntity.getConvertStyle()).append("\n\n");
        }

        prompt.append("小说内容：\n")
                .append(commandEntity.getNovelContent())
                .append("\n\n请将以上小说内容转换为标准剧本格式。");

        return prompt.toString();
    }

    private ScriptConvertResultVO parseConvertResult(String result, long startTime) {
        try {
            JSONObject jsonObject = JSON.parseObject(result);

            String scriptContent = jsonObject.getString("scriptContent");
            String analysisResult = jsonObject.getString("analysisResult");
            Integer sceneCount = jsonObject.getInteger("sceneCount");
            String[] characterList = jsonObject.getObject("characterList", String[].class);

            if (scriptContent == null || scriptContent.isEmpty()) {
                scriptContent = result;
            }

            return ScriptConvertResultVO.builder()
                    .scriptContent(scriptContent)
                    .analysisResult(analysisResult)
                    .sceneCount(sceneCount != null ? sceneCount : 0)
                    .characterList(characterList != null ? characterList : new String[0])
                    .convertTime(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.warn("解析JSON失败，使用原始结果", e);
            return ScriptConvertResultVO.builder()
                    .scriptContent(result)
                    .sceneCount(0)
                    .characterList(new String[0])
                    .convertTime(System.currentTimeMillis() - startTime)
                    .build();
        }
    }
}