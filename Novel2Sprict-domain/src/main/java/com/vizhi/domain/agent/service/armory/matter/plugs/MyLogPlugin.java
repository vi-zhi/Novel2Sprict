package com.vizhi.domain.agent.service.armory.matter.plugs;

import com.google.adk.plugins.LoggingPlugin;
import org.springframework.stereotype.Service;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-05-13 17:58
 */
@Service("myLogPlugin")
public class MyLogPlugin extends LoggingPlugin {

    public MyLogPlugin(){
        super("myLogPlugin");
    }

    public MyLogPlugin(String name){
        super(name);
    }

}
