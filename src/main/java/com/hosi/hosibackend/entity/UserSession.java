package com.hosi.hosibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "user_sessions")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private String userAgent;
    private String ipAddress;
    private boolean isMobile;
    @ColumnDefault("0")
    private boolean isRevoked;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
