package com.rahul.projectevaulation.shared.otp.repository;

import com.rahul.projectevaulation.shared.otp.entity.Otp;
import com.rahul.projectevaulation.shared.otp.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByRecipientAndPurposeAndActiveTrue(String recipient, OtpPurpose purpose);

    @Modifying
    @Query("UPDATE Otp o SET o.active = false WHERE o.recipient = :recipient AND o.purpose = :purpose AND o.active = true")
    void invalidateActiveOtps(@Param("recipient") String recipient, @Param("purpose") OtpPurpose purpose);
}
