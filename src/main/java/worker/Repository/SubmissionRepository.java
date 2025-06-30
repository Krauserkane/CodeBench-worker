package worker.Repository;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import worker.Entity.Submission;
import worker.Entity.SubmissionId;


@Repository
public interface SubmissionRepository extends JpaRepository<Submission, SubmissionId> {
}
