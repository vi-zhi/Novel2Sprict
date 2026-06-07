package com.vizhi.domain.novel2script.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScriptConvertResultVO {

    private String scriptContent;

    private String analysisResult;

    private Integer sceneCount;

    private String[] characterList;

    private Long convertTime;

    private String status;

}