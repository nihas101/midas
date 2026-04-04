package de.nihas101.midas.backup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "backup_status")
public class BackupStatusEntity {

    @Id
    private Integer id;

    @Column(name = "last_success_at")
    private LocalDateTime lastSuccessAt;
}
