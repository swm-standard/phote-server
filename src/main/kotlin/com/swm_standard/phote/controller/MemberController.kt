package com.swm_standard.phote.controller

import com.swm_standard.phote.common.resolver.memberId.MemberId
import com.swm_standard.phote.common.responsebody.BaseResponse
import com.swm_standard.phote.dto.DeleteMemberResponse
import com.swm_standard.phote.service.MemberService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api")
class MemberController(
    private val memberService: MemberService,
) {
    @DeleteMapping("/member")
    fun deleteMember(
        @MemberId memberId: UUID,
    ): BaseResponse<DeleteMemberResponse> {
        val uuid = memberService.deleteMember(memberId)

        return BaseResponse(msg = "멤버 탈퇴 성공", data = DeleteMemberResponse(uuid))
    }
}
