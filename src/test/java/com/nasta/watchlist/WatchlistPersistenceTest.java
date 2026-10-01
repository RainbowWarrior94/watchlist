package com.nasta.watchlist;
import com.nasta.watchlist.model.*;
import com.nasta.watchlist.model.enums.ItemStatus;
import com.nasta.watchlist.model.enums.MediaType;
import com.nasta.watchlist.model.enums.MemberRole;
import com.nasta.watchlist.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Весь контекст приложения + настоящий PostgreSQL.
// Если миграция и сущности расходятся (Flyway + ddl-auto: validate), тесты упадут ещё при старте.
// @Transactional: после каждого теста изменения откатываются, база остаётся чистой.
@SpringBootTest
@Import(TestcontainersConfig.class)
@Transactional
class WatchlistPersistenceTest {

    @Autowired UserRepository users;
    @Autowired WatchlistRepository watchlists;
    @Autowired WatchlistMemberRepository members;
    @Autowired TitleRepository titles;
    @Autowired WatchlistItemRepository items;
    @Autowired EntityManager em;

    private User newUser(String name) {
        User u = new User();
        u.setEmail(name + "@test.com");
        u.setUsername(name);
        u.setPasswordHash("hash");
        return users.save(u);
    }

    private Title newTitle(int tmdbId, String name) {
        Title t = new Title();
        t.setTmdbId(tmdbId);
        t.setMediaType(MediaType.MOVIE);
        t.setTitle(name);
        return titles.save(t);
    }

    @Test
    void creatorBecomesOwnerAndSeesTheList() {
        User anna = newUser("anna");
        Watchlist wl = watchlists.save(new Watchlist("Кино на выходные", null, anna));
        members.save(new WatchlistMember(wl, anna, MemberRole.OWNER));
        em.flush();
        em.clear(); // сбрасываем кэш Hibernate, чтобы читать именно из БД

        assertThat(watchlists.findAllByMemberUserId(anna.getId()))
                .extracting(Watchlist::getName)
                .containsExactly("Кино на выходные");

        var member = members.findById(new WatchlistMemberId(wl.getId(), anna.getId()));
        assertThat(member).isPresent();
        assertThat(member.get().getRole()).isEqualTo(MemberRole.OWNER);
    }

    @Test
    void nonMemberDoesNotSeeTheList() {
        User anna = newUser("anna");
        User boris = newUser("boris");
        Watchlist wl = watchlists.save(new Watchlist("Только для Анны", null, anna));
        members.save(new WatchlistMember(wl, anna, MemberRole.OWNER));
        em.flush();
        em.clear();

        assertThat(watchlists.findAllByMemberUserId(boris.getId())).isEmpty();
        assertThat(members.findById(new WatchlistMemberId(wl.getId(), boris.getId()))).isEmpty();
    }

    @Test
    void sameTitleCannotBeAddedToListTwice() {
        User anna = newUser("anna");
        Watchlist wl = watchlists.save(new Watchlist("Фильмы", null, anna));
        Title matrix = newTitle(603, "Матрица");

        WatchlistItem item = items.saveAndFlush(new WatchlistItem(wl, matrix, anna));
        assertThat(item.getStatus()).isEqualTo(ItemStatus.PLANNED);
        assertThat(items.existsByWatchlist_IdAndTitle_Id(wl.getId(), matrix.getId())).isTrue();

        assertThatThrownBy(() -> items.saveAndFlush(new WatchlistItem(wl, matrix, anna)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void emailMustBeUnique() {
        newUser("anna");
        User duplicate = new User();
        duplicate.setEmail("anna@test.com");
        duplicate.setUsername("anna2");
        duplicate.setPasswordHash("hash");

        assertThatThrownBy(() -> users.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}