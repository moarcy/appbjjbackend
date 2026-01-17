package bjjapp.repository;

import bjjapp.entity.SchoolOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolOwnerRepository extends JpaRepository<SchoolOwner, Long> {
}
