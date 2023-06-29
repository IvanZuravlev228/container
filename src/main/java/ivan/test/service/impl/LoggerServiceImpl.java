package ivan.test.service.impl;

import ivan.annotation.Component;
import ivan.test.service.LoggerService;

@Component
public class LoggerServiceImpl implements LoggerService {
    @Override
    public String sayHello() {
        return "Hello Logger 1";
    }

    @Override
    public void sayMessage(String message) {
        System.out.println(message);
    }
}
