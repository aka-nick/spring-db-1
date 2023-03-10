package hello.jdbc.exception.basic;

import java.net.ConnectException;
import java.sql.SQLException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CheckedAppTest {


    @Test
    void checked() {
        Controller c = new Controller();
        Assertions.assertThatThrownBy(() -> c.request())
                .isInstanceOf(Exception.class);
    }

    static class Controller {

        Service s = new Service();

        /**
         * 처리할 수도 없는 체크 예외가 컨트롤러계층까지(작성한 소스코드 상에서는) 올라온다.
         *      이런 예외는 대부분이 복구가 불가능한 시스템 예외들이다.
         * 또한, 특정 기술 예외에 종속이 된다.
         *      예를 들어 SQLException은 java.sql 패키지, 즉 JDBC 기술이다.
         *      영속계층접근에 쓰이는 또다른 기술인 JPA에서는 같은 상황에 PersistenceException이 발생한다.
         *      JDBC -> JPA로 교체하는 상황이 벌어진다면 모든 시그니처를 수정해야 하는 번거로움이 발생할 것이다.
         * 그렇다고 체크 예외의 가장 상위인 Exception을 잡게 되면?
         *      당장은 해결되는 것처럼 보이겠지만
         *      모든 컴파일 오류를 전부 통과시키게 될 것이므로 더 큰 문제가 일어나는 셈이다. 이 방법은 매우 좋지 않다.
         * 그래서! 가능하면 언체크 예외 활용을 먼저 고려하는 편이 좋다!
         */
        public void request() throws SQLException, ConnectException {
            s.logic();
        }
    }

    static class Service {

        Repository r = new Repository();
        NetworkClient n = new NetworkClient();

        public void logic() throws ConnectException, SQLException {
            r.call();
            n.call();
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("쿼리 실패");
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("연결 실패");
        }
    }

}
