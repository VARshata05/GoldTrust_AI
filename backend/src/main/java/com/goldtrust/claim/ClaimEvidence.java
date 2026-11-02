package com.goldtrust.claim;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "claim_evidence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @Column(name = "evidence_type", nullable = false, length = 50)
    private String evidenceType; // POLICE_REPORT, DAMAGE_PHOTO, APPRAISAL_DOC, RECEIPT, CCTV, AFFIDAVIT

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean verified = false;

    @Column(name = "uploaded_at")
    @Builder.Default
    private Instant uploadedAt = Instant.now();
}

