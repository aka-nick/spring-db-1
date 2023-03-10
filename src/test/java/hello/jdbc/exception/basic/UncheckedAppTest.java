package hello.jdbc.exception.basic;

import java.net.ConnectException;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedAppTest {


    /**
     * 언체크(런타임)예외는 해당 객체가 처리할 수 없는 예외는 무시한다. 따라서 체크 예외처럼 강제로 의존할 필요가 없다.
     *      기술을 바꿔도 파급효과가 적다는 의미가 된다. 예외가 처리되는 해당 부분만 변경하면 되기 때문에 변경 영향 범위가 최소화된다.
     *      필요한 경우에는 런타임 예외를 잡아서 처리하고, 아니면 알아서 던지게 둔다.
     * 그리고 공통으로 처리하는 부분에서(컨트롤러, Advice 등) 처리하면 된다.
     *
     * 추가로, 런타임 예외는 놓칠 수 있기 때문에 문서화가 중요하다.
     *      또는, 남기지 않을 수 있는거지 남기면 안되는 것은 아니기 때문에, 명시적으로 남겨도 되긴 된다.
     */
    @Test
    void unchecked() {
        Controller c = new Controller();
        Assertions.assertThatThrownBy(() -> c.request())
                .isInstanceOf(RuntimeException.class);
    }


    /**
     * 예외를 전환할 때는 꼭!!! 기존 예외를 포함해야 한다!!
     * 그렇지 않으면 스택트레이스를 확인할 때 심각한 문제가 발생할 수 있다. 진짜 문제가 발생한 위치를 찾을 수가 없게된다.
     */
    @Test
    void printEx() {
        Controller c = new Controller();
        try {
            c.request();

        }
        catch (Exception e) {
//            e.printStackTrace(); // 좋지 않은 방법(System.out을 통하게 된다). 로그를 남기자.
            log.info("예외 발생", e); // 이렇게.
        }
    }


    static class Controller {

        Service s = new Service();

        public void request() {
            s.logic();
        }
    }

    static class Service {

        Repository r = new Repository();
        NetworkClient n = new NetworkClient();

        public void logic() {
            r.call();
            n.call();
        }
    }

    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
//                throw new RuntimeSQLException(e);
                throw new RuntimeSQLException(); // 잘못된 예외 먹어버리기.
                // 이렇게 하면 실제 발생한 예외 지점/원인을 확인할 수 없게 되는 치명적인 오류가 된다.
            }
        }
        public void runSQL() throws SQLException {
            throw new SQLException("쿼리 실패");
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String msg) {
            super(msg);
        }
    }
    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }

        public RuntimeSQLException() {
            // 기존 익셉션 내용은 여기서 먹어버림.
        }
    }
}
