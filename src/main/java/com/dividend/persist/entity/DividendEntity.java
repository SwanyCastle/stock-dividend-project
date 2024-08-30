package com.dividend.persist.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "DIVIDEND")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = { "companyId", "dateTime" }
                )
        }
)
public class DividendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    private LocalDateTime dateTime;

    private String dividend;
}
