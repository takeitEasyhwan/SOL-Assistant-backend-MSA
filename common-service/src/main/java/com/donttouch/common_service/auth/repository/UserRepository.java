package com.donttouch.common_service.auth.repository;


import com.donttouch.common_service.auth.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
