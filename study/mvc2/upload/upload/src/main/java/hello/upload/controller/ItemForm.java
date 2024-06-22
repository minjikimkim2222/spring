package hello.upload.controller;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
    DTO - 상품저장용 폼

    엔디티(Item)과 다름.
        Item은 내가 정의한 UploadFile형이지만,
        ItemForm은 스프링이 제공하는 MulipartFile을 사용하고 있는중.
 */
@Data
public class ItemForm {
    private Long itemId;
    private String itemName;
    private List<MultipartFile> imageFiles; // -- 이미지 다중 업로드하기 위해, MultipartFile을 사용했다 !!
    private MultipartFile attachFile;

    // 참고로, MultipartFile은 @ModelAttribute에서 사용할 수 있다 !!
}
