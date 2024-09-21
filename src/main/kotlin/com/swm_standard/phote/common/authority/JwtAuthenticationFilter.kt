package com.swm_standard.phote.common.authority

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : GenericFilterBean() {
    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse?,
        chain: FilterChain?,
    ) {
        val accessToken = resolveToken(request as HttpServletRequest)
        if (accessToken != null &&
            jwtTokenProvider.validateToken(accessToken) &&
            request.getHeader("userAgent")?.contains("ELB-HealthChecker/2.0") == false
        ) {
            val authentication = jwtTokenProvider.getAuthentication(accessToken)
            SecurityContextHolder.getContext().authentication = authentication
        }

        chain?.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("accessToken")

        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }
}
