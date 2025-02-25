package jfile.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jfile.dto.UserPasswordDTO;
import jfile.model.User;
import jfile.model.UserPassword;

@Repository
public interface UserPasswordRepository extends JpaRepository<UserPassword, Long> {

    @Query("SELECT u FROM UserPassword u WHERE u.correspondingUser.uid = :uid")
    Optional<UserPassword> findHashByUserId(@Param("uid") Long uid);
}