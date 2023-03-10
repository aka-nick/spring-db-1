package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    /**
     * 언체크 예외는
     * 예외를 잡거나, 냅두거나다.
     * 냅두면 알아서 던진다.
     */
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String msg) {
            super(msg);
        }
    }

    static class Repository {
        public void call() {
            throw new MyUncheckedException("Exception occurred");
        }
    }

    static class Service {
        public void call() {
            Repository repository = new Repository();
            repository.call(); //repository 안에서 예외가 발생하지만, 컴파일러는 아무 경고도 하지 않는다.
        }
    }

    @Test
    void uncheckedCatch() { // throws 구문을 시그니처에 추가 해도 된다.
                        // 근데 거의 생략을 한다. 예외의 의존관계를 불필요하게 참조할 일이 없다.
                        // 다만 타 개발자의 개발편의성을 위해서
                        //      (소스코드만 보고 예외가 발생할 수 있다는 것을 바로 파악하라고)
                        //      추가해놓는 경우도 있다.

        Service service = new Service();
        try {
            service.call();
        }
        catch (MyUncheckedException e) {
            log.info("언체크 예외 캐치 = {}", e.getMessage(), e);
        }
    }

    @Test
    void uncheckedThrow() {
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.call())
                .isInstanceOf(MyUncheckedException.class);
    }
}
