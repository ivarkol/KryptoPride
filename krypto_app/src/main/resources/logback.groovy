import ch.qos.logback.core.util.FileSize

def logPattern = "%d{yyyy-MM-dd'T'HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
def BASE_PATH = "../kpride/logs"

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = logPattern
    }
}

appender("FILE", RollingFileAppender) {
    file = "${BASE_PATH}/kpride.log"
    encoder(PatternLayoutEncoder) {
        pattern = logPattern
    }
    rollingPolicy(FixedWindowRollingPolicy) {
        fileNamePattern = "${BASE_PATH}/kpride_%i.log"
        minIndex = 1
        maxIndex = 10
    }
    triggeringPolicy(SizeBasedTriggeringPolicy) {
        maxFileSize = FileSize.valueOf("20MB")
    }

}

root(DEBUG, ["CONSOLE"])