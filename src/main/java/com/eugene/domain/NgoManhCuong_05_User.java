package com.eugene.domain;

/**
 * Created by Ngô Mạnh Cường on 11/26/2016.
 */

import com.eugene.group.NgoManhCuong_05_ChangePassword;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * Entity cho bài học
 * có các thuộc tính như dưới, có liên kết nhiều 1 với khóa học
 * có liên kết với quyền người dùng
 * có chức năng kiểm tra hợp lệ
 */

@Entity
@Table(name = "users")
public class NgoManhCuong_05_User implements Serializable {
  private static final long serialVersionUID = 1;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "user_id")
  private Long userId;
  @NotEmpty
  @Column(name = "username", nullable = false, unique = true)
  private String username;
  @Column(name = "password", nullable = false)
  @Size(min = 6)
  private String password;
  @NotEmpty(message = "Email should not be empty")
  @Column(name = "email", nullable = false, unique = true)
  private String email;
  @Column(name = "enabled", nullable = false)
  private boolean enabled = true;
  @Column(name = "image_url")
  private String userImageUrl;
  @ManyToOne(optional = false)
  @JoinColumn(name = "roleId", referencedColumnName = "role_id", insertable = false, updatable = false)
  private NgoManhCuong_05_UserRole userRole;
  @Column(name = "full_name")
  private String fullName;
  @Column(name = "phone_number")
  private String phoneNumber;
  @Column(name = "bio")
  private String bio;

  @Transient
  @NotEmpty(message = "New password may not be empty", groups = NgoManhCuong_05_ChangePassword.class)
  private String newPassword;
  @NotEmpty(message = "Confirm password may not be empty", groups = NgoManhCuong_05_ChangePassword.class)
  @Transient
  private String retypePassword;
  @Transient
  @NotEmpty(message = "Old password may not be empty", groups = NgoManhCuong_05_ChangePassword.class)
  private String oldPassword;

  public NgoManhCuong_05_User() {
  }

  public NgoManhCuong_05_User(NgoManhCuong_05_User user) {
    this.userId = user.userId;
    this.username = user.username;
    this.email = user.email;
    this.password = user.password;
    this.enabled = user.enabled;
    this.userImageUrl = user.userImageUrl;
    this.fullName = user.fullName;
    this.phoneNumber = user.phoneNumber;
    this.bio = user.bio;
    this.retypePassword = user.retypePassword;
    this.newPassword = user.newPassword;
    this.oldPassword = user.oldPassword;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public NgoManhCuong_05_UserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(NgoManhCuong_05_UserRole userRole) {
    this.userRole = userRole;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getUserImageUrl() {
    return userImageUrl;
  }

  public void setUserImageUrl(String userImageUrl) {
    this.userImageUrl = userImageUrl;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getRetypePassword() {
    return retypePassword;
  }

  public void setRetypePassword(String retypePassword) {
    this.retypePassword = retypePassword;
  }

  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }
}
