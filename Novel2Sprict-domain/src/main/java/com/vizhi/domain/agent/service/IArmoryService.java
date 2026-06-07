package com.vizhi.domain.agent.service;

import com.vizhi.domain.agent.model.valobj.AiAgentConfigTableVO;

import java.util.List;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-05-08 20:07
 */
public interface IArmoryService {

    void acceptArmoryAgents(List<AiAgentConfigTableVO> tables) throws Exception;

}
