package ivan.test.service.impl;

import ivan.annotation.Autowired;
import ivan.annotation.Component;
import ivan.annotation.PostConstructor;
import ivan.annotation.Qualifier;
import ivan.test.service.AccountService;
import ivan.test.service.BackService;
import ivan.test.service.LoggerService;

@Component
public class BackServiceImpl implements BackService {
    private AccountService accountService;
    private LoggerService loggerService;

    public BackServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    public BackServiceImpl(@Qualifier("LoggerServiceImpl") LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    @PostConstructor
    public void testPostConstruct() {
        loggerService.sayMessage("PostConstructor method was called");
    }
}
