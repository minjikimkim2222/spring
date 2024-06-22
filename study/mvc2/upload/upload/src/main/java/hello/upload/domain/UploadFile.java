package hello.upload.domain;

import lombok.Data;

@Data
public class UploadFile {
    private String uploadFileName; // 고객이 업로드한 파일명
    private String storeFileName; // 서버 내부에서 관리하는 파일명

    /*
        고객이 업로드한 파일명 그대로 서버 내부에서 관리하면 안된다.
        왜냐면, 서로 다른 고객이, 같은 파일이름을 업로드할 경우, 기존 파일이름과 충돌이 날 수 있기 때문!!

        보통 storeFileName은 안 겹치게 UUID 사용
     */

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
