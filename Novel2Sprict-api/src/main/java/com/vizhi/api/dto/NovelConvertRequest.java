package com.vizhi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author vizhi
 * 描述 :
 * @create 2026-06-07 22:15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovelConvertRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    private String novelContent;

    private String novelTitle;

    private String convertStyle;

}
