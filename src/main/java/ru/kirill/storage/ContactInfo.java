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
@Table(name = "contract_info")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ContactInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "contact", nullable = false, unique = true)
    private String contact;
    @Column(name = "contract_type", nullable = false)
    private CONTACTTYPE contactType;
    @Column(name = "create_date", nullable = false)
    @CreationTimestamp
    @DateTimeFormat(pattern = DateUtils.DATE_FORMAT)
    private LocalDateTime createDate;
    @Column(name = "update_date")
    @UpdateTimestamp
    @DateTimeFormat(pattern = DateUtils.DATE_FORMAT)
    private LocalDateTime updateDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Volunteer volunteerId;

    /**
     * Переопределяем equals и hashCode
     * https://jpa-buddy.com/blog/hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids/
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getEffectiveClass(this) != getEffectiveClass(o)) return false;
        return getId() != null && getId().equals(((ContactInfo) o).getId());
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
