package com.vizhi.domain.novel2script.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelConvertCommandEntity {

    private String userId;

    private String novelContent;

    private String novelTitle;

    private String convertStyle;

}