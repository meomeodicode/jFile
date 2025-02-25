package jfile.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jfile.model.DownloadTask;

public interface DownloadRepository extends JpaRepository<DownloadTask, String> {
    
    @Query("SELECT COUNT(d) FROM DownloadTask d WHERE d.user.uid = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT d FROM DownloadTask d WHERE d.user.uid = :userId")
    List<DownloadTask> findByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE DownloadTask d SET d.downloadStatus = :status WHERE d.downloadId = :taskId")
    void updateStatus(@Param("taskId") String taskId, @Param("status") short status);
    
    @Modifying
    @Query("DELETE FROM DownloadTask d WHERE d.user.uid = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(d) FROM DownloadTask d WHERE d.downloadStatus = :status")
    long countByStatus(@Param("status") short status);
}