package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /*
        등록 V1 : 요청 값으로 Member 엔디티를 직접 받는다.

        - 문제점
            1) 엔디티에 프레젠테이션 계층을 위한 로직이 추가됨
                - 엔디티에 API 검증을 위한 로직이 추가된다. (@NotEmpty 등등)
                - 실무에서는 회원 엔디티를 위한 API가 다양하게 만들어지는데, 한 엔디티에 각각의 API를 위한
                  모든 요청 요구사항들을 담기는 어렵다.
            2) 엔디티가 변경되면, API 스펙이 변한다.

         - 결론
            - API 요청 스펙이 맞추어, 별도의 DTO를 파라미터로 받는다.
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        // RequestBody는 json으로 넘긴 값들을 매핑된 Member 에 다 매핑시켜줌 !!
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id){
            this.id = id;
        }
    }

    /*
        등록 V2 : 요청값으로 Member 엔디티 대신에, 별도의 DTO를 받는다.
     */

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberRequest {

        @NotEmpty
        private String name;
    }

    /*
        수정 API -- name만 바꾸는 수정 api
        --> 역시나 변경감지로 처리 !
     */
    @PatchMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
           @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    /*
        회원 조회 V1 : 응답값으로 엔디티를 직접 외부에 노출

        - 문제점
            1) 엔디티에 프레젠테이션 계층을 위한 로직이 추가된다.
                - 기본적으로 엔디티의 모든 값이 노출된다.
                - 응답스펙을 맞추기 위한 로직이 추가된다. (@JsonIgnore, 별도의 뷰 로직 등등)
                - 실무에서는 같은 엔디티에 대해 API가 용도에 따라 다양하게 만들어지는데,
                  한 엔디티에 각각의 API를 위한 프레젠테이션 응답 로직을 담기는 어렵다.

            2) 엔디티가 변경되면, API 스펙이 변한다.

            3) 추가로 컬렉션을 직접 반환하면, 향후 API 스펙을 변경하기 어렵다.
              (별도의 Result 클래스 생성으로 해결)

        - 결론
            -> API 응답 스펙이 맞추어, 별도의 DTO를 반환한ㄷ ㅏ !!
     */
     @GetMapping("/api/v1/members")
     public List<Member> membersV1(){
         return memberService.findMembers();
     }

     /*
        회원조회 V2 : 응답 값으로 엔디티가 아닌, 별도의 DTO 사용
      */
    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();

        // 엔디티 -> DTO 변환
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> { // 템플릿으로 만듭니다.
        // private int count; -- Result로 한번 더 감싸서 내보내기에, 코드 유연성 증가 !!!
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

}
