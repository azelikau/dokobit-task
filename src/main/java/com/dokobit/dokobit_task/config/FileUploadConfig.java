package com.dokobit.dokobit_task.config;

import lombok.Setter;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
@ConfigurationProperties("file-upload")
@Setter
public class FileUploadConfig {

  private DataSize requestMaxSize;

  @Bean
  public ServletFileUpload fileUpload() {
    ServletFileUpload servletFileUpload = new ServletFileUpload();

    servletFileUpload.setSizeMax(requestMaxSize.toBytes());

    return servletFileUpload;
  }

}
