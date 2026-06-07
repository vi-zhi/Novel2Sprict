package com.vizhi.domain.novel2script.service;

import com.vizhi.domain.novel2script.model.entity.NovelConvertCommandEntity;
import com.vizhi.domain.novel2script.model.valobj.ScriptConvertResultVO;

/**
 * @author  vizhi
 * 描述 :  
 * @create 2026-06-07 22:55
 */
public interface INovelConvertService {
    ScriptConvertResultVO convertNovelToScript(NovelConvertCommandEntity novelConvertCommandEntity);
}
