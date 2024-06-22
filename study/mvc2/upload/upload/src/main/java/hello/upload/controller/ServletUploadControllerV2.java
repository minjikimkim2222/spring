package hello.upload.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {

    @Value("${file.dir}") // application.properties에서 설정한 속성값을 가져오자
    private String fileDir;
    @GetMapping("/upload")
    public String newFile(){
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV1(HttpServletRequest request) throws ServletException, IOException {
        log.info("request : {}", request);

        String itemName = request.getParameter("itemName");
        log.info("itemName : {}", itemName);

        Collection<Part> parts = request.getParts();
        log.info("parts : {}", parts);

        for (Part part : parts) {
            log.info("==== PART =====");

            // PART name 출력
            log.info("name : {}", part.getName());

            // PART의 각각의 헤더name - 헤더value 출력
            Collection<String> headerNames = part.getHeaderNames();
            for (String headerName : headerNames) {
                log.info("header {} : {}", headerName,
                        part.getHeader(headerName));
            }

            // 편의메서드
            // content-Disposition ; filename
            log.info("submittedFileName : {}", part.getSubmittedFileName()); // -- cat.jpg (업로드한 파일명)
            log.info("size : {}", part.getSize()); // part body size

            // 데이터 읽기 -- 파일 body 내용 읽어오기
            InputStream inputStream = part.getInputStream();
            // 파일 body부분이 바이너리 내용이니까, 바이너리 -> String으로 형변환
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            log.info("body : {}", body);

            // 파일에 저장하기
            if (StringUtils.hasText(part.getSubmittedFileName())){ // -- 실제 파일명이 있는가!
                String fullPath = fileDir + part.getSubmittedFileName();
                log.info("파일 저장 fullPath : {}", fullPath);
                part.write(fullPath);
            }

        }
        return "upload-form";
    }
}
