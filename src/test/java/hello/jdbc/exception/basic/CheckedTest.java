package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest {

    @Test
    void checkedCatch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checkedThrow() {
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }

    /*
    Exception을 상속하면 checked 예외가 된다
     */
    static class MyCheckedException extends Exception {
          public MyCheckedException(String msg) {
              super(msg);
          }
    }

    /**
     * Checked 예외는 예외를 잡아서 처리하든 던지든 둘 중 하나를 해야한다.
     */
    static class Service {

        Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리하는 코드
         */
        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                // 예외 처리 로직
                log.info("예외 처리, message={}", e.getMessage(), e);
            } catch (Exception e) {
                log.info("모든 체크/언체크 예외 처리", e);
            }
        }

        /**
         * 예외를 던지는 코드
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }
    static class Repository {
        public void call() throws MyCheckedException {
            throw new MyCheckedException("exception occurred");
        }
    }

}
