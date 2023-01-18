package ui;


import meta.TestFlow;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class BaseTest extends TestFlow {

    protected WebDriver driver;
    protected Browser browser;
    protected String downloadFilepath = System.getProperty("user.dir") + "\\download\\";
    protected String url_home = "http://any.url/";
    protected String SERT_NAME = "Иванов И.И.";
    protected String LOGIN_NAME = "login";
    protected String PASS = "Qwerty1";
    protected long startTime = 0;
    protected long endTime = 0;


    public WebDriver getDriver() {
        return this.driver;
    }


    @BeforeMethod(groups = {"fast", "all"})
    public void setUp(Method method) throws InterruptedException, IOException {
        System.out.println("Прогон теста: " + method.getDeclaringClass() + "." + method.getName());
        startTime = System.currentTimeMillis();

        String filePath = System.getProperty("user.dir") + "\\download\\";
        Path pathMain = Paths.get(filePath);
        if (!Files.exists(pathMain)) {
            final File file = new File(filePath);
            file.mkdir();
        }
        //отдельная папка для каждого потока
        downloadFilepath = System.getProperty("user.dir") + "\\download\\" + Thread.currentThread().getId() + "\\";
        Path path = Paths.get(downloadFilepath);
        if (!Files.exists(path)) {
            final File file = new File(downloadFilepath);
            file.mkdir();
        }

        url_home = System.getenv("TEST_URL");
        if (url_home == null) {
            url_home = "http://google.com/";
        }

        ChromeOptions options = new ChromeOptions();
        //загрузка файлов не работает в режиме Headless
        //options.setHeadless(true);
        HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("profile.default_content_settings.popups", 0);
            params.put("download.default_directory", downloadFilepath);
            //безопасная загрузка файлов
            params.put("safebrowsing.enabled", "true");
            // отключает открытие предпросмотра при попытке скачать pdf файл и сразу же качает его
            params.put("plugins.always_open_pdf_externally", true);
            //options.setExperimentalOption("prefs", params);
            //options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
            options.addExtensions(new File("CryptoPro Extension for CAdES Browser Plug-in.crx"));
            //options.addArguments("--no-sandbox");
            driver = new ChromeDriver(options);
            browser = new Browser(driver);
            browser.maximize();
    }

    //чтение из файла
    protected String getFileText(String name) {
        try {
            return Files.newBufferedReader(Paths.get(name)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "000000";
        }
    }

    //запись в файл
    protected boolean setFileText(String name, String text) {
        try {
            List<String> lines = Arrays.asList(text);
            Files.write(Paths.get(name), lines, Charset.forName("UTF-8"));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //возвращает строку с текущей датой + n дней
    protected String getDateCurrentPlusDays(int days) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);
        Date date = cal.getTime();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy");
        return formatForDateNow.format(date);
    }

    //возвращает строку  - mmss + milisec (генератор "случайных чисел")
    protected String getMinSecCurrentPlusDay() {
        Calendar cal = GregorianCalendar.getInstance();
        Date dateT = cal.getTime();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("SSS");
        String mili = formatForDateNow.format(dateT);
        Date date = cal.getTime();
        SimpleDateFormat formatForMinNow = new SimpleDateFormat("mmss");
        return String.valueOf(Integer.valueOf(formatForMinNow.format(date)) + Integer.valueOf(mili));
    }

    @AfterMethod(groups = {"fast", "all"}, alwaysRun = true)
    public void tearDown(Method method, ITestResult result) throws IOException {
        if (browser != null) {
            browser.quit();
            endTime = System.currentTimeMillis();
            long timeSec = (endTime - startTime) / 1000;
            String allTime = (int) timeSec / 60 + ":" + timeSec % 60;
            System.out.println("Время выполнения теста: " + allTime);
        }
    }

}
