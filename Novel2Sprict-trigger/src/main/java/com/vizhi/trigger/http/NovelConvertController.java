
package com.vizhi.trigger.http;

import com.vizhi.api.dto.NovelConvertRequest;
import com.vizhi.api.dto.ScriptConvertResponse;
import com.vizhi.domain.novel2script.model.entity.NovelConvertCommandEntity;
import com.vizhi.domain.novel2script.model.valobj.ScriptConvertResultVO;
import com.vizhi.domain.novel2script.service.INovelConvertService;
import com.vizhi.types.enums.ResponseCode;
import com.vizhi.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/novel2script")
@CrossOrigin(origins = "*")
public class NovelConvertController {

    @Resource
    private INovelConvertService novelConvertService;

    @PostMapping("/convert")
    public ScriptConvertResponse convertNovel(@RequestBody NovelConvertRequest request) {
        try {
            log.info("接收到小说转换请求: userId={}, title={}",
                    request.getUserId(), request.getNovelTitle());

            if (request.getNovelContent() == null || request.getNovelContent().trim().isEmpty()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "小说内容不能为空");
            }

            NovelConvertCommandEntity commandEntity = NovelConvertCommandEntity.builder()
                    .userId(request.getUserId())
                    .novelContent(request.getNovelContent())
                    .novelTitle(request.getNovelTitle())
                    .convertStyle(request.getConvertStyle())
                    .build();

            ScriptConvertResultVO resultVO = novelConvertService.convertNovelToScript(commandEntity);

            return ScriptConvertResponse.builder()
                    .scriptContent(resultVO.getScriptContent())
                    .analysisResult(resultVO.getAnalysisResult())
                    .sceneCount(resultVO.getSceneCount())
                    .characterList(resultVO.getCharacterList())
                    .build();

        } catch (AppException e) {
            log.error("参数验证失败", e);
            throw e;
        } catch (Exception e) {
            log.error("小说转换失败", e);
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "小说转换失败: " + e.getMessage());
        }
    }

    @PostMapping("/convert/file")
    public ScriptConvertResponse convertNovelFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", required = false, defaultValue = "default_user") String userId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "style", required = false) String style) {

        try {
            log.info("接收到小说文件转换请求: fileName={}, userId={}",
                    file.getOriginalFilename(), userId);

            if (file.isEmpty()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "上传文件不能为空");
            }

            String fileName = file.getOriginalFilename();
            if (fileName != null && !fileName.toLowerCase().endsWith(".txt")) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "仅支持TXT格式文件");
            }

            String content;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                content = reader.lines().collect(Collectors.joining("\n"));
            }

            if (content.trim().isEmpty()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "文件内容为空");
            }

            if (title == null || title.isEmpty()) {
                title = fileName != null ? fileName.replace(".txt", "") : "未命名小说";
            }

            NovelConvertCommandEntity commandEntity = NovelConvertCommandEntity.builder()
                    .userId(userId)
                    .novelContent(content)
                    .novelTitle(title)
                    .convertStyle(style)
                    .build();

            ScriptConvertResultVO resultVO = novelConvertService.convertNovelToScript(commandEntity);

            return ScriptConvertResponse.builder()
                    .scriptContent(resultVO.getScriptContent())
                    .analysisResult(resultVO.getAnalysisResult())
                    .sceneCount(resultVO.getSceneCount())
                    .characterList(resultVO.getCharacterList())
                    .build();

        } catch (AppException e) {
            log.error("参数验证失败", e);
            throw e;
        } catch (IOException e) {
            log.error("文件读取失败", e);
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("小说文件转换失败", e);
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "小说转换失败: " + e.getMessage());
        }
    }
}