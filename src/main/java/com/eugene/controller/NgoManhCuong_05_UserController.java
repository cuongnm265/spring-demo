package com.eugene.controller;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.eugene.domain.NgoManhCuong_05_User;
import com.eugene.group.NgoManhCuong_05_ChangePassword;
import com.eugene.repository.NgoManhCuong_05_UserRepository;
import com.eugene.service.NgoManhCuong_05_CustomUserDetails;
import com.eugene.validator.NgoManhCuong_05_PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Ngô Mạnh Cường on 12/12/2016.
 */

/*Controller cho người dùng*/
@Controller
public class NgoManhCuong_05_UserController {

  private final NgoManhCuong_05_UserRepository userRepository;
  private final NgoManhCuong_05_PasswordValidator passwordValidator;

  @Autowired
  public NgoManhCuong_05_UserController(NgoManhCuong_05_UserRepository userRepository, NgoManhCuong_05_PasswordValidator passwordValidator) {
    this.userRepository = userRepository;
    this.passwordValidator = passwordValidator;
  }

  /*Gọi trang sửa thông tin cá nhân*/
  @RequestMapping("/profile")
  public String profile(Model model) {
    NgoManhCuong_05_CustomUserDetails userDetails = (NgoManhCuong_05_CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long userId = userDetails.getUserId();
    NgoManhCuong_05_User user = userRepository.findOne(userId);
    model.addAttribute("user", user);
    return "users/profile";
  }

  /*Sửa thông tin cá nhân, tương tự khóa học, up hình lên AWS S3*/
  @RequestMapping(value = "/profile/update", method = RequestMethod.POST)
  public String updateUser(@Valid NgoManhCuong_05_User user,
                           BindingResult result,
                           @RequestParam("file") MultipartFile file,
                           @RequestParam("name") String name,
                           RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
      return "users/profile";
    }

    AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
    AmazonS3 s3client = new AmazonS3Client(credentials);
    String bucketName = "cuongngo-lms";
    boolean exist = false;
    for (Bucket bucket : s3client.listBuckets()) {
      if (bucketName.equals(bucket.getName())) {
        exist = true;
      }
    }
    if (!exist) {
      s3client.createBucket(bucketName);
    }
    try {
      InputStream is = file.getInputStream();
      if (is.available() > 0) {
        s3client.putObject(new PutObjectRequest(bucketName, name, is, new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead));
        S3Object s3Object = s3client.getObject(new GetObjectRequest(bucketName, name));
        user.setUserImageUrl(s3Object.getObjectContent().getHttpRequest().getURI().toString());
      } else if (user.getUserImageUrl() == null) {
        user.setUserImageUrl("https://cuongngo-lms.s3.amazonaws.com/no-image.jpg");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    SecurityContext context = SecurityContextHolder.getContext();
    Authentication auth = context.getAuthentication();
    NgoManhCuong_05_CustomUserDetails userDetails = (NgoManhCuong_05_CustomUserDetails) auth.getPrincipal();
    userDetails.setUserImageUrl(user.getUserImageUrl());
    redirectAttributes.addFlashAttribute("message", "Your profile was successfully updated!!");
    user.setPassword(user.getPassword());
    userRepository.setUserInfoById(user.getEmail(), user.getFullName(), user.getPhoneNumber(), user.getBio(), user.getUserImageUrl(), user.getUserId());
    return "redirect:/profile";
  }

  /*Gọi trang sửa mật khẩu*/
  @RequestMapping("/password")
  public String password(Model model) {
    NgoManhCuong_05_CustomUserDetails userDetails = (NgoManhCuong_05_CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long userId = userDetails.getUserId();
    NgoManhCuong_05_User user = userRepository.findOne(userId);
    user.setPassword(user.getPassword());
    model.addAttribute("user", user);
    return "users/password";
  }

  /*Sửa mật khẩu*/
  @RequestMapping(value = "password/update", method = RequestMethod.POST)
  public String updatePassword(@ModelAttribute("user") @Validated({NgoManhCuong_05_ChangePassword.class}) NgoManhCuong_05_User user,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
    passwordValidator.validate(user, result);
    if (result.hasErrors()) {

      return "users/password";
    }
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String hashedPassword = passwordEncoder.encode(user.getNewPassword());
    NgoManhCuong_05_CustomUserDetails userDetails = (NgoManhCuong_05_CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    userDetails.setPassword(hashedPassword);
    userRepository.changeUserPassword(hashedPassword, user.getUserId());
    redirectAttributes.addFlashAttribute("message", "Your password was successfully updated!");
    return "redirect:/password";
  }
}
