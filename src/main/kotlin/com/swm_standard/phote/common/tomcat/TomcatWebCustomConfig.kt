package com.swm_standard.phote.common.tomcat

import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Configuration

@Configuration
class TomcatWebCustomConfig : WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    override fun customize(factory: TomcatServletWebServerFactory) {
        factory.addConnectorCustomizers(
            TomcatConnectorCustomizer { connector ->
                connector.setProperty(
                    "relaxedQueryChars",
                    "<>[\\]^`{|}",
                )
            },
        )
    }
}
