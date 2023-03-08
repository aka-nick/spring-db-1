package hello.jdbc.repository;

import static org.junit.jupiter.api.Assertions.*;

import hello.jdbc.domain.Member;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

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

    }

}