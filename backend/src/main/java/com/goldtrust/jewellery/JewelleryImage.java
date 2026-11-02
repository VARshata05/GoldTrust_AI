package com.goldtrust.jewellery;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "jewellery_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JewelleryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jewellery_asset_id", nullable = false)
    private JewelleryAsset jewelleryAsset;

    @Column(name = "version_number", nullable = false)
    @Builder.Default
    private Integer versionNumber = 1;

    @Column(name = "image_type", length = 50)
    @Builder.Default
    private String imageType = "PRIMARY"; // PRIMARY, FRONT, BACK, HALLMARK, DAMAGE

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "ai_features_extracted")
    @Builder.Default
    private Boolean aiFeaturesExtracted = false;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}

