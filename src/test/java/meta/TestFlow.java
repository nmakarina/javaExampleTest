package meta;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import io.qameta.allure.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import java.io.File;


/**
 * Класс родитель для основных тестов, содержит все нужные пути, настройки логера,
 * обеспечивает начало и конец сьюта, записи в лог.
 */
public class TestFlow {
    // Путь к конфигу логера, там задаетс как и куда записывать сообщения
    private final String PATH_TO_LOGGER_CONFIG = System.getProperty("user.dir") + File.separator + "config.xml";
    public static Logger logger = LoggerFactory.getLogger("main_log");


    @BeforeSuite
    @Parameters({"critical"})
    public void startSuite() {
        //инициализация лога
        initLogger();
    }


    @AfterSuite
    public void stopSuite() {
        logger.info("SUITE FINISHED!");
    }

    /**
     * Пытаемся инициализировать логер с помощью конфига
     */
    private void initLogger() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(PATH_TO_LOGGER_CONFIG);
        } catch (JoranException je) {
            System.err.println("Cant initialize logger! \n" + je.getMessage());
            je.printStackTrace();
        }
    }

    @Attachment(value = "{1}", type = "text/plain")
    protected String saveTextLog (String message, String value){
        return message;
    }

}
