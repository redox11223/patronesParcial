package com.parcial.test.reports.repository;

import com.parcial.test.reports.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepo extends JpaRepository<Report,Long> {
}
