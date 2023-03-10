package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;

public interface MemberRepository {

    /**
     * @throws MyDbException 체크 -> 언체크 래핑 예외 클래스
     */
    Member save(Member member);

    /**
     * @throws MyDbException 체크 -> 언체크 래핑 예외 클래스
     */
    Member select(String memberId);

    /**
     * @throws MyDbException 체크 -> 언체크 래핑 예외 클래스
     */
    void update(String memberId, int money);

    /**
     * @throws MyDbException 체크 -> 언체크 래핑 예외 클래스
     */
    void delete(String memberId);
}
