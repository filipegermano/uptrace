package br.com.uppersystems.uptrace.configuration;

import br.com.uppersystems.uptrace.trace.UpAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogggingConfig {

    @Bean
    public Logger logPier() {

        //if (pierProperties.getSplunk().getLogs().isEnabled()) {

            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger logger = (Logger) LoggerFactory.getLogger("UP");
            LogstashTcpSocketAppender appender = new net.logstash.logback.appender.LogstashTcpSocketAppender();

            appender.setName("UP");
            appender.setContext(lc);
          //  appender.addDestination(pierProperties.getSplunk().getLogs().getHost() + ":" + pierProperties.getSplunk().getLogs().getPort().toString());
           // appender.setWriteBufferSize(pierProperties.getLogstash().getWriteBufferSize());
            LogstashEncoder encoder = new LogstashEncoder();
            encoder.setIncludeCallerData(true);
            appender.setEncoder(encoder);
            appender.start();

            logger.addAppender(appender);
        //}
        return null;
    }

    @Bean
    public Logger logPierBackground() {

        //if (pierProperties.getSplunk().getLogs().isEnabled()) {

            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger logger = (Logger) LoggerFactory.getLogger("UP_BACKGROUND");
            LogstashTcpSocketAppender appender = new net.logstash.logback.appender.LogstashTcpSocketAppender();

            appender.setName("UP_BACKGROUND");
            appender.setContext(lc);
          //  appender.addDestination(pierProperties.getSplunk().getLogs().getHost() + ":" + pierProperties.getSplunk().getLogs().getPort().toString());
           // appender.setWriteBufferSize(pierProperties.getLogstash().getWriteBufferSize());
            LogstashEncoder encoder = new LogstashEncoder();
            encoder.setIncludeCallerData(true);
            appender.setEncoder(encoder);
            appender.start();

            logger.addAppender(appender);
        //}
        return null;
    }

    @Bean
    public Logger logRoot() {

        //if (pierProperties.getSplunk().getLogs().isEnabled()) {

            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

            logger.setAdditive(false);
            Appender<ILoggingEvent> appender = new UpAppender();
            appender.setContext(lc);
            appender.start();
            logger.addAppender(appender);
        //}
        return null;
    }
}
