package ivan.test.service.impl;

import ivan.annotation.Autowired;
import ivan.annotation.Component;
import ivan.test.service.LoggerService;
import ivan.test.service.SendMessageService;

@Component
public class LoggerServiceImpl2 implements LoggerService {
    private final SendMessageService sendMessageService;

    @Autowired
    public LoggerServiceImpl2(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public String sayHello() {
        return "Hello Logger 2";
    }

    @Override
    public void sayMessage(String message) {
        System.out.println(message);
    }
}
