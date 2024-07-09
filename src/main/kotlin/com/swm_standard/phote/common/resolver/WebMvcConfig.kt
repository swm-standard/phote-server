package com.swm_standard.phote.common.resolver

import com.swm_standard.phote.common.resolver.memberId.MemberIdResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val memberIdResolver: MemberIdResolver
): WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(memberIdResolver)
    }
}