package ivan.test;

import ivan.annotation.Autowired;
import ivan.annotation.Component;
import ivan.test.service.AccountService;
import ivan.test.service.BackService;
import ivan.test.service.LoggerService;

@Component
public class ClientApp {
    private final BackService backService;
    private final AccountService accountService;
    private final LoggerService loggerService;

    @Autowired
    public ClientApp(AccountService accountService,
                     BackService backService, LoggerService loggerService) {
        this.accountService = accountService;
        this.backService = backService;
        this.loggerService = loggerService;
    }

    public void info() {
        System.out.println(accountService);
        System.out.println(backService);
        System.out.println(loggerService);
        System.out.println(loggerService.sayHello());
    }
}
