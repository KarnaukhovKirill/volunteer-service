package ru.kirill.storage;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.format.annotation.DateTimeFormat;
import ru.kirill.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "location")
@Getter
@Setter
@Builder
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_loc_id")
    private Location parentLocation;
    @Column(name = "create_date", nullable = false)
    @CreationTimestamp
    @DateTimeFormat(pattern = DateUtils.DATE_FORMAT)
    private LocalDateTime createDate;
    @Column(name = "location_kind", nullable = false)
    @Enumerated(EnumType.STRING)
    private LOCATIONKIND locationKind = LOCATIONKIND.PARENT;
    @Column(name = "update_date")
    @UpdateTimestamp
    @DateTimeFormat(pattern = DateUtils.DATE_FORMAT)
    private LocalDateTime updateDate;

    /**
     * Переопределяем equals и hashCode
     * https://jpa-buddy.com/blog/hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids/
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getEffectiveClass(this) != getEffectiveClass(o)) return false;
        return getId() != null && getId().equals(((Location) o).getId());
    }

    @Override
    public final int hashCode() {
        return getEffectiveClass(this).hashCode();
    }

    private static Class<?> getEffectiveClass(Object o) {
        return o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    }
}
