package com.vizhi.domain.agent.service.armory.matter.plugs;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.CallbackContext;
import com.google.adk.agents.InvocationContext;
import com.google.adk.models.LlmRequest;
import com.google.adk.models.LlmResponse;
import com.google.adk.plugins.BasePlugin;
import com.google.genai.types.Content;
import io.reactivex.rxjava3.core.Maybe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-05-13 17:40
 */
@Service("myTestPlugin")
@Slf4j
public class MyTestPlugin extends BasePlugin {

    public MyTestPlugin(){
        super("myTestPlugin");
    }

    public MyTestPlugin(String name){
        super(name);
    }

    @Override
    public Maybe<Content> onUserMessageCallback(InvocationContext invocationContext, Content userMessage) {
        log.info("用户输入的信息 : {}" , userMessage.text());
        return super.onUserMessageCallback(invocationContext, userMessage);
    }

    @Override
    public Maybe<Content> beforeAgentCallback(BaseAgent agent, CallbackContext callbackContext) {
        String agentName = agent.name();
        log.info("智能体名称 : {}" , agentName);
        return super.beforeAgentCallback(agent, callbackContext);
    }


}
