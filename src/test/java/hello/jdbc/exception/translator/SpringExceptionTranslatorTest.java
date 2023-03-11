package hello.jdbc.exception.translator;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

import hello.jdbc.connection.ConnectionConst;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

@Slf4j
public class SpringExceptionTranslatorTest {

    DataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

    }

    @Test
    void sqlExceptionCode() {
        String sql = "select bad grammar";
        try {
            Connection con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeQuery();
        }
        catch (SQLException e) { // 구식의 예외 래핑 : 일일히 에러코드를 확인하는 어려움이 있다
            int errorCode = e.getErrorCode();
            assertThat(errorCode).isEqualTo(42122);
            log.info("errorCode={}", errorCode);
            log.info("error", e);
        }
    }

    @Test
    void exceptionTranslator() {
        String sql = "select bad grammar";
        try {
            Connection con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeQuery();
        }
        catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(42122);
            // 스프링이 제공하는 예외변환기
            SQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);

            // 스프링이 제공하는 변환예외 인터페이스
            DataAccessException resultEx = translator.translate("select", sql, e);
            log.info("resultEx", resultEx);

            // 기대되는 스프링 예외인터페이스 구현체 : 잘못된 문법 예외(BadSqlGrammarException)
            assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);

            /*
            이런 기술을 사용하게 되면 스프링에 대한 종속성이 생기게 된다.
            하지만 예외추상화까지 상세히 다뤄주는 다른 기술이 쓸만한 수준에서는 없다고 보면 되기 때문에
            스프링을 사용함으로 인한 생산성과 종속성의 트레이드오프를 괜찮은 선택지로 고려할 수 있다.
            */
        }
    }

}
