package hello.jdbc.repository;

import static hello.jdbc.connection.ConnectionConst.PASSWORD;
import static hello.jdbc.connection.ConnectionConst.URL;
import static hello.jdbc.connection.ConnectionConst.USERNAME;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;
    @BeforeEach
    void before() {
//        DriverManagerDataSource ds = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
//        repository = new MemberRepositoryV1(ds);

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(URL);
        ds.setUsername(USERNAME);
        ds.setPassword(PASSWORD);
        repository = new MemberRepositoryV1(ds);
    }

    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV1", 10500);
        repository.save(member);
    }

    @Test
    void crud2() throws SQLException {
        String memberId = "memberV1";
        Member select = repository.select(memberId);
        log.info("member = {}", select);
        Assertions.assertThat(select.getMemberId()).isEqualTo(memberId);
        Assertions.assertThat(select.getMoney()).isEqualTo(10500);
    }

    @Test
    void crud3() throws SQLException {
        String memberId = "memberV1";
        int updateMoney = 30000;

        repository.update(memberId, updateMoney);
        Member select = repository.select(memberId);

        Assertions.assertThat(select.getMemberId()).isEqualTo(memberId);
        Assertions.assertThat(select.getMoney()).isEqualTo(updateMoney);
    }

    @Test
    void crud4() throws SQLException {
        String memberId = "dMember";
        int money = 5000;

        Member save = repository.save(new Member(memberId, money));
        Assertions.assertThat(save.getMemberId()).isEqualTo(memberId);
        Assertions.assertThat(save.getMoney()).isEqualTo(money);

        repository.delete(memberId);
        Assertions.assertThatThrownBy(() -> repository.select(memberId))
                .isInstanceOf(NoSuchElementException.class);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}