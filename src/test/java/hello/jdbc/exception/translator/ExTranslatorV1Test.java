package hello.jdbc.exception.translator;

import static hello.jdbc.connection.ConnectionConst.*;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbDuplicateKeyException;
import hello.jdbc.repository.ex.MyDbException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

@Slf4j
public class ExTranslatorV1Test {

    Repository r;
    Service s;

    @BeforeEach
    void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        r = new Repository(dataSource);
        s = new Service(r);
    }

    @Test
    void duplicateSave() {
        s.create("MyId");//최초 시도 : 기대값 - 성공
        s.create("MyId");//재시도 : 기대값 - 새로운 아이디 제시
    }

    @RequiredArgsConstructor
    static class Service {

        private final Repository repository;
        void create(String memberId) {
            try {
                Member member = new Member(memberId, 0);
                repository.save(member);
                log.info("saveID = {}", memberId);
            } catch (MyDbDuplicateKeyException e) {
                /*
                리포지토리에 접근하는 코드의 catch 절에서
                리포지토리에서 올라온 '특별한' 예외에 대해 비즈니스예외처리로직을 적용할 수 있다
                 */
                log.info("키 중복, 복구 시도");

                String retryId = generateNewId(memberId);
                log.info("retryId = {}", retryId);
                repository.save(new Member(retryId, 0));
            } catch (MyDbException e) {
                log.info("데이터 접근계층 예외", e);
                throw e;
            }
        }
        private String generateNewId(String memberId) {
            return memberId + new Random().nextInt(10000);
        }
    }

    @RequiredArgsConstructor
    static class Repository {
        private final DataSource dataSource;

        public Member save(Member member) {
            String sql = "insert into member(member_id, money) values (?,?)";
            Connection con = null;
            PreparedStatement pstmt = null;

            try {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2, member.getMoney());
                pstmt.executeUpdate();
                return member;
            }
            catch (SQLException e) {
                /*
                 * 리포지토리에서 '비즈니스적으로 특별히 처리'하고 싶은 특정 예외에 대하여
                 * 에러코드를 확인하여 관련있는 새로운 예외(대개 커스텀한)로 포장하여 올려보낼 수 있다
                 * 그러면 리포지토리에 접근한 코드에서는 예외가 발생할 것에 대비해
                 * 해당 예외를 catch하고 '비즈니스적인 특별한 처리'를 하면 된다.
                 */
                //h2 db
                if (e.getErrorCode() == 23505) {
                    throw new MyDbDuplicateKeyException(e);
                }
                throw new MyDbException(e);
            }
            finally {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }
        }

    }

}
