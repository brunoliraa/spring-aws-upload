package com.br.springawsuploadimage.repository;

import com.br.springawsuploadimage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
