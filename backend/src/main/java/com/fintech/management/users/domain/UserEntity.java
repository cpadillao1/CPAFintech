package com.fintech.management.users.domain;

import com.fintech.management.branches.domain.BranchEntity;
import com.fintech.management.functionalities.domain.RoleEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"branch", "roles"}) // Evita bucles infinitos en logs
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Comparación segura para JPA
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include // El ID es lo único que define si un usuario es igual a otro
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 30)
    private String lastName;

    @Column(nullable = false, length = 30)
    private String login;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY) // Correcto: Evita traer la sucursal si no se usa
    @JoinColumn(name = "branch_id")
    private BranchEntity branch;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER) // Mantenlo así si los roles se cargan en cada Login
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();

}
