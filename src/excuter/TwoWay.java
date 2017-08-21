package excuter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 *
 */
public class TwoWay {
    public static void main(String... args) {
        String name = "test";
        String password = "test";
        UserValidator ldapValidator = new UserValidator("LDAP");
        UserValidator dbValidator = new UserValidator("DataBase");
        TaskValidator ldapTask = new TaskValidator(ldapValidator, name, password);
        TaskValidator dbTask = new TaskValidator(dbValidator, name, password);
        List<TaskValidator> validatorList = new ArrayList<>();
        validatorList.add(ldapTask);
        validatorList.add(dbTask);
        ExecutorService executorService = Executors.newCachedThreadPool();
        String result = null;
        try {
            result = executorService.invokeAny(validatorList);
            System.out.println("结果为 " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }
}


class UserValidator {

    private String name;

    UserValidator(String name) {
        this.name = name;
    }

    /**
     * 模拟用户验证
     *
     * @param name
     * @param password
     * @return
     */
    boolean validate(String name, String password) {
        Random random = new Random();
        try {
            long time = (long) (Math.random() * 10);
            System.out.printf("validate %s : time %d\n", name, time);
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            return false;
        }
        return random.nextBoolean();
    }

    String getName() {
        return name;
    }
}

class TaskValidator implements Callable<String> {

    private UserValidator validator;

    private String user;
    private String password;

    TaskValidator(UserValidator validator, String user, String password) {
        this.validator = validator;
        this.user = user;
        this.password = password;
    }

    @Override
    public String call() throws Exception {
        if (!validator.validate(user, password)) {
            System.out.printf("%s 用户找不到\n", validator.getName());
            throw new Exception("用户验证失败");
        }
        System.out.printf("%s 找到用户\n", validator.getName());
        return validator.getName();
    }
}