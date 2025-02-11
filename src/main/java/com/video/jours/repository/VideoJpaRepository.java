package com.video.jours.repository;

import com.video.jours.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoJpaRepository extends JpaRepository<Video, String> {
}
