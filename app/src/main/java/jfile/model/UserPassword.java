package jfile.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users_passwords") 
@NoArgsConstructor 
@Getter
@AllArgsConstructor
public class UserPassword {

    @Id
    private Long of_uid;

    @OneToOne
    @MapsId
    @JoinColumn(name = "of_uid", nullable = false)
    private User correspondingUser;

    @Column(nullable = false, length = 128)
    private String hash;

     public void setCorrespondingUser(User user) {
        this.correspondingUser = user;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
