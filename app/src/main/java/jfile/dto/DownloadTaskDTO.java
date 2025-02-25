package jfile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadTaskDTO {
    private String downloadId;
    private Long ofUserId;
    private short downloadType;
    private String url;
    private short downloadStatus;
    private String metadata;
}