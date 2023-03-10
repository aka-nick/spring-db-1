package hello.jdbc.repository;

import hello.jdbc.domain.Member;

/**
 * 기존의 리포지토리 클래스는 SQLException이라는 jdbc 기술에 의해 종속되어있었다.
 * 그렇기 때문에 인터페이스로 추상화를 시도해도, 특정 예외클래스로 인해 인터페이스까지도 종속될 수밖에 없었다.
 * 그러나 체크 예외를 언체크예외로 감싸기로 하면, 인터페이스로 추상화가 가능하다.
 * 현 인터페이스는 그 변화에 따른 이점이라고 볼 수 있다.
 */
public interface MemberRepositoryEx {

    Member save(Member member) throws Exception;
    Member select(String memberId) throws Exception;
    void update(String memberId, int money) throws Exception;
    void delete(String memberId) throws Exception;
}
