package hello.upload.controller;

import hello.upload.domain.Item;
import hello.upload.domain.ItemRepository;
import hello.upload.domain.UploadFile;
import hello.upload.file.FileStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    // 상품 등록 폼을 보여준다.
    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form){
        return "item-form";
    }


    //    폼의 데이터를 저장하고, 보여주는 화면으로 redirect한다.
    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {
        // item-form에서 사용자로부터 파일 1개, 파일 여러개를 저장하고 + UploadFile 형태의 attachFile, imageFiles로 반환
        // Item 엔디티의 필드명과 동일
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles());

        // 데이터베이스에 저장
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);
        /*
            참고로 보통 파일 '자체'는 AWS - S3 같은 곳에 저장하고..
            '데이터베이스'에는, item 엔디티의 (UploadFile - 고객이 저장한 파일명 / 서버가 저장한 파일명; '파일경로') 같이,
            파일 관련 부수적인 정보들이 저장됨.
         */
        log.info(">>>>>>>>>>>> Item : {}", item);

        redirectAttributes.addAttribute("itemId", item.getId());

        return "redirect:/items/{itemId}";
    }

    // 상품을 보여준다.
    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model){
        Item item = itemRepository.findById(id);
        model.addAttribute("item", item);
        return "item-view";
    }

    // 이미지를 보여줄 컨트롤러
    /*
        <img> 태그로 이미지를 조회할 때 사용한다.
        UrlResource로 이미지 파일을 읽어서, @ResponseBody로 이미지 바이너리 반환
     */
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        // "file:/Users/../a90ce674-66d3-4c65-b38b-53487493aef8.js"
        return new UrlResource("file:" + fileStore.getFullPath(filename));
        // 해당 경로의 이미지 파일을 찾아서 업로드해줌 !!
    }

    // 첨부파일 링크를 클릭하면, 파일을 다운시켜줄 컨트롤러
    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {

        Item item = itemRepository.findById(itemId);
        String storeFileName = item.getAttachFile().getStoreFileName(); // -- 서버 내부에서 관리하는 파일명
        String uploadFileName = item.getAttachFile().getUploadFileName(); // -- 고객이 업로드한 파일명

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName : {}", uploadFileName);

        // 한글이 깨져서 넣어줌
        String encodedUploadFileName = UriUtils.encode(uploadFileName,
                StandardCharsets.UTF_8);

        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);

    }



}
