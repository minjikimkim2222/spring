package hello.upload.file;

import hello.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
    멀티파트파일'자체'를, '서버'에 저장하는 역할
    - storeFile : 첨부파일 '한개' 저장
    - storeFiles : 첨부파일 '여러개' 저장 - ex) 이미지파일들
    - createStoreFileName : 서버내부에서 관리하는 파일명은 유일한 이름이어야 한다. -- UUID 사용
    - extractExt : 확장자 별도 추출

    파일'자체' 저장 -> AWS의 S3 같은 곳..
    파일'경로' / 부수정보 저장 -> 데이터베이스
 */
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename){
        return fileDir + filename;
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles){
            if (!multipartFile.isEmpty()){
                storeFileResult.add(storeFile(multipartFile));
                // -- 멀티파일 '한개'를 저장하고, UploadFile로 바꿔주는, 내가 만든 함수임
            }
        }

        return storeFileResult;
    }

    // 멀티파트 파일을, 나한테, 진짜 파일로 저장한다.
    // 그다음에, UploadFile로 반환해준다.
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()){
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename(); // -- ex) image.png
        String storeFileName = createStoreFileName(originalFilename); // -- '서버'에 저장할 파일명 (특, 유일함)

        // 파일 저장 -- '내 로컬'에 서버에 저장할 파일명 저장
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        return new UploadFile(originalFilename, storeFileName);
    }

    private String createStoreFileName(String originalFilename){ // -- '서버'에 저장할 파일명
        String ext = extractExt(originalFilename); // ex) "png"

        // 서버에 저장하는 파일명 -- UUID 사용해야 함
        String uuid = UUID.randomUUID().toString(); // -- ex) "qwe-qew-123-3134-sf"

        return uuid + "." + ext; // "uuid.확장자" -- ex) "qwe-qew-123-3134-sf.png"
    }

    private String extractExt(String originalFilename){
        int pos = originalFilename.lastIndexOf(".");

        return originalFilename.substring(pos + 1); // -ex) "png"
    }
}
