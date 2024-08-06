package com.swm_standard.phote.common.resolver.memberId

import com.swm_standard.phote.common.authority.JwtTokenProvider
import com.swm_standard.phote.common.exception.BadRequestException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import java.util.*

@Component
class MemberIdResolver(
    private val jwtTokenProvider: JwtTokenProvider,
) : HandlerMethodArgumentResolver {
    // supportsParameter: 어떤 파라미터를 처리할 것인지
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        parameter.getParameterAnnotation(MemberId::class.java) ?: return false
        if (!parameter.parameterType.isAssignableFrom(UUID::class.java)) {
            return false
        }

        return true
    }

    // resolveArgument: 어떤 값을 만들 것인지
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        val request = webRequest.nativeRequest as HttpServletRequest

        val accessToken = request.getHeader("accessToken")

        if (accessToken == null || accessToken.isEmpty()) {
            throw BadRequestException("비어있는 accessToken")
        }

        // token validation 분기처리 필요할 듯
        if (jwtTokenProvider.validateToken(accessToken)) {
            throw BadRequestException("유효하지 않은 accessToken")
        }

        return jwtTokenProvider.getJwtContents(accessToken)
    }
}
