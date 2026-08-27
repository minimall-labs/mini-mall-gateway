package com.minimall.gateway.config;

import com.minimall.gateway.workbench.WorkbenchModuleProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WorkbenchModuleProperties.class)
public class WorkbenchConfig {}
