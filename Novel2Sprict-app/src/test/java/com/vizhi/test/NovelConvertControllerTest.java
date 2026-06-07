package com.vizhi.test;

import com.vizhi.api.dto.NovelConvertRequest;
import com.vizhi.api.dto.ScriptConvertResponse;
import com.vizhi.domain.novel2script.model.entity.NovelConvertCommandEntity;
import com.vizhi.domain.novel2script.model.valobj.ScriptConvertResultVO;
import com.vizhi.domain.novel2script.service.INovelConvertService;
import com.vizhi.trigger.http.NovelConvertController;
import com.vizhi.types.enums.ResponseCode;
import com.vizhi.types.exception.AppException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 小说转剧本控制器单元测试
 */
@RunWith(SpringRunner.class)
@WebMvcTest(NovelConvertController.class)
public class NovelConvertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private INovelConvertService novelConvertService;

    private NovelConvertRequest validRequest;
    private ScriptConvertResponse expectedResponse;
    private ScriptConvertResultVO mockResultVO;

    @Before
    public void setUp() {
        // 准备测试数据
        validRequest = NovelConvertRequest.builder()
                .userId("test_user")
                .novelTitle("测试小说")
                .novelContent("这是一段测试内容。")
                .convertStyle("现代都市剧")
                .build();

        mockResultVO = ScriptConvertResultVO.builder()
                .scriptContent("【场景标题】\n内.咖啡厅-日\n\n李明走进咖啡厅...")
                .analysisResult("{\"characters\":[{\"name\":\"李明\"}]}")
                .sceneCount(1)
                .characterList(new String[]{"李明"})
                .status("success")
                .convertTime(1000L)
                .build();

        expectedResponse = ScriptConvertResponse.builder()
                .scriptContent(mockResultVO.getScriptContent())
                .analysisResult(mockResultVO.getAnalysisResult())
                .sceneCount(mockResultVO.getSceneCount())
                .characterList(mockResultVO.getCharacterList())
                .build();
    }

    /**
     * 测试1：正常文本转换 - 成功场景
     */
    @Test
    public void test_convertNovel_Success() throws Exception {
        // Given
        when(novelConvertService.convertNovelToScript(any(NovelConvertCommandEntity.class)))
                .thenReturn(mockResultVO);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/novel2script/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"test_user\",\"novelTitle\":\"测试小说\",\"novelContent\":\"这是一段测试内容。\",\"convertStyle\":\"现代都市剧\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.scriptContent").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sceneCount").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.characterList[0]").value("李明"));

        // Verify
        verify(novelConvertService, times(1)).convertNovelToScript(any(NovelConvertCommandEntity.class));
    }

    /**
     * 测试2：文件上传转换 - TXT文件成功
     */
    @Test
    public void test_convertNovelFile_Success() throws Exception {
        // Given
        when(novelConvertService.convertNovelToScript(any(NovelConvertCommandEntity.class)))
                .thenReturn(mockResultVO);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "这是测试文件内容。".getBytes(StandardCharsets.UTF_8)
        );

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/novel2script/convert/file")
                        .file(file)
                        .param("userId", "test_user")
                        .param("title", "测试文件")
                        .param("style", "现代都市剧"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.scriptContent").exists());

        // Verify
        ArgumentCaptor<NovelConvertCommandEntity> captor = ArgumentCaptor.forClass(NovelConvertCommandEntity.class);
        verify(novelConvertService, times(1)).convertNovelToScript(captor.capture());

        NovelConvertCommandEntity captured = captor.getValue();
        assertEquals("test_user", captured.getUserId());
        assertEquals("测试文件", captured.getNovelTitle());
        assertEquals("现代都市剧", captured.getConvertStyle());
        assertTrue(captured.getNovelContent().contains("测试文件内容"));
    }

    /**
     * 测试3：文件上传转换 - 使用默认userId
     */
    @Test
    public void test_convertNovelFile_DefaultUserId() throws Exception {
        // Given
        when(novelConvertService.convertNovelToScript(any(NovelConvertCommandEntity.class)))
                .thenReturn(mockResultVO);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "story.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "故事内容".getBytes(StandardCharsets.UTF_8)
        );

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/novel2script/convert/file")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify
        ArgumentCaptor<NovelConvertCommandEntity> captor = ArgumentCaptor.forClass(NovelConvertCommandEntity.class);
        verify(novelConvertService, times(1)).convertNovelToScript(captor.capture());

        assertEquals("default_user", captor.getValue().getUserId());
    }

    /**
     * 测试4：文件上传转换 - 自动从文件名提取标题
     */
    @Test
    public void test_convertNovelFile_AutoTitleFromFileName() throws Exception {
        // Given
        when(novelConvertService.convertNovelToScript(any(NovelConvertCommandEntity.class)))
                .thenReturn(mockResultVO);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "我的小说.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "内容".getBytes(StandardCharsets.UTF_8)
        );

        // When
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/novel2script/convert/file")
                        .file(file)
                        .param("userId", "user1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Then
        ArgumentCaptor<NovelConvertCommandEntity> captor = ArgumentCaptor.forClass(NovelConvertCommandEntity.class);
        verify(novelConvertService, times(1)).convertNovelToScript(captor.capture());

        assertEquals("我的小说", captor.getValue().getNovelTitle());
    }

    /**
     * 测试5：文本转换 - 内容为空应该返回错误
     */
    @Test
    public void test_convertNovel_EmptyContent_ShouldThrowException() throws Exception {
        // Given
        NovelConvertRequest emptyRequest = NovelConvertRequest.builder()
                .userId("test_user")
                .novelContent("")
                .build();

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/novel2script/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"test_user\",\"novelContent\":\"\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.ILLEGAL_PARAMETER.getCode()));

        // Verify
        verify(novelConvertService, never()).convertNovelToScript(any());
    }

    /**
     * 测试6：文本转换 - 内容为null应该返回错误
     */
    @Test
    public void test_convertNovel_NullContent_ShouldThrowException() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/novel2script/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"test_user\",\"novelContent\":null}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Verify
        verify(novelConvertService, never()).convertNovelToScript(any());
    }

    /**
     * 测试7：文件上传 - 空文件应该返回错误
     */
    @Test
    public void test_convertNovelFile_EmptyFile_ShouldThrowException() throws Exception {
        // Given
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/novel2script/convert/file")
                        .file(emptyFile))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.ILLEGAL_PARAMETER.getCode()));

        // Verify
        verify(novelConvertService, never()).convertNovelToScript(any());
    }

    /**
     * 测试8：文件上传 - 非TXT格式应该返回错误
     */
    @Test
    public void test_convertNovelFile_NonTxtFile_ShouldThrowException() throws Exception {
        // Given
        MockMultipartFile pdfFile = new MockMultipartFile(
                "file",
                "document.pdf",
                "application/pdf",
                "PDF内容".getBytes(StandardCharsets.UTF_8)
        );

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/novel2script/convert/file")
                        .file(pdfFile))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("仅支持TXT格式文件"));

        // Verify
        verify(novelConvertService, never()).convertNovelToScript(any());
    }

    /**
     * 测试9：文件上传 - 文件内容为空（只有空白字符）应该返回错误
     */
    @Test
    public void test_convertNovelFile_BlankContent_ShouldThrowException() throws Exception {
        // Given
        MockMultipartFile blankFile = new MockMultipartFile(
                "file",
                "blank.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "   \n\n  ".getBytes(StandardCharsets.UTF_8)
        );

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/novel2script/convert/file")
                        .file(blankFile))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.ILLEGAL_PARAMETER.getCode()));

        // Verify
        verify(novelConvertService, never()).convertNovelToScript(any());
    }

    /**
     * 测试10：服务层抛出异常时的处理
     */
    @Test
    public void test_convertNovel_ServiceException_ShouldHandleGracefully() throws Exception {
        // Given
        when(novelConvertService.convertNovelToScript(any(NovelConvertCommandEntity.class)))
                .thenThrow(new RuntimeException("AI服务异常"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/novel2script/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"test_user\",\"novelContent\":\"测试内容\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ResponseCode.UN_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("小说转换失败: AI服务异常"));

        // Verify
        verify(novelConvertService, times(1)).convertNovelToScript(any());
    }

    /**
     * 测试11：验证传递给服务层的命令对象包含正确的数据
     */
    @Test
    public void test_convertNovel_VerifyCommandEntity() throws Exception {
        // Given
        when(novelConvertService.convertNovelToScript(any(NovelConvertCommandEntity.class)))
                .thenReturn(mockResultVO);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/novel2script/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user123\",\"novelTitle\":\"标题\",\"novelContent\":\"内容\",\"convertStyle\":\"古装剧\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Then
        ArgumentCaptor<NovelConvertCommandEntity> captor = ArgumentCaptor.forClass(NovelConvertCommandEntity.class);
        verify(novelConvertService, times(1)).convertNovelToScript(captor.capture());

        NovelConvertCommandEntity captured = captor.getValue();
        assertEquals("user123", captured.getUserId());
        assertEquals("标题", captured.getNovelTitle());
        assertEquals("内容", captured.getNovelContent());
        assertEquals("古装剧", captured.getConvertStyle());
    }

    /**
     * 测试12：长文本转换
     */
    @Test
    public void test_convertNovel_LongContent() throws Exception {
        // Given
        String longContent = "这是一个很长的测试内容。".repeat(100);
        when(novelConvertService.convertNovelToScript(any(NovelConvertCommandEntity.class)))
                .thenReturn(mockResultVO);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/novel2script/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"test_user\",\"novelContent\":\"" + longContent + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify
        ArgumentCaptor<NovelConvertCommandEntity> captor = ArgumentCaptor.forClass(NovelConvertCommandEntity.class);
        verify(novelConvertService, times(1)).convertNovelToScript(captor.capture());

        assertTrue(captor.getValue().getNovelContent().length() > 1000);
    }

    /**
     * 测试13：文件上传 - 中文文件名
     */
    @Test
    public void test_convertNovelFile_ChineseFileName() throws Exception {
        // Given
        when(novelConvertService.convertNovelToScript(any(NovelConvertCommandEntity.class)))
                .thenReturn(mockResultVO);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "中文文件名.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "中文内容".getBytes(StandardCharsets.UTF_8)
        );

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/novel2script/convert/file")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify
        ArgumentCaptor<NovelConvertCommandEntity> captor = ArgumentCaptor.forClass(NovelConvertCommandEntity.class);
        verify(novelConvertService, times(1)).convertNovelToScript(captor.capture());

        assertEquals("中文文件名", captor.getValue().getNovelTitle());
    }

    /**
     * 测试14：响应数据完整性验证
     */
    @Test
    public void test_convertNovel_ResponseCompleteness() throws Exception {
        // Given
        when(novelConvertService.convertNovelToScript(any(NovelConvertCommandEntity.class)))
                .thenReturn(mockResultVO);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/novel2script/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"test\",\"novelContent\":\"test\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.scriptContent").value(mockResultVO.getScriptContent()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.analysisResult").value(mockResultVO.getAnalysisResult()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sceneCount").value(mockResultVO.getSceneCount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.characterList.length()").value(1));
    }

    /**
     * 测试15：多次调用验证服务没有被重复调用
     */
    @Test
    public void test_convertNovel_NoDuplicateCalls() throws Exception {
        // Given
        when(novelConvertService.convertNovelToScript(any(NovelConvertCommandEntity.class)))
                .thenReturn(mockResultVO);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/novel2script/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"test\",\"novelContent\":\"test\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Then - 确保只调用一次
        verify(novelConvertService, times(1)).convertNovelToScript(any(NovelConvertCommandEntity.class));
    }
}