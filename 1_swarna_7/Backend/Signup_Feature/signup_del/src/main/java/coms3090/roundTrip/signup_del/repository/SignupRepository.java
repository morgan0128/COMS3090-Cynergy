package coms3090.roundTrip.signup_del.repository;

import coms3090.roundTrip.signup_del.model.Signup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author tanya
 * Repository for SB -> DB communication
 *
 * Upcoming modifications: create queries for findByEmail, findByUsername for easy login validation
 */
@Repository
public interface SignupRepository extends JpaRepository<Signup, Integer> {
}