package meta;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Слушатель основных тестов (не для юнит-тестов!), обеспечивает запись в лог по событиям в тестах
 */
public class MainListener extends TestListenerAdapter {


    @Override
    public void onTestStart(ITestResult iTestResult) {
        super.onTestStart(iTestResult);
        logMessage("info", iTestResult, "started.");
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        super.onTestSkipped(iTestResult);
        logMessage("warn", iTestResult, "skipped!");
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        super.onTestFailure(iTestResult);
        logMessage("error", iTestResult, "failed!");
        logMessage("error", iTestResult, String.join("", " message: ", iTestResult.getThrowable().getMessage()));
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        super.onTestSuccess(iTestResult);
        logMessage("info", iTestResult, "finished.");
    }

    /**
     * Получаем параметры теста для логирования
     *
     * @param iTestResult все параметры теста
     * @return строку, которая пустая в случае отсутствия параметров у теста или содержит все параметры
     */
    private String getParams(ITestResult iTestResult) {
        StringBuilder builder = new StringBuilder("Params: ");
        for (Object parameter : iTestResult.getParameters()) {
            if (parameter instanceof Object[]) {
                builder.append(Arrays.deepToString((Object[]) parameter) + " ");
            } else {
                builder.append(parameter + " ");
            }
        }
        return builder.length() > "Params: ".length() ? builder.toString() : "";
    }

    /**
     * Выбирает нужный метод для логирования и формирует сообщение
     *
     * @param type    тип логирования (info, error, warn)
     * @param result  результат теста (для вытягивания параметров)
     * @param message сообщение для логирования
     */
    private void logMessage(String type, ITestResult result, String message) {
        Consumer<String> typeConsumer = TestFlow.logger::info;
        if (type.equals("warn")) {
            typeConsumer = TestFlow.logger::warn;
        } else if (type.equals("error")) {
            typeConsumer = TestFlow.logger::error;
        }
        typeConsumer.accept(String.join("", result.getName(), "(", getParams(result), ") ", message));
    }
}
