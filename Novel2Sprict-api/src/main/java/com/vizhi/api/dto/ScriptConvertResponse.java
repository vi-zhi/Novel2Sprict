package com.vizhi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-06-07 22:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScriptConvertResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String scriptContent;

    private String analysisResult;

    private Integer sceneCount;

    private String[] characterList;

}