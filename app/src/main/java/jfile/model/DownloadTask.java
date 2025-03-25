package jfile.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "download_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DownloadTask {
    @Id
    @Column(length = 256)
    private String downloadId;

    @ManyToOne
    @JoinColumn(name = "download_of_uid", nullable = false)
    private User user;

    @Column(nullable = false)
    private Short downloadType = 1;  // Default value

    @Column(nullable = false, length = 256)
    private String url;

    @Column(nullable = false)
    private Short downloadStatus = 0;  // Default value

    @Column(columnDefinition = "TEXT")
    private String metadata;
}

